package ua.edu.cdu.vu.price.aggregator.api.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record MarketplaceConfig(String marketplace,
                                String url,
                                SelectorConfig categories,
                                List<SelectorConfig> subcategories,
                                SelectorConfig filters,
                                ProductsSelectorConfig products,
                                SearchSelectorConfig search) {
}
