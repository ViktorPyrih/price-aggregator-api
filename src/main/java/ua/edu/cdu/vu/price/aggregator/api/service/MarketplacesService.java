package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.dao.MarketplaceConfigDao;
import ua.edu.cdu.vu.price.aggregator.api.dto.MarketplacesResponse;
import ua.edu.cdu.vu.price.aggregator.api.mapper.MarketplacesResponseMapper;

@Service
@RequiredArgsConstructor
public class MarketplacesService {

    private final MarketplaceConfigDao marketplaceConfigDao;
    private final MarketplacesResponseMapper marketplacesResponseMapper;

    public MarketplacesResponse getMarketplaces() {
        var marketplaces = marketplaceConfigDao.getAllMarketplaces();
        return marketplacesResponseMapper.convertToResponse(marketplaces);
    }
}
