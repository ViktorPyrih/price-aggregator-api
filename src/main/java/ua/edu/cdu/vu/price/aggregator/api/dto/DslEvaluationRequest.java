package ua.edu.cdu.vu.price.aggregator.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.Map;

@With
@Value
@Builder
public class DslEvaluationRequest {

    @Valid @NotNull Target target;
    @NotBlank String expression;
    List<@NotBlank String> actions;
    List<@NotBlank String> otherExpressions;
    Map<@NotBlank String, @NotNull Object> arguments;

    public record Target(@URL @NotBlank String url) {
    }
}
