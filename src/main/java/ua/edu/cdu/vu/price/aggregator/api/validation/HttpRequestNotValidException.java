package ua.edu.cdu.vu.price.aggregator.api.validation;

public class HttpRequestNotValidException extends RuntimeException {

    public HttpRequestNotValidException(String message) {
        super(message);
    }
}
