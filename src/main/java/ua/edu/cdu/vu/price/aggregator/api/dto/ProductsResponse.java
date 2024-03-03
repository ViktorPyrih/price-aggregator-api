package ua.edu.cdu.vu.price.aggregator.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductsResponse {

    List<Product> products;

    @Value
    @Builder
    public static class Product {
        String link;
        String image;
        String price;
        String description;
    }

}
