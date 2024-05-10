package ua.edu.cdu.vu.price.aggregator.api.job.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import ua.edu.cdu.vu.price.aggregator.api.job.model.CategoriesResponse;
import ua.edu.cdu.vu.price.aggregator.api.job.model.FiltersResponse;

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
        var response = rest.getForEntity(URL.formatted(port) + "/marketplaces/{marketplace}/subcategories?category={category}&subcategory1={subcategory}",
                CategoriesResponse.class, marketplace, category, subcategory);
        assertThat(response.getStatusCode())
                .as("Response status is %s for marketplace: %s, category: %s, subcategory: %s, but expected 200 OK",
                        response.getStatusCode(), marketplace, category, subcategory)
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        return response.getBody().categories();
    }

    @Override
    public FiltersResponse getFilters(String marketplace, String category, String subcategory1, String subcategory2) {
        log.info("Verifying filters for marketplace: {}, category: {}, subcategory1: {}, subcategory2: {}", marketplace, category, subcategory1, subcategory2);
        var response = rest.getForEntity(URL.formatted(port) + "/marketplaces/{marketplace}/filters?category={category}&subcategory1={subcategory1}&subcategory2={subcategory2}",
                FiltersResponse.class, marketplace, category, subcategory1, subcategory2);

        assertThat(response.getStatusCode())
                .as("Response status is %s for marketplace: %s, category: %s, subcategory1: %s, subcategory2: %s, but expected 200 OK",
                        response.getStatusCode(), marketplace, category, subcategory1, subcategory2)
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotNull()
                .extracting(FiltersResponse::filters)
                .asList()
                .as("Filters list is empty for marketplace: %s, category: %s, subcategory1: %s, subcategory2: %s",
                        marketplace, category, subcategory1, subcategory2)
                .isNotEmpty();

        return response.getBody();
    }
}
