package ua.edu.cdu.vu.price.aggregator.api.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;
import ua.edu.cdu.vu.price.aggregator.api.exception.MarketplaceNotFoundException;
import ua.edu.cdu.vu.price.aggregator.api.mapper.MarketplaceConfigMapper;
import ua.edu.cdu.vu.price.aggregator.api.properties.MarketplaceConfigProperties;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PropertiesMarketplaceConfigDao implements MarketplaceConfigDao {

    private final MarketplaceConfigProperties marketplaceConfigProperties;
    private final MarketplaceConfigMapper marketplaceConfigMapper;

    @Override
    public MarketplaceConfig load(String marketplace) {
        return Optional.ofNullable(marketplaceConfigProperties.get(marketplace))
                .map(part -> marketplaceConfigMapper.convertToDomain(marketplace, part))
                .orElseThrow(() -> new MarketplaceNotFoundException(marketplace));
    }

    @Override
    public Map<String, MarketplaceConfig> loadAll() {
        return marketplaceConfigProperties.getMarketplaceConfig().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), marketplaceConfigMapper.convertToDomain(entry.getKey(), entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
