package ua.edu.cdu.vu.price.aggregator.api.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductsSelectorConfig(TemplateConfig filters, SelectorConfig self) {

    @Builder
    public record SelectorConfig(List<String> actions,
                                 String imageSelector,
                                 String linkSelector,
                                 String priceImgSelector,
                                 String descriptionImgSelector,
                                 String titleSelector,
                                 String priceSelector,
                                 String pagesCountSelector,
                                 List<String> other) {
    }
}
