package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.SelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.CategoriesResponse;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.exception.CategoriesNotFoundException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;

import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private static final String CATEGORY = "category";
    private static final String SUBCATEGORY = "subcategory";

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;

    @Cacheable("categories")
    public CategoriesResponse getCategories(String marketplace) {
        return getCategories(marketplace, MarketplaceConfig::categories, Map.of());
    }

    @Cacheable("subcategories")
    public CategoriesResponse getCategories(String marketplace, String category) {
        return getCategories(marketplace, MarketplaceConfig::subcategories1, Map.of(CATEGORY, category));
    }

    @Cacheable("subcategories2")
    public CategoriesResponse getCategories(String marketplace, String category, String subcategory) {
        return getCategories(marketplace, MarketplaceConfig::subcategories2, Map.of(CATEGORY, category, SUBCATEGORY, subcategory));
    }

    private CategoriesResponse getCategories(String marketplace, Function<MarketplaceConfig, SelectorConfig> selectorConfigFunction, Map<String, Object> arguments) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        DslEvaluationRequest request = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), selectorConfigFunction.apply(marketplaceConfig), arguments);

        try {
            return CategoriesResponse.builder()
                    .categories(dslEvaluationService.evaluate(request).getSingleValue())
                    .build();
        } catch (DslExecutionException e) {
            throw new CategoriesNotFoundException(marketplace, e, arguments.values().stream().map(Object::toString).toArray(String[]::new));
        }
    }
}
