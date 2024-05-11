package ua.edu.cdu.vu.price.aggregator.api.functional.tests.steps;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.client.PriceAggregatorClient;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class FiltersSteps {

    private final PriceAggregatorClient priceAggregatorClient;

    public void verifyFilters(String marketplace, String category, String subcategory1, String subcategory2) {
        var filters = priceAggregatorClient.getFilters(marketplace, category, subcategory1, subcategory2);
        assertThat(filters)
                .as("Filters list is empty for marketplace: %s, category: %s, subcategory1: %s, subcategory2: %s",
                        marketplace, category, subcategory1, subcategory2)
                .isNotEmpty();
    }
}
