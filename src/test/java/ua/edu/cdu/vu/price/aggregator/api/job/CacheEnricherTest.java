package ua.edu.cdu.vu.price.aggregator.api.job;

import org.junit.jupiter.api.Test;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.BaseFunctionalTest;

public class CacheEnricherTest extends BaseFunctionalTest {

    @Test
    void populateHotlineCache() {
        final String marketplace = "hotline";
        populateCache(marketplace, true);
    }

    @Test
    void populateEkatalogCache() {
        final String marketplace = "ekatalog";
        populateCache(marketplace, false);
    }

    void populateCache(String marketplace, boolean assertSubcategories2) {
        categoriesSteps.verifyAllCategories(marketplace, assertSubcategories2, args -> {
            if (args.length == 4) {
                filtersSteps.verifyFilters(args[0], args[1], args[2], args[3]);
            } else if (args.length == 5) {
                filtersSteps.verifyFilters(args[0], args[1], args[2], args[3], args[4]);
            }
        });
    }
}
