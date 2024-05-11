package ua.edu.cdu.vu.price.aggregator.api.service;

import com.google.common.util.concurrent.MoreExecutors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.ProductsSelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.SearchSelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.TemplateConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationResponse;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;
import ua.edu.cdu.vu.price.aggregator.api.exception.CategoriesNotFoundException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.exception.SearchNotConfiguredException;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;
import ua.edu.cdu.vu.price.aggregator.api.mapper.ProductsResponseMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@MockitoSettings
class ProductsServiceTest {

    private static final String MARKETPLACE1 = "marketplace1";
    private static final String MARKETPLACE2 = "marketplace2";
    private static final String MARKETPLACE3 = "marketplace3";
    private static final String SOME_STRING = "someString";
    private static final String SOME_SUBCATEGORY = "subcategory";
    private static final String QUERY = "query";
    private static final String URL = "example@example.com";
    private static final String SOME_DSL = "dsl";
    private static final String SUBCATEGORY_1 = "subcategory1";
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 100.0;

    @Mock
    private MarketplaceConfigDao marketplaceConfigDao;

    @Mock
    private DslEvaluationService dslEvaluationService;

    private final ExecutorService taskExecutor = MoreExecutors.newDirectExecutorService();

    private final DslEvaluationRequestMapper dslEvaluationRequestMapper = Mappers.getMapper(DslEvaluationRequestMapper.class);

    private final ProductsResponseMapper productsResponseMapper = Mappers.getMapper(ProductsResponseMapper.class);

    private ProductsService unit;

    @BeforeEach
    void setUp() {
        unit = new ProductsService(taskExecutor, marketplaceConfigDao, dslEvaluationService, dslEvaluationRequestMapper, productsResponseMapper);
    }

    @Test
    void search_shouldSearchByQueryIgnoringNotConfiguredMarketplaces() {
        when(marketplaceConfigDao.loadAll()).thenReturn(
                Map.of(
                        MARKETPLACE1, buildMarketplaceConfig(MARKETPLACE1, buildSearchSelectorConfig()),
                        MARKETPLACE2, buildMarketplaceConfig(MARKETPLACE2, buildSearchSelectorConfig()),
                        MARKETPLACE3, buildMarketplaceConfig()
                )
        );
        when(dslEvaluationService.evaluate(any())).thenReturn(buildDslEvaluationResponse());

        ProductsResponse response = unit.search(QUERY);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(buildProductsResponse(1, MARKETPLACE1, MARKETPLACE2));

        verify(dslEvaluationService, times(2)).evaluate(buildDslEvaluationRequest());
    }

    @Test
    void search_shouldSearchByQueryAndMarketplace() {
        when(marketplaceConfigDao.load(MARKETPLACE1)).thenReturn(buildMarketplaceConfig(MARKETPLACE1, buildSearchSelectorConfig()));
        when(dslEvaluationService.evaluate(any())).thenReturn(buildDslEvaluationResponse());

        ProductsResponse response = unit.search(MARKETPLACE1, QUERY);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(buildProductsResponse(1, MARKETPLACE1));

        verify(dslEvaluationService).evaluate(buildDslEvaluationRequest());
    }

    @Test
    void search_whenSearchConfigIsNull_shouldThrowSearchNotConfiguredException() {
        when(marketplaceConfigDao.load(MARKETPLACE3)).thenReturn(buildMarketplaceConfig());

        assertThatExceptionOfType(SearchNotConfiguredException.class)
                .isThrownBy(() -> unit.search(MARKETPLACE3, QUERY))
                .withMessage("Search is not configured for the marketplace: " + MARKETPLACE3);

        verify(dslEvaluationService, never()).evaluate(any());
    }

    @Test
    void getProducts_shouldIgnoreSubcategoryLikeFilter() {
        when(marketplaceConfigDao.load(MARKETPLACE1)).thenReturn(buildMarketplaceConfig());
        when(dslEvaluationService.evaluate(any())).thenReturn(buildDslEvaluationResponse());

        Map<String, String> subcategories = Map.of(SUBCATEGORY_1, SOME_SUBCATEGORY);
        ProductsResponse response = unit.getProducts(MARKETPLACE1, SOME_STRING, subcategories, buildProductsRequest(), 1);

        assertThat(response).isEqualTo(buildProductsResponse(3, MARKETPLACE3));

        verify(dslEvaluationService).evaluate(buildDslEvaluationRequest2());
    }

