package ua.edu.cdu.vu.price.aggregator.api.dao;

import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;

import java.util.Map;

public interface MarketplaceConfigDao {

    MarketplaceConfig load(String marketplace);

    Map<String, MarketplaceConfig> loadAll();

}
