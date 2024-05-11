package ua.edu.cdu.vu.price.aggregator.api.exception;

public class MarketplaceNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Marketplace not found: ";

    public MarketplaceNotFoundException(String marketplace) {
        super(MESSAGE + marketplace);
    }
}
