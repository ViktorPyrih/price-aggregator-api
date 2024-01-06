package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;

    public List<String> getCategories(String marketplace) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        DslEvaluationRequest request = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), marketplaceConfig.categories());
        return dslEvaluationService.evaluate(request).getValues();
    }
}
