package ua.edu.cdu.vu.price.aggregator.api.controller.advice.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ValidationErrorResponse {

    @Value
    @Builder
    public static class Error {

        String field;
        String reason;

    }

    String message;
    List<Error> errors;

}
