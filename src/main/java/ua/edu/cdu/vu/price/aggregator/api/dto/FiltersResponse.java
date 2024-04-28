package ua.edu.cdu.vu.price.aggregator.api.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
public class FiltersResponse implements Serializable {

    List<Filter> filters;

    @Value
    @Builder
    public static class Filter implements Serializable {

        String key;
        List<String> values;

    }
}
