package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.*;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.api.exception.CategoriesNotFoundException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.exception.SearchNotConfiguredException;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;
import ua.edu.cdu.vu.price.aggregator.api.mapper.ProductsResponseMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static ua.edu.cdu.vu.price.aggregator.api.util.CommonConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {

    private static final int FIRST_PAGE = 1;
    private static final String KEY_TEMPLATE = "key_%d";
    private static final String VALUE_TEMPLATE = "value_%d";

    private final ExecutorService taskExecutor;
    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;
    private final ProductsResponseMapper productsResponseMapper;
    private final ProductCategoryGenerator productCategoryGenerator;

    public ProductsResponse search(String query) {
        var futures = marketplaceConfigDao.loadAll().values().stream()
                .filter(marketplaceConfig -> nonNull(marketplaceConfig.search()))
                .map(marketplaceConfig -> CompletableFuture.supplyAsync(() -> search(marketplaceConfig, query), taskExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        return Arrays.stream(futures)
                .map(CompletableFuture::join)
                .map(response -> (ProductsResponse) response)
                .reduce(ProductsResponse.empty(), ProductsService::merge);
    }

    public ProductsResponse search(String marketplace, String query) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        if (isNull(marketplaceConfig.search())) {
            throw new SearchNotConfiguredException(marketplace);
        }

        return search(marketplaceConfig, query);
    }

    private ProductsResponse search(MarketplaceConfig marketplaceConfig, String query) {

        SearchSelectorConfig selectorConfig = marketplaceConfig.search();

        String modifiedQuery = query;
        if (selectorConfig.aiEnabled()) {
            Category category = productCategoryGenerator.generate(query);
            modifiedQuery = String.join(SPACE, category.name(), query);
        }

        Map<String, Object> arguments = Map.of(QUERY, modifiedQuery);

        log.debug("Searching for products on marketplace: {} with query: {}, modified query: {}", marketplaceConfig.marketplace(), query, modifiedQuery);

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
                selectorConfig.linkSelector(), selectorConfig.imageSelector(), selectorConfig.priceImgSelector(), selectorConfig.descriptionImgSelector(), selectorConfig.titleSelector(), selectorConfig.priceSelector());
        var results = dslEvaluationService.evaluate(request).getValues();

        return productsResponseMapper.convertToResponse(marketplaceConfig.marketplace(), (List<String>) results.get(0), (List<String>) results.get(1),
                (List<String>) results.get(2), (List<String>) results.get(3), (List<String>) results.get(4), (List<String>) results.get(5));
    }

    public ProductsResponse getProducts(String marketplace, String category, Map<String, String> subcategories, ProductsRequest productsRequest, int page) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        ProductsSelectorConfig selectorConfig = marketplaceConfig.products();

        var filters = extractFilters(productsRequest, extractLastSubcategory(subcategories));

        var arguments = createArgumentsMap(category, subcategories, productsRequest, page);
        arguments = enrichArguments(arguments, filters, KEY_TEMPLATE, Map.Entry::getKey);
        arguments = enrichArguments(arguments, filters, VALUE_TEMPLATE, Map.Entry::getValue);

        return scrapeProducts(marketplaceConfig, selectorConfig, filters, arguments, e -> new CategoriesNotFoundException(marketplace, page, category, subcategories.values(), e),
                selectorConfig.self().linkSelector(), selectorConfig.self().imageSelector(), selectorConfig.self().priceImgSelector(), selectorConfig.self().descriptionImgSelector(),
                selectorConfig.self().titleSelector(), selectorConfig.self().priceSelector(), selectorConfig.self().pagesCountSelector()
        );
    }

    private static Map<String, Object> createArgumentsMap(String category, Map<String, String> subcategories, ProductsRequest productsRequest, int page) {
        Map<String, Object> arguments = new HashMap<>(subcategories);
        arguments.put(CATEGORY, category);
        arguments.put(MIN_PRICE, productsRequest.getMinPrice());
        arguments.put(MAX_PRICE, productsRequest.getMaxPrice());
        arguments.put(PAGE, page);

        return arguments;
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
            int pagesCount = Optional.ofNullable((String) results.get(6))
                    .map(Integer::parseInt)
                    .orElse(FIRST_PAGE);

            return productsResponseMapper.convertToResponse(marketplaceConfig.marketplace(), (List<String>) results.get(0), (List<String>) results.get(1),
                    (List<String>) results.get(2), (List<String>) results.get(3), (List<String>) results.get(4), (List<String>) results.get(5), pagesCount);
        } catch (DslExecutionException e) {
            throw exceptionMapper.apply(e);
        }
    }

    private static String extractLastSubcategory(Map<String, String> subcategories) {
        return subcategories.entrySet().stream()
                .map(entry -> Map.entry(Integer.parseInt(StringUtils.substringAfter(entry.getKey(), SUBCATEGORY)), entry.getValue()))
                .max(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .orElseThrow();
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
                .mapToObj(index -> templateConfig.template().formatted(index, index))
                .toList();
        var actions = new LinkedList<>(request.getActions());
        actions.addAll(templateConfig.index(), dynamicActions);

        return request.withActions(actions);
    }

    private static Map<String, Object> enrichArguments(Map<String, Object> initialArguments, List<Map.Entry<String, String>> filters, String template, Function<Map.Entry<String, String>, String> filterExtractor) {
        return IntStream.range(0, filters.size())
                .boxed()
                .collect(Collectors.toMap(template::formatted, i -> filterExtractor.apply(filters.get(i)), (v1, v2) -> v2, () -> initialArguments));
    }
}
