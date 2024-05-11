package ua.edu.cdu.vu.price.aggregator.api.exception;

public class SearchNotConfiguredException extends RuntimeException {

    private static final String MESSAGE = "Search is not configured for the marketplace: ";

    public SearchNotConfiguredException(String marketplace) {
        super(MESSAGE + marketplace);
    }

}
