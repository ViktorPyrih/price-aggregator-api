package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.SelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;

import java.util.List;
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

    public List<String> getCategories(String marketplace) {
        return getCategories(marketplace, MarketplaceConfig::categories, Map.of());
    }

    public List<String> getCategories(String marketplace, String category) {
        return getCategories(marketplace, MarketplaceConfig::subcategories1, Map.of(CATEGORY, category));
    }

    public List<String> getCategories(String marketplace, String category, String subcategory) {
        return getCategories(marketplace, MarketplaceConfig::subcategories2, Map.of(CATEGORY, category, SUBCATEGORY, subcategory));
    }

    private List<String> getCategories(String marketplace, Function<MarketplaceConfig, SelectorConfig> selectorConfigFunction, Map<String, String> arguments) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        DslEvaluationRequest request = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), selectorConfigFunction.apply(marketplaceConfig), arguments);
        return dslEvaluationService.<List<String>>evaluate(request).getValue();
    }
}
