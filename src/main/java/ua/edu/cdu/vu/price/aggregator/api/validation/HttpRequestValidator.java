package ua.edu.cdu.vu.price.aggregator.api.validation;

import jakarta.servlet.http.HttpServletRequest;

public interface HttpRequestValidator<T> {

    void validate(HttpServletRequest request, T context) throws HttpRequestNotValidException;

    default void validate(HttpServletRequest request) throws HttpRequestNotValidException {
        validate(request, null);
    }
}