    @Test
    void getProducts_whenDslExecutionExceptionIsThrown_shouldThrowCategoriesNotFoundException() {
        when(marketplaceConfigDao.load(MARKETPLACE1)).thenReturn(buildMarketplaceConfig());
        when(dslEvaluationService.evaluate(any())).thenThrow(DslExecutionException.class);

        Map<String, String> subcategories = Map.of(SUBCATEGORY_1, SOME_SUBCATEGORY);

        assertThatExceptionOfType(CategoriesNotFoundException.class)
                .isThrownBy(() -> unit.getProducts(MARKETPLACE1, SOME_STRING, subcategories, buildProductsRequest(), 1))
                .withMessage("Categories combination: [someString, subcategory] not found on marketplace: marketplace1 for page: 1");

        verify(dslEvaluationService).evaluate(buildDslEvaluationRequest2());
    }

    private static MarketplaceConfig buildMarketplaceConfig() {
        return buildMarketplaceConfig(MARKETPLACE3, null);
    }

    private static MarketplaceConfig buildMarketplaceConfig(String marketplace, SearchSelectorConfig searchSelectorConfig) {
        return MarketplaceConfig.builder()
                .marketplace(marketplace)
                .url(URL)
                .search(searchSelectorConfig)
                .products(buildProductsSelectorConfig())
                .build();
    }

    private static SearchSelectorConfig buildSearchSelectorConfig() {
        return SearchSelectorConfig.builder()
                .titleSelector(SOME_DSL)
                .imageSelector(SOME_DSL)
                .linkSelector(SOME_DSL)
                .priceSelector(SOME_DSL)
                .priceImgSelector(SOME_DSL)
                .descriptionImgSelector(SOME_DSL)
                .build();
    }

    private static ProductsSelectorConfig buildProductsSelectorConfig() {
        return ProductsSelectorConfig.builder()
                .filters(new TemplateConfig(SOME_DSL, 1))
                .self(ProductsSelectorConfig.SelectorConfig.builder()
                        .actions(Collections.nCopies(2, SOME_DSL))
                        .titleSelector(SOME_DSL)
                        .imageSelector(SOME_DSL)
                        .linkSelector(SOME_DSL)
                        .priceSelector(SOME_DSL)
                        .priceImgSelector(SOME_DSL)
                        .descriptionImgSelector(SOME_DSL)
                        .pagesCountSelector(SOME_DSL)
                        .build())
                .build();
    }

    private static DslEvaluationRequest buildDslEvaluationRequest() {
        return DslEvaluationRequest.builder()
                .target(new DslEvaluationRequest.Target(URL))
                .arguments(Map.of(QUERY, QUERY))
                .expressions(Collections.nCopies(6, SOME_DSL))
                .build();
    }

    private static DslEvaluationRequest buildDslEvaluationRequest2() {
        return DslEvaluationRequest.builder()
                .target(new DslEvaluationRequest.Target(URL))
                .arguments(Map.of(
                        "key_0", SOME_STRING,
                        "maxPrice", MAX_PRICE,
                        "page", 1,
                        "value_0", SOME_STRING,
                        "category", SOME_STRING,
                        SUBCATEGORY_1, SOME_SUBCATEGORY,
                        "minPrice", MIN_PRICE
                ))
                .actions(Collections.nCopies(3, SOME_DSL))
                .expressions(Collections.nCopies(7, SOME_DSL))
                .build();
    }

    private static DslEvaluationResponse buildDslEvaluationResponse() {
        List<String> value = List.of(SOME_STRING);
        return DslEvaluationResponse.builder()
                .values(List.of(value, value, value, value, value, value, "3"))
                .build();
    }

    private static ProductsResponse buildProductsResponse(int pagesCount, String... marketplaces) {
        return ProductsResponse.builder()
                .products(Arrays.stream(marketplaces)
                        .map(ProductsServiceTest::buildProduct)
                        .toList())
                .pagesCount(pagesCount)
                .build();
    }

    private static ProductsResponse.Product buildProduct(String marketplace) {
        return ProductsResponse.Product.builder()
                .marketplace(marketplace)
                .title(SOME_STRING)
                .image(SOME_STRING)
                .link(SOME_STRING)
                .price(SOME_STRING)
                .priceImage(SOME_STRING)
                .descriptionImage(SOME_STRING)
                .build();
    }

    private static ProductsRequest buildProductsRequest() {
        return ProductsRequest.builder()
                .minPrice(MIN_PRICE)
                .maxPrice(MAX_PRICE)
                .filters(List.of(ProductsRequest.Filter.builder()
                        .key(SOME_STRING)
                        .values(List.of(SOME_STRING, SOME_SUBCATEGORY))
                        .build()))
                .build();
    }
}
