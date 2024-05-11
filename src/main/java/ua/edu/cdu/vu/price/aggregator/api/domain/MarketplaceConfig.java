package ua.edu.cdu.vu.price.aggregator.api.domain;

import java.util.List;

public record MarketplaceConfig(String marketplace,
                                String url,
                                SelectorConfig categories,
                                List<SelectorConfig> subcategories,
                                SelectorConfig filters,
                                ProductsSelectorConfig products,
                                SearchSelectorConfig search) {
}
