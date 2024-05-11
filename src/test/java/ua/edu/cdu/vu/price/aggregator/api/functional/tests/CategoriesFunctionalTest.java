package ua.edu.cdu.vu.price.aggregator.api.functional.tests;

import org.junit.jupiter.api.Test;

public class CategoriesFunctionalTest extends BaseFunctionalTest {

    @Test
    void verifyAllHotlineCategories() {
        final String marketplace = "hotline";
        categoriesSteps.verifyAllCategories(marketplace);
    }

    @Test
    void verifyAllEkatalogCategories() {
        final String marketplace = "ekatalog";
        categoriesSteps.verifyAllCategories(marketplace);
    }
}
