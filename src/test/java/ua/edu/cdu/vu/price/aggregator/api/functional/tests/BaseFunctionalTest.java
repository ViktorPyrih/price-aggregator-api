package ua.edu.cdu.vu.price.aggregator.api.functional.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.client.PriceAggregatorRestClient;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.decorator.PriceAggregatorClientBulkhead;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.steps.CategoriesSteps;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.steps.FiltersSteps;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseFunctionalTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected CategoriesSteps categoriesSteps;

    @Autowired
    protected FiltersSteps filtersSteps;

    @BeforeEach
    void init() {
        RestAssured.port = port;
    }

    @Configuration
    public static class TestConfiguration {

        @Bean
        @Primary
        public PriceAggregatorClientBulkhead priceAggregatorClientBulkhead(PriceAggregatorRestClient client) {
            return new PriceAggregatorClientBulkhead(client);
        }
    }
}
