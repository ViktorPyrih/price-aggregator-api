package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.TemplateConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.api.exception.CategoryNotFoundException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;
import ua.edu.cdu.vu.price.aggregator.api.mapper.ProductsResponseMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private static final String KEY_TEMPLATE = "key_%d";
    private static final String VALUE_TEMPLATE = "value_%d";

    private static final String CATEGORY = "category";
    private static final String SUBCATEGORY_1 = "subcategory1";
    private static final String SUBCATEGORY_2 = "subcategory2";
    private static final String MIN_PRICE = "minPrice";
    private static final String MAX_PRICE = "maxPrice";
    private static final String PAGE = "page";

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;
    private final ProductsResponseMapper productsResponseMapper;

    public ProductsResponse getProducts(String marketplace, String category, String subcategory1, String subcategory2, ProductsRequest productsRequest, int page) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);

        DslEvaluationRequest rawRequest = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), marketplaceConfig.products().self());
        List<Map.Entry<String, String>> filters = extractFilters(productsRequest);
        DslEvaluationRequest request = enrichRequestWithFilterActions(rawRequest, marketplaceConfig.products().filters(), filters.size());

        Map<String, Object> arguments = new HashMap<>() {{
            put(CATEGORY, category);
            put(SUBCATEGORY_1, subcategory1);
            put(SUBCATEGORY_2, subcategory2);
            put(MIN_PRICE, productsRequest.getMinPrice());
            put(MAX_PRICE, productsRequest.getMaxPrice());
            put(PAGE, page);
        }};
        arguments = enrichArguments(arguments, filters, KEY_TEMPLATE, Map.Entry::getKey);
        arguments = enrichArguments(arguments, filters, VALUE_TEMPLATE, Map.Entry::getValue);

        try {
            var rawProducts = dslEvaluationService.<List<Pair<String, String>>>evaluate(request.withArguments(arguments)).getValue();
            return productsResponseMapper.convertToResponse(rawProducts);
        } catch (DslExecutionException e) {
            throw new CategoryNotFoundException(marketplace, e, category, subcategory1, subcategory2);
        }
    }

    private List<Map.Entry<String, String>> extractFilters(ProductsRequest request) {
        return Stream.ofNullable(request.getFilters())
                .flatMap(List::stream)
                .flatMap(filter -> filter.getValues().stream()
                        .map(value -> Map.entry(filter.getKey(), value)))
                .distinct()
                .toList();
    }

    private DslEvaluationRequest enrichRequestWithFilterActions(DslEvaluationRequest request, TemplateConfig templateConfig, int filtersCount) {
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

    private Map<String, Object> enrichArguments(Map<String, Object> initialArguments, List<Map.Entry<String, String>> filters, String template, Function<Map.Entry<String, String>, String> filterExtractor) {
        return IntStream.range(0, filters.size())
                .boxed()
                .collect(Collectors.toMap(template::formatted, i -> filterExtractor.apply(filters.get(i)), (v1, v2) -> v2, () -> initialArguments));
    }
}
