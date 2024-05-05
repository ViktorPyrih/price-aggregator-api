package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.MarketplacesResponse;
import ua.edu.cdu.vu.price.aggregator.api.mapper.MarketplacesResponseMapper;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketplacesService {

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final MarketplacesResponseMapper marketplacesResponseMapper;

    public MarketplacesResponse getMarketplaces() {
        var categoriesCountPerMarketplace = marketplaceConfigDao.loadAll().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), getSubcategoriesCount(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return marketplacesResponseMapper.convertToResponse(categoriesCountPerMarketplace);
    }

    public int getSubcategoriesCount(String marketplace) {
        MarketplaceConfig marketplaceConfig = marketplaceConfigDao.load(marketplace);
        return getSubcategoriesCount(marketplaceConfig);
    }

    private int getSubcategoriesCount(MarketplaceConfig marketplaceConfig) {
        return (int) marketplaceConfig.subcategories().stream()
                .filter(Objects::nonNull)
                .count();
    }
}
