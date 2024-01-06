package ua.edu.cdu.vu.price.aggregator.api.dao;

import ua.edu.cdu.vu.price.aggregator.api.domain.MarketplaceConfig;

public interface MarketplaceConfigDao {

    MarketplaceConfig load(String marketplace);

}
