package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.FiltersResponse;
import ua.edu.cdu.vu.price.aggregator.api.exception.CategoriesNotFoundException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.mapper.DslEvaluationRequestMapper;
import ua.edu.cdu.vu.price.aggregator.api.mapper.FilterResponseMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FiltersService {

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final DslEvaluationService dslEvaluationService;
    private final DslEvaluationRequestMapper dslEvaluationRequestMapper;
    private final FilterResponseMapper filterResponseMapper;
    private final SubcategoriesService subcategoriesService;

    @Cacheable("filters")
    public FiltersResponse getFilters(String marketplace, String category, Map<String, String> subcategories) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        var arguments = subcategoriesService.getSubcategoriesMap(marketplace, category, subcategories);
        DslEvaluationRequest request = dslEvaluationRequestMapper.convertToRequest(marketplaceConfig.url(), marketplaceConfig.filters(), arguments);

        try {
            List<Pair<String, List<String>>> rawFilters = dslEvaluationService.evaluate(request).getSingleValue();
            return filterResponseMapper.convertToResponse(rawFilters);
        } catch (DslExecutionException e) {
            throw new CategoriesNotFoundException(marketplace, category, subcategories.values(), e);
        }
    }
}
