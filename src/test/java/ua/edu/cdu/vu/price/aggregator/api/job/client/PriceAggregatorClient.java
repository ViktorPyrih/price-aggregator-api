package ua.edu.cdu.vu.price.aggregator.api.job.client;

import ua.edu.cdu.vu.price.aggregator.api.job.model.FiltersResponse;

import java.util.List;

public interface PriceAggregatorClient {

    List<String> getCategories(String marketplace);

    List<String> getSubcategories(String marketplace, String category);

    List<String> getSubcategories(String marketplace, String category, String subcategory);

    FiltersResponse getFilters(String marketplace, String category, String subcategory1, String subcategory2);
}
