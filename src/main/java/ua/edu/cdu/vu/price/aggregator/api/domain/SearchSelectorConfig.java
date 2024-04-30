package ua.edu.cdu.vu.price.aggregator.api.domain;

import java.util.List;

public record SearchSelectorConfig(List<String> actions,
                                   String imageSelector,
                                   String linkSelector,
                                   String priceSelector,
                                   String descriptionSelector,
                                   String pagesCountSelector,
                                   List<String> other) {
}
