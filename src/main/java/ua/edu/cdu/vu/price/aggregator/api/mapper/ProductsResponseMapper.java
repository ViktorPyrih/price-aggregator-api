package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.Mapper;
import ua.edu.cdu.vu.price.aggregator.api.dto.ProductsResponse;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductsResponseMapper {

    default ProductsResponse convertToResponse(List<Pair<String, String>> rawProducts) {
        return ProductsResponse.builder()
                .products(rawProducts.stream()
                        .map(pair -> ProductsResponse.Product.builder()
                                .link(pair.getValue())
                                .image(pair.getKey())
                                .build())
                        .toList())
                .build();
    }
}
