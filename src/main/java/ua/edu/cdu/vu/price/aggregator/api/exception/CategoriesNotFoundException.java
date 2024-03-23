package ua.edu.cdu.vu.price.aggregator.api.exception;

import java.util.Collection;
import java.util.List;

public class CategoriesNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Categories combination: %s not found on marketplace: %s";
    private static final String MESSAGE_WITH_PAGE = "Categories combination: %s not found on marketplace: %s for page: %d";

    public CategoriesNotFoundException(String marketplace, Throwable cause, Collection<String> categories) {
        super(MESSAGE.formatted(List.of(categories), marketplace), cause);
    }

    public CategoriesNotFoundException(String marketplace, Throwable cause, String... categories) {
        this(marketplace, cause, List.of(categories));
    }

    public CategoriesNotFoundException(String marketplace, int page, Throwable cause, String... categories) {
        super(MESSAGE_WITH_PAGE.formatted(List.of(categories), marketplace, page), cause);
    }
}
