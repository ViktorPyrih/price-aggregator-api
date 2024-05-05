package ua.edu.cdu.vu.price.aggregator.api.job.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.edu.cdu.vu.price.aggregator.api.job.model.CategoriesResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class PriceAggregatorRestClient implements PriceAggregatorClient {

    private static final String URL = "http://localhost:%d/api/v1";

    private final TestRestTemplate rest;

    private final int port;

    public List<String> getCategories(String marketplace) {
        log.info("Getting categories for marketplace: {}", marketplace);
        return rest.getForObject(URL.formatted(port) + "/marketplaces/{marketplace}/categories",
                CategoriesResponse.class, marketplace).categories();
    }

    public List<String> getSubcategories(String marketplace, String category) {
        log.info("Getting subcategories for marketplace: {}, category: {}", marketplace, category);
        return rest.getForObject(URL.formatted(port) + "/marketplaces/{marketplace}/subcategories?category={category}",
                CategoriesResponse.class, marketplace, category).categories();
    }

    public List<String> getSubcategories(String marketplace, String category, String subcategory) {
        log.info("Getting subcategories for marketplace: {}, category: {}, subcategory: {}", marketplace, category, subcategory);
        ResponseEntity<CategoriesResponse> response = rest.getForEntity(URL.formatted(port) + "/marketplaces/{marketplace}/subcategories?category={category}&subcategory1={subcategory}",
                CategoriesResponse.class, marketplace, category, subcategory);
        assertThat(response.getStatusCode())
                .as("Response status is %s for marketplace: %s, category: %s, subcategory: %s, but expected 200 OK",
                        response.getStatusCode(), marketplace, category, subcategory)
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        return response.getBody().categories();
    }
}
