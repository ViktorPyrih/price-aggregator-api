package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.api.dto.MarketplacesResponse;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface MarketplacesResponseMapper {

    default MarketplacesResponse convertToResponse(List<String> marketplaces) {
        return MarketplacesResponse.builder()
                .marketplaces(marketplaces)
                .build();
    }
}
