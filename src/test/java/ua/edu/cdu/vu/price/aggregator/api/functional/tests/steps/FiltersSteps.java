package ua.edu.cdu.vu.price.aggregator.api.functional.tests.steps;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.client.PriceAggregatorClient;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class FiltersSteps {

    private static final Set<String> SUBCATEGORIES2_TO_IGNORE = Set.of("Повітряні змії");

    private final PriceAggregatorClient priceAggregatorClient;

    public void verifyFilters(String marketplace, String category, String subcategory1, String subcategory2) {
        var filters = priceAggregatorClient.getFilters(marketplace, category, subcategory1, subcategory2);
        if (!SUBCATEGORIES2_TO_IGNORE.contains(subcategory2)) {
            assertThat(filters)
                    .as("Filters list is empty for marketplace: %s, category: %s, subcategory1: %s, subcategory2: %s",
                            marketplace, category, subcategory1, subcategory2)
                    .isNotEmpty();
        }
    }

    public void verifyFilters(String marketplace, String category, String subcategory1, String subcategory2, String subcategory3) {
        var filters = priceAggregatorClient.getFilters(marketplace, category, subcategory1, subcategory2, subcategory3);
        assertThat(filters)
                .as("Filters list is empty for marketplace: %s, category: %s, subcategory1: %s, subcategory2: %s, subcategory3: %s",
                        marketplace, category, subcategory1, subcategory2, subcategory3)
                .isNotEmpty();
    }
}
