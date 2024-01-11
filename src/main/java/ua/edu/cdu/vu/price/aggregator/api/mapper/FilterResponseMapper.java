package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.api.dto.FiltersResponse;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface FilterResponseMapper {

    default FiltersResponse convertToResponse(List<Pair<String, List<String>>> rawFilters) {
        return FiltersResponse.builder()
                .filters(rawFilters.stream()
                        .map(pair -> FiltersResponse.Filter.builder()
                                .key(pair.getKey())
                                .values(pair.getValue())
                                .build())
                        .toList())
                .build();
    }
}
