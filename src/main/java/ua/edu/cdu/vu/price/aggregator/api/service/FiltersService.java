package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;
import ua.edu.cdu.vu.price.aggregator.api.mapper.FilterResponseMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FiltersService {

    private static final String CATEGORY = "category";
    private static final String SUBCATEGORY_1 = "subcategory1";
    private static final String SUBCATEGORY_2 = "subcategory2";

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;
    private final FilterResponseMapper filterResponseMapper;

    public FiltersResponse getFilters(String marketplace, String category, String subcategory1, String subcategory2) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        var arguments = Map.of(CATEGORY, category, SUBCATEGORY_1, subcategory1, SUBCATEGORY_2, subcategory2);
        DslEvaluationRequest request = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), marketplaceConfig.filters(), arguments);
        var rawFilters = dslEvaluationService.<List<Pair<String, List<String>>>>evaluate(request).getValue();

        return filterResponseMapper.convertToResponse(rawFilters);
    }
}
