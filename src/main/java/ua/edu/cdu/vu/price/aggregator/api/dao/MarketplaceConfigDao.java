package ua.edu.cdu.vu.price.aggregator.api.dao;

import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;

import java.util.List;

public interface MarketplaceConfigDao {

    MarketplaceConfig load(String marketplace);

    List<String> getAllMarketplaces();
}
