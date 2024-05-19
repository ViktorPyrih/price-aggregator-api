package ua.edu.cdu.vu.price.aggregator.api.functional.tests.decorator;

import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.client.PriceAggregatorClient;
import ua.edu.cdu.vu.price.aggregator.api.functional.tests.model.FiltersResponse;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PriceAggregatorClientBulkhead implements PriceAggregatorClient {

    private final PriceAggregatorClient client;
    private final Semaphore semaphore = new Semaphore(10);

    @Override
    public List<String> getCategories(String marketplace) {
        return runInSemaphore(() -> client.getCategories(marketplace));
    }

    @Override
    public List<String> getSubcategories(String marketplace, String category) {
        return runInSemaphore(() -> client.getSubcategories(marketplace, category));
    }

    @Override
    public List<String> getSubcategories(String marketplace, String category, String subcategory) {
        return runInSemaphore(() -> client.getSubcategories(marketplace, category, subcategory));
    }

    @Override
    public List<String> getSubcategories(String marketplace, String category, String subcategory1, String subcategory2) {
        return runInSemaphore(() -> client.getSubcategories(marketplace, category, subcategory1, subcategory2));
    }

    @Override
    public List<FiltersResponse.Filter> getFilters(String marketplace, String category, String subcategory1, String subcategory2) {
        return runInSemaphore(() -> client.getFilters(marketplace, category, subcategory1, subcategory2));
    }

    @Override
    public List<FiltersResponse.Filter> getFilters(String marketplace, String category, String subcategory1, String subcategory2, String subcategory3) {
        return runInSemaphore(() -> client.getFilters(marketplace, category, subcategory1, subcategory2, subcategory3));
    }

    private <T> T runInSemaphore(Supplier<T> supplier) {
        acquire();

        try {
            return supplier.get();
        } finally {
            semaphore.release();
        }
    }

    private void acquire() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
