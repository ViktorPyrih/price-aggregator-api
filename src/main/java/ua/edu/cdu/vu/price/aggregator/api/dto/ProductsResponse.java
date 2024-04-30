package ua.edu.cdu.vu.price.aggregator.api.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Value
@Builder
public class ProductsResponse implements Serializable {

    List<Product> products;
    int pagesCount;

    @Value
    @Builder
    public static class Product implements Serializable {
        String link;
        String image;
        String price;
        String description;
    }

    public static ProductsResponse empty() {
        return ProductsResponse.builder()
                .products(Collections.emptyList())
                .pagesCount(1)
                .build();
    }
}
