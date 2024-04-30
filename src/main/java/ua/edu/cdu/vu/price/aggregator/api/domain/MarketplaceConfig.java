package ua.edu.cdu.vu.price.aggregator.api.domain;

public record MarketplaceConfig(String url,
                                SelectorConfig categories,
                                SelectorConfig subcategories1,
                                SelectorConfig subcategories2,
                                SelectorConfig filters,
                                ProductsSelectorConfig products,
                                SearchSelectorConfig search) {
}
