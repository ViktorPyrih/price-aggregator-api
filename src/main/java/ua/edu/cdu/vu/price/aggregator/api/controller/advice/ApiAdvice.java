package ua.edu.cdu.vu.price.aggregator.api.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.edu.cdu.vu.price.aggregator.api.exception.*;
import ua.edu.cdu.vu.price.aggregator.api.validation.HttpRequestNotValidException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class ApiAdvice {

    private static final String NAME_MESSAGE_SEPARATOR = ": ";
    private static final String ALL_ERRORS_SEPARATOR = ", ";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = e.getBody();
        problemDetail.setDetail(getDetail(e));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({DslValidationException.class, DslExecutionException.class})
    public ProblemDetail handleDslExceptions(RuntimeException e) {
        log.error("DSL exception occurred", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CategoriesNotFoundException.class)
    public ProblemDetail handleCategoryNotFoundException(CategoriesNotFoundException e) {
        log.warn("Categories not found", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UnsupportedOperationException.class, HttpRequestNotValidException.class})
    public ProblemDetail handleUnsupportedOperationExceptions(UnsupportedOperationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({MarketplaceNotFoundException.class})
    public ProblemDetail handleMarketplaceNotFoundException(MarketplaceNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler({SearchNotConfiguredException.class})
    public ProblemDetail handleSearchNotConfiguredException(SearchNotConfiguredException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
    }

    private String getDetail(MethodArgumentNotValidException e) {
        return Stream.concat(globalErrors(e), fieldErrors(e))
                .collect(Collectors.joining(ALL_ERRORS_SEPARATOR));
    }

    private Stream<String> globalErrors(MethodArgumentNotValidException e) {
        return e.getGlobalErrors().stream()
                .map(error -> String.join(NAME_MESSAGE_SEPARATOR, error.getObjectName(), error.getDefaultMessage()));
    }

    private Stream<String> fieldErrors(MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
                .map(error -> String.join(NAME_MESSAGE_SEPARATOR, error.getField(), error.getDefaultMessage()));
    }
}
