package ua.edu.cdu.vu.price.aggregator.api.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.mapper.MarketplaceConfigMapper;
import ua.edu.cdu.vu.price.aggregator.api.properties.MarketplaceConfigProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, MarketplaceConfig> loadAll() {
        return marketplaceConfigProperties.getMarketplaceConfig().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), marketplaceConfigMapper.convertToDomain(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public List<String> getAllMarketplaces() {
        return marketplaceConfigProperties.getMarketplaceConfig().keySet().stream().toList();
    }

}
