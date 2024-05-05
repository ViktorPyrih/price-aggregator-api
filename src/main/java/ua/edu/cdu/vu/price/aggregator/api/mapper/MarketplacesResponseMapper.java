package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.api.dto.MarketplacesResponse;

import java.util.Map;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface MarketplacesResponseMapper {

    default MarketplacesResponse convertToResponse(Map<String, Integer> marketplaceConfigs) {
        return MarketplacesResponse.builder()
                .marketplaces(marketplaceConfigs.entrySet().stream()
                        .map(this::convertToMarketplace)
                        .toList())
                .build();
    }

    private MarketplacesResponse.Marketplace convertToMarketplace(Map.Entry<String, Integer> entry) {
        return MarketplacesResponse.Marketplace.builder()
                .name(entry.getKey())
                .subcategoriesCount(entry.getValue())
                .build();
    }
}
