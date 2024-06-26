package ua.edu.cdu.vu.price.aggregator.api.functional.tests.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.model.CategoriesResponse;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.model.FiltersResponse;

import java.util.List;

import static io.restassured.RestAssured.given;

@Slf4j
@Component
@EnableRetry
@Retryable(
        retryFor = AssertionError.class,
        exceptionExpression = "message.contains('500')",
        backoff = @Backoff(delay = 1000, multiplier = 2)
)
public class PriceAggregatorRestClient implements PriceAggregatorClient {

    private static final String BASE_PATH = "/api/v1";

    @Override
    public List<String> getCategories(String marketplace) {
        log.info("Getting categories for marketplace: {}", marketplace);

        return given().basePath(BASE_PATH)
                .when().get("/marketplaces/{marketplace}/categories", marketplace)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(CategoriesResponse.class)
                .categories();
    }

    @Override
    public List<String> getSubcategories(String marketplace, String category) {
        log.info("Getting subcategories for marketplace: {}, category: {}", marketplace, category);

        return given().basePath(BASE_PATH)
                .when().get("/marketplaces/{marketplace}/subcategories?category={category}", marketplace, category)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(CategoriesResponse.class)
                .categories();
    }

    @Override
    public List<String> getSubcategories(String marketplace, String category, String subcategory) {
        log.info("Getting subcategories for marketplace: {}, category: {}, subcategory: {}", marketplace, category, subcategory);

        return given().basePath(BASE_PATH)
                .when().get("/marketplaces/{marketplace}/subcategories?category={category}&subcategory1={subcategory}", marketplace, category, subcategory)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(CategoriesResponse.class)
                .categories();
    }

    @Override
    public List<String> getSubcategories(String marketplace, String category, String subcategory1, String subcategory2) {
        log.info("Getting subcategories for marketplace: {}, category: {}, subcategory1: {}, subcategory2: {}", marketplace, category, subcategory1, subcategory2);

        return given().basePath(BASE_PATH)
                .when().get("/marketplaces/{marketplace}/subcategories?category={category}&subcategory1={subcategory1}&subcategory2={subcategory2}", marketplace, category, subcategory1, subcategory2)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(CategoriesResponse.class)
                .categories();
    }

    @Override
    public List<FiltersResponse.Filter> getFilters(String marketplace, String category, String subcategory1, String subcategory2) {
        log.info("Getting filters for marketplace: {}, category: {}, subcategory1: {}, subcategory2: {}", marketplace, category, subcategory1, subcategory2);

        return given().basePath(BASE_PATH)
                .when().get("/marketplaces/{marketplace}/filters?category={category}&subcategory1={subcategory1}&subcategory2={subcategory2}", marketplace, category, subcategory1, subcategory2)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(FiltersResponse.class)
                .filters();
    }

    @Override
    public List<FiltersResponse.Filter> getFilters(String marketplace, String category, String subcategory1, String subcategory2, String subcategory3) {
        log.info("Getting filters for marketplace: {}, category: {}, subcategory1: {}, subcategory2: {}, subcategory3: {}", marketplace, category, subcategory1, subcategory2, subcategory3);

        return given().basePath(BASE_PATH)
                .when().get("/marketplaces/{marketplace}/filters?category={category}&subcategory1={subcategory1}&subcategory2={subcategory2}&subcategory3={subcategory3}", marketplace, category, subcategory1, subcategory2, subcategory3)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(FiltersResponse.class)
                .filters();
    }
}
