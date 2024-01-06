package ua.edu.cdu.vu.price.aggregator.api.exception;

public class DslExecutionException extends RuntimeException {

    private static final String MESSAGE = "DSL expression execution failed";

    public DslExecutionException(String message) {
        super(message);
    }

    public DslExecutionException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
