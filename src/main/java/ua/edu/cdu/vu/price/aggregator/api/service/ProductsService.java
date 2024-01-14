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
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;
import ua.edu.cdu.vu.price.aggregator.api.mapper.ProductsResponseMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private static final String FILTER_TEMPLATE = "filter_%d";

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
        var filterValues = extractFilterValues(productsRequest.getFilters());
        DslEvaluationRequest request = enrichRequestWithFilterActions(rawRequest, marketplaceConfig.products().filters(), filterValues.size());

        var initialArguments = new HashMap<String, Object>() {{
            put(CATEGORY, category);
            put(SUBCATEGORY_1, subcategory1);
            put(SUBCATEGORY_2, subcategory2);
            put(MIN_PRICE, productsRequest.getMinPrice());
            put(MAX_PRICE, productsRequest.getMaxPrice());
            put(PAGE, page);
        }};
        var arguments = createArguments(initialArguments, filterValues);

        var rawProducts = dslEvaluationService.<List<Pair<String, String>>>evaluate(request.withArguments(arguments)).getValue();

        return productsResponseMapper.convertToResponse(rawProducts);
    }

    private List<String> extractFilterValues(List<ProductsRequest.Filter> filters) {
        return Stream.ofNullable(filters)
                .flatMap(List::stream)
                .map(ProductsRequest.Filter::getValues)
                .flatMap(List::stream)
                .toList();
    }

    private DslEvaluationRequest enrichRequestWithFilterActions(DslEvaluationRequest request, TemplateConfig templateConfig, int filtersCount) {
        var dynamicActions = IntStream.range(0, filtersCount)
                .mapToObj(index -> templateConfig.template().formatted(index));
        return request.withActions(Stream.concat(request.getActions().stream(), dynamicActions).toList());
    }

    private Map<String, Object> createArguments(Map<String, Object> initialArguments, List<String> filterValues) {
        return IntStream.range(0, filterValues.size())
                .boxed()
                .collect(Collectors.toMap(FILTER_TEMPLATE::formatted, filterValues::get, (v1, v2) -> v2, () -> initialArguments));
    }
}
