package ua.edu.cdu.vu.price.aggregator.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FiltersResponse {

    List<Filter> filters;

    @Value
    @Builder
    public static class Filter {

        String key;
        List<String> values;

    }
}
