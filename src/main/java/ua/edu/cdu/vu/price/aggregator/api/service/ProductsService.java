package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.api.exception.CategoriesNotFoundException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;
import ua.edu.cdu.vu.price.aggregator.api.mapper.ProductsResponseMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private static final int FIRST_PAGE = 1;
    private static final String KEY_TEMPLATE = "key_%d";
    private static final String VALUE_TEMPLATE = "value_%d";

    private static final String CATEGORY = "category";
    private static final String SUBCATEGORY_1 = "subcategory1";
    private static final String SUBCATEGORY_2 = "subcategory2";
    private static final String MIN_PRICE = "minPrice";
    private static final String MAX_PRICE = "maxPrice";
    private static final String PAGE = "page";
    private static final String QUERY = "query";

    private final ExecutorService taskExecutor;
    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;
    private final ProductsResponseMapper productsResponseMapper;

    public ProductsResponse search(String query) {
        var futures = marketplaceConfigDao.getAllMarketplaces().stream()
                .map(marketplaceConfigDao::load)
                .filter(marketplaceConfig -> nonNull(marketplaceConfig.search()))
                .map(marketplaceConfig -> CompletableFuture.supplyAsync(() -> search(marketplaceConfig, query), taskExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        return Arrays.stream(futures)
                .map(CompletableFuture::join)
                .map(response -> (ProductsResponse) response)
                .reduce(ProductsResponse.empty(), ProductsService::merge);
    }

    private ProductsResponse search(MarketplaceConfig marketplaceConfig, String query) {

        SearchSelectorConfig selectorConfig = marketplaceConfig.search();

        Map<String, Object> arguments = Map.of(QUERY, query);

        return scrapeProducts(marketplaceConfig, selectorConfig, arguments);
    }

    private static ProductsResponse merge(ProductsResponse response1, ProductsResponse response2) {
        return ProductsResponse.builder()
                .products(Stream.concat(response1.getProducts().stream(), response2.getProducts().stream()).toList())
                .pagesCount(FIRST_PAGE)
                .build();
    }

    @SuppressWarnings("unchecked")
    private ProductsResponse scrapeProducts(MarketplaceConfig marketplaceConfig,
                                            SearchSelectorConfig selectorConfig,
                                            Map<String, Object> arguments) {
        DslEvaluationRequest request = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), selectorConfig, arguments,
                selectorConfig.linkSelector(), selectorConfig.imageSelector(), selectorConfig.priceSelector(), selectorConfig.descriptionSelector());
        var results = dslEvaluationService.evaluate(request).getValues();

        return productsResponseMapper.convertToResponse((List<String>) results.get(0), (List<String>) results.get(1), (List<String>) results.get(2), (List<String>) results.get(3));
    }

    public ProductsResponse getProducts(String marketplace, String category, String subcategory1, String subcategory2, ProductsRequest productsRequest, int page) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        ProductsSelectorConfig selectorConfig = marketplaceConfig.products();

        List<Map.Entry<String, String>> filters = extractFilters(productsRequest, subcategory2);

        var arguments = createArgumentsMap(category, subcategory1, subcategory2, productsRequest, page);
        arguments = enrichArguments(arguments, filters, KEY_TEMPLATE, Map.Entry::getKey);
        var allArguments = enrichArguments(arguments, filters, VALUE_TEMPLATE, Map.Entry::getValue);

        return scrapeProducts(marketplaceConfig, selectorConfig, filters, allArguments, e -> new CategoriesNotFoundException(marketplace, page, e, category, subcategory1, subcategory2),
                selectorConfig.self().linkSelector(), selectorConfig.self().imageSelector(), selectorConfig.self().priceSelector(), selectorConfig.self().descriptionSelector(), selectorConfig.self().pagesCountSelector());
    }

    private static Map<String, Object> createArgumentsMap(String category, String subcategory1, String subcategory2, ProductsRequest productsRequest, int page) {
        return new HashMap<>() {{
            put(CATEGORY, category);
            put(SUBCATEGORY_1, subcategory1);
            put(SUBCATEGORY_2, subcategory2);
            put(MIN_PRICE, productsRequest.getMinPrice());
            put(MAX_PRICE, productsRequest.getMaxPrice());
            put(PAGE, page);
        }};
    }

    @SuppressWarnings("unchecked")
    private ProductsResponse scrapeProducts(MarketplaceConfig marketplaceConfig,
                                            ProductsSelectorConfig selectorConfig,
                                            List<Map.Entry<String, String>> filters,
                                            Map<String, Object> arguments,
                                            Function<DslExecutionException, RuntimeException> exceptionMapper,
                                            String... selectors) {
        DslEvaluationRequest rawRequest = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), selectorConfig, arguments, selectors);
        DslEvaluationRequest request = enrichRequestWithFilterActions(rawRequest, marketplaceConfig.products().filters(), filters.size());

        try {
            List<Object> results = dslEvaluationService.evaluate(request).getValues();
            int pagesCount = Optional.ofNullable((String) results.get(4))
                    .map(Integer::parseInt)
                    .orElse(FIRST_PAGE);

            return productsResponseMapper.convertToResponse((List<String>) results.get(0), (List<String>) results.get(1), (List<String>) results.get(2), (List<String>) results.get(3), pagesCount);
        } catch (DslExecutionException e) {
            throw exceptionMapper.apply(e);
        }
    }

    private static List<Map.Entry<String, String>> extractFilters(ProductsRequest request, String subcategory2) {
        return Stream.ofNullable(request.getFilters())
                .flatMap(List::stream)
                .flatMap(filter -> filter.getValues().stream()
                        .map(value -> Map.entry(filter.getKey(), value)))
                .distinct()
                .filter(entry -> !entry.getValue().equals(subcategory2))
                .toList();
    }

    private static DslEvaluationRequest enrichRequestWithFilterActions(DslEvaluationRequest request, TemplateConfig templateConfig, int filtersCount) {
        var dynamicActions = IntStream.range(0, filtersCount)
                .mapToObj(index -> templateConfig.template().formatted(index, index));
        var actions = Stream.of(
                        request.getActions().subList(0, request.getActions().size() - 2).stream(),
                        dynamicActions,
                        request.getActions().subList(request.getActions().size() - 3, request.getActions().size()).stream())
                .flatMap(Function.identity())
                .toList();

        return request.withActions(actions);
    }

    private static Map<String, Object> enrichArguments(Map<String, Object> initialArguments, List<Map.Entry<String, String>> filters, String template, Function<Map.Entry<String, String>, String> filterExtractor) {
        return IntStream.range(0, filters.size())
                .boxed()
                .collect(Collectors.toMap(template::formatted, i -> filterExtractor.apply(filters.get(i)), (v1, v2) -> v2, () -> initialArguments));
    }
}
