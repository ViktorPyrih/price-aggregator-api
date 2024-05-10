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

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

//@Disabled
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheEnricherTest {

    private static final Set<String> SUBCATEGORIES_TO_IGNORE = Set.of("Всі бренди");

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
    void populateHotlineCacheConcurrently() {
        final String marketplace = "hotline";
        populateCategoriesCacheConcurrently(marketplace);
    }

    @Test
    @Disabled
    void populateEkatalogCacheConcurrently() {
        final String marketplace = "ekatalog";
        populateCategoriesCacheConcurrently(marketplace);
    }

    @Test
    void populateHotlineCache() {
        final String marketplace = "hotline";
        populateCache(marketplace);
    }

    @Test
    void populateEkatalogCache() {
        final String marketplace = "ekatalog";
        populateCache(marketplace);
    }

    void populateCache(String marketplace) {
        var categories = priceAggregatorClient.getCategories(marketplace);
        for (var category : categories) {
            var subcategories = priceAggregatorClient.getSubcategories(marketplace, category);
            for (var subcategory : subcategories) {
                var subcategories2 = priceAggregatorClient.getSubcategories(marketplace, category, subcategory);
                for (var subcategory2 : subcategories2) {
                    if (!SUBCATEGORIES_TO_IGNORE.contains(subcategory2)) {
                        priceAggregatorClient.getFilters(marketplace, category, subcategory, subcategory2);
                    }
                }
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
