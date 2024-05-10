package ua.edu.cdu.vu.price.aggregator.api.util.pool;

public class WebDriverNotAvailableException extends RuntimeException {

    private static final String MESSAGE = "Web driver not available in the pool";
    private static final String MESSAGE_WITH_TIMEOUT = "Web driver not available in the pool, timeout: %d";

    public WebDriverNotAvailableException(Throwable cause) {
        super(MESSAGE, cause);
    }

    public WebDriverNotAvailableException(long timeout) {
        super(MESSAGE_WITH_TIMEOUT.formatted(timeout));
    }
}
