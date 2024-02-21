package ua.edu.cdu.vu.price.aggregator.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductsRequest {

    @NotNull
    @PositiveOrZero
    Double minPrice;
    @NotNull
    @Positive
    Double maxPrice;
    List<@Valid Filter> filters;

    @Value
    @Builder
    public static class Filter {
        @NotBlank
        String key;
        @NotEmpty
        List<String> values;
    }

}
