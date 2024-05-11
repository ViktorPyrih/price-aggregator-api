package ua.edu.cdu.vu.price.aggregator.api.job;

import org.junit.jupiter.api.Test;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.BaseFunctionalTest;

//@Disabled
public class CacheEnricherTest extends BaseFunctionalTest {

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
        categoriesSteps.verifyAllCategories(marketplace, args -> filtersSteps.verifyFilters(args[0], args[1], args[2], args[3]));
    }
}
