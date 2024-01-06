package ua.edu.cdu.vu.price.aggregator.api.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.mapper.MarketplaceConfigMapper;
import ua.edu.cdu.vu.price.aggregator.api.properties.MarketplaceConfigProperties;

@Component
@RequiredArgsConstructor
public class PropertiesMarketplaceConfigDao implements MarketplaceConfigDao {

    private final MarketplaceConfigProperties marketplaceConfigProperties;
    private final MarketplaceConfigMapper marketplaceConfigMapper;

    @Override
    public MarketplaceConfig load(String marketplace) {
        MarketplaceConfigProperties.Part part = marketplaceConfigProperties.get(marketplace);
        return marketplaceConfigMapper.convertToDomain(part);
    }

}
