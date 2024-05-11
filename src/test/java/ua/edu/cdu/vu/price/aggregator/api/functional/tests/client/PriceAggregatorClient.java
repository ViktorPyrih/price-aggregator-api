package ua.edu.cdu.vu.price.aggregator.api.functional.tests.client;

import ua.edu.cdu.vu.price.aggregator.api.functional.tests.model.FiltersResponse;

import java.util.List;

public interface PriceAggregatorClient {

    List<String> getCategories(String marketplace);

    List<String> getSubcategories(String marketplace, String category);

    List<String> getSubcategories(String marketplace, String category, String subcategory);

    List<FiltersResponse.Filter> getFilters(String marketplace, String category, String subcategory1, String subcategory2);
}
