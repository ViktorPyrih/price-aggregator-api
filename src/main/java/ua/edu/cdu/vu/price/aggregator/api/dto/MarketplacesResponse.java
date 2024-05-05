package ua.edu.cdu.vu.price.aggregator.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MarketplacesResponse {

    List<Marketplace> marketplaces;

    @Value
    @Builder
    public static class Marketplace {

        String name;
        Integer subcategoriesCount;

    }

}
