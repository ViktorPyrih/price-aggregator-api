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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;
    private final SubcategoriesService subcategoriesService;

    @Cacheable("categories")
    public CategoriesResponse getCategories(String marketplace) {
        return getCategories(marketplace, MarketplaceConfig::categories, Map.of());
    }

    @Cacheable("subcategories")
    public CategoriesResponse getSubcategories(String marketplace, String category, Map<String, String> subcategories) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);

        Map<String, Object> arguments = subcategoriesService.getSubcategoriesMap(marketplace, category, subcategories);

        var subcategoriesConfig = marketplaceConfig.subcategories();

        return getCategories(marketplace, config -> subcategoriesConfig.get(arguments.size() - 1), arguments);
    }

    private CategoriesResponse getCategories(String marketplace, Function<MarketplaceConfig, SelectorConfig> selectorConfigFunction, Map<String, Object> arguments) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        DslEvaluationRequest request = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), selectorConfigFunction.apply(marketplaceConfig), arguments);

        try {
            return CategoriesResponse.builder()
                    .categories(dslEvaluationService.evaluate(request).getSingleValue())
                    .build();
        } catch (DslExecutionException e) {

            throw new CategoriesNotFoundException(marketplace, convertToStringList(arguments.values()), e);
        }
    }

    private static List<String> convertToStringList(Collection<Object> objects) {
        return objects.stream()
                .map(Object::toString)
                .toList();
    }
}
