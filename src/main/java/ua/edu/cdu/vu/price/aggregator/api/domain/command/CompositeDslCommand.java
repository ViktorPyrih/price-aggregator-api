package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@EqualsAndHashCode(callSuper = true)
public abstract class CompositeDslCommand<IN, OUT> extends DslCommand<IN, OUT> {

    private final DslExpression<IN> withExpression;

    public CompositeDslCommand(@NonNull List<DslExpression<IN>> otherDslExpressions, @NonNull Map<String, String> arguments) {
        if (otherDslExpressions.isEmpty()) {
            throw new DslValidationException("%s command takes 'otherDslExpression' as a parameter".formatted(getClass().getSimpleName()));
        }

        String with = arguments.get("with");
        if (isNull(with)) {
            throw new DslValidationException("'With' parameter is required");
        }

        int index = parseInt(with);
        if (index <= 0) {
            throw new DslValidationException("'With' parameter should be greater than 0");
        }

        withExpression = otherDslExpressions.get(index - 1);
    }

    @Override
    public OUT executeInternal(String url, IN input, Map<String, Object> context, WebDriver webDriver) {
        IN withExpressionResult = withExpression.evaluate(url, context, webDriver);

        if (isNull(withExpressionResult)) {
            throw new DslExecutionException("ZIP command executed on null input, returned after evaluation of 'with' expression");
        }

        return combine(input, withExpressionResult);
    }

    protected abstract OUT combine(IN input1, IN input2);
}
