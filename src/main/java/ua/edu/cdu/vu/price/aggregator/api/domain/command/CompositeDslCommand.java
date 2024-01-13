package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.NonNull;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public abstract class CompositeDslCommand<IN, OUT> extends DslCommand<IN, OUT> {

    private final DslExpression<IN> withExpression;

    public CompositeDslCommand(@NonNull List<DslExpression<IN>> otherDslExpressions, @NonNull Map<String, String> arguments) {
        if (otherDslExpressions.size() < 1) {
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
    public OUT execute(String url, IN input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("ZIP command executed on null input");
        }

        IN withExpressionResult = withExpression.evaluate(url, context);

        if (isNull(withExpressionResult)) {
            throw new DslExecutionException("ZIP command executed on null input, returned after evaluation of 'with' expression");
        }

        return combine(input, withExpressionResult);
    }

    protected abstract OUT combine(IN input1, IN input2);
}
