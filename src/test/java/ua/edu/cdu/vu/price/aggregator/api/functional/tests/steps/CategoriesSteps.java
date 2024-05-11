package ua.edu.cdu.vu.price.aggregator.api.functional.tests.steps;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.client.PriceAggregatorClient;

import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class CategoriesSteps {

    private static final Set<String> SUBCATEGORIES_TO_IGNORE = Set.of("Парфумерія");
    private static final Set<String> SUBCATEGORIES2_TO_IGNORE = Set.of("Всі бренди");

    private final PriceAggregatorClient priceAggregatorClient;

    public void verifyAllCategories(String marketplace, boolean assertSubcategories2) {
        verifyAllCategories(marketplace, assertSubcategories2, subcategory -> {});
    }

    public void verifyAllCategories(String marketplace, boolean assertSubcategories2, Consumer<String[]> subcategoryConsumer) {
        var categories = priceAggregatorClient.getCategories(marketplace);
        assertThat(categories)
                .as("Categories list is empty for marketplace: %s", marketplace)
                .isNotEmpty();

        for (var category : categories) {
            var subcategories = priceAggregatorClient.getSubcategories(marketplace, category);
            assertThat(subcategories)
                    .as("Subcategories list is empty for marketplace: %s, category: %s", marketplace, category)
                    .isNotEmpty();

            for (var subcategory : subcategories) {
                if (SUBCATEGORIES_TO_IGNORE.contains(subcategory)) {
                    continue;
                }

                var subcategories2 = priceAggregatorClient.getSubcategories(marketplace, category, subcategory);

                if (assertSubcategories2) {
                    assertThat(subcategories2)
                            .as("Subcategories2 list is empty for marketplace: %s, category: %s, subcategory: %s", marketplace, category, subcategory)
                            .isNotEmpty();
                }

                for (var subcategory2 : subcategories2) {
                    if (!SUBCATEGORIES2_TO_IGNORE.contains(subcategory2)) {
                        subcategoryConsumer.accept(new String[]{marketplace, category, subcategory, subcategory2});
                    }
                }
            }
        }
    }
}
