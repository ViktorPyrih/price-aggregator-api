package ua.edu.cdu.vu.price.aggregator.api.exception;

public class DslValidationException extends RuntimeException {

    private static final String MESSAGE = "DSL validation failed";

    public DslValidationException(String message) {
        super(message);
    }

    public DslValidationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
