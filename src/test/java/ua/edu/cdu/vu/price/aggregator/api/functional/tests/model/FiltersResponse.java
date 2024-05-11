package ua.edu.cdu.vu.price.aggregator.api.functional.tests.model;

import java.util.List;

public record FiltersResponse(List<Filter> filters) {

    public record Filter(String key, List<String> values) {
    }
}
