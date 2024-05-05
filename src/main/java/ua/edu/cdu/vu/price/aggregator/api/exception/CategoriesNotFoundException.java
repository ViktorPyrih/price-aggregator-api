package ua.edu.cdu.vu.price.aggregator.api.exception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CategoriesNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Categories combination: %s not found on marketplace: %s";
    private static final String MESSAGE_WITH_PAGE = "Categories combination: %s not found on marketplace: %s for page: %d";

    public CategoriesNotFoundException(String marketplace, Collection<String> categories, Throwable cause) {
        super(MESSAGE.formatted(categories, marketplace), cause);
    }

    public CategoriesNotFoundException(String marketplace, String category, Collection<String> categories, Throwable cause) {
        super(createMessage(marketplace, category, categories), cause);
    }

    public CategoriesNotFoundException(String marketplace, int page, String category, Collection<String> categories, Throwable cause) {
        super(createMessage(marketplace, page, category, categories), cause);
    }

    private static String createMessage(String marketplace, String category, Collection<String> categories) {
        ArrayList<String> allCategories = new ArrayList<>(List.of(category));
        allCategories.addAll(categories);

        return MESSAGE.formatted(allCategories, marketplace);
    }

    private static String createMessage(String marketplace, int page, String category, Collection<String> categories) {
        ArrayList<String> allCategories = new ArrayList<>(List.of(category));
        allCategories.addAll(categories);

        return MESSAGE_WITH_PAGE.formatted(allCategories, marketplace, page);
    }
}
