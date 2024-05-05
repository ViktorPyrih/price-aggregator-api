package ua.edu.cdu.vu.price.aggregator.api.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import ua.edu.cdu.vu.price.aggregator.api.job.client.PriceAggregatorClient;
import ua.edu.cdu.vu.price.aggregator.api.job.client.PriceAggregatorRestClient;
import ua.edu.cdu.vu.price.aggregator.api.job.decorator.PriceAggregatorClientBulkhead;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

//@Disabled
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheEnricherTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ExecutorService taskExecutor;

    @LocalServerPort
    private int port;

    private PriceAggregatorClient priceAggregatorClient;

    @BeforeEach
    void setUp() {
        priceAggregatorClient = new PriceAggregatorClientBulkhead(new PriceAggregatorRestClient(rest, port));
    }

    @Test
    @Disabled
    void populateHotlineCategoriesCacheConcurrently() {
        final String marketplace = "hotline";
        populateCategoriesCacheConcurrently(marketplace);
    }

    @Test
    @Disabled
    void populateEkatalogCategoriesCacheConcurrently() {
        final String marketplace = "ekatalog";
        populateCategoriesCacheConcurrently(marketplace);
    }

    @Test
    void populateHotlineCategoriesCache() {
        final String marketplace = "hotline";
        populateCategoriesCache(marketplace);
    }

    @Test
    void populateEkatalogCategoriesCache() {
        final String marketplace = "ekatalog";
        populateCategoriesCache(marketplace);
    }

    void populateCategoriesCache(String marketplace) {
        var categories = priceAggregatorClient.getCategories(marketplace);
        for (var category : categories) {
            var subcategories = priceAggregatorClient.getSubcategories(marketplace, category);
            for (var subcategory : subcategories) {
                priceAggregatorClient.getSubcategories(marketplace, category, subcategory);
            }
        }
    }

    private void populateCategoriesCacheConcurrently(String marketplace) {
        var futures = priceAggregatorClient.getCategories(marketplace).stream()
                .map(category -> CompletableFuture.runAsync(() -> {
                    var subcategories = priceAggregatorClient.getSubcategories(marketplace, category);
                    var futures2 = subcategories.stream()
                            .map(subcategory -> CompletableFuture.runAsync(() -> priceAggregatorClient.getSubcategories(marketplace, category, subcategory), taskExecutor))
                            .toArray(CompletableFuture[]::new);

                    CompletableFuture.allOf(futures2).join();
                }, taskExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }
}
