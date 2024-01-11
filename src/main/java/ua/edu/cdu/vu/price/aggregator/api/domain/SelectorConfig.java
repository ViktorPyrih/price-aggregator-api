package ua.edu.cdu.vu.price.aggregator.api.domain;

import java.util.List;

public record SelectorConfig(List<String> actions,
                             String selector,
                             List<String> other) {
}
