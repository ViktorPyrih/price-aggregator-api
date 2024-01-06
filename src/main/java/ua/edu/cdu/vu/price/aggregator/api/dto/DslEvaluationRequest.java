package ua.edu.cdu.vu.price.aggregator.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.Map;

public record DslEvaluationRequest(@Valid @NotNull Target target,
                                   @NotBlank String expression,
                                   List<@NotBlank String> actions,
                                   Map<@NotBlank String, @NotBlank String> arguments) {

    public record Target(@URL @NotBlank String url) {
    }
}
