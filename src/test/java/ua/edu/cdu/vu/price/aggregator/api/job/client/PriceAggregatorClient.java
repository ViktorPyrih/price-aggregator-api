package ua.edu.cdu.vu.price.aggregator.api.job.client;

import java.util.List;

public interface PriceAggregatorClient {

    List<String> getCategories(String marketplace);

    List<String> getSubcategories(String marketplace, String category);

    List<String> getSubcategories(String marketplace, String category, String subcategory);
}
