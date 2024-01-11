package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class ZipDslCommand extends DslCommand<Iterable<Object>, List<Pair<Object, Object>>> {

    private final DslExpression<Iterable<Object>> withExpression;

    public ZipDslCommand(@NonNull List<DslExpression<Iterable<Object>>> otherDslExpressions, @NonNull Map<String, String> arguments) {
        if (otherDslExpressions.size() < 1) {
            throw new DslValidationException("ZIP command takes 'otherDslExpression' as a parameter");
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
    public List<Pair<Object, Object>> execute(String url, Iterable<Object> input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("ZIP command executed on null input");
        }

        Iterable<Object> withExpressionResult = withExpression.evaluate(url, context);

        if (isNull(withExpressionResult)) {
            throw new DslExecutionException("ZIP command executed on null input, returned after evaluation of 'with' expression");
        }

        long zipResultSize = Math.min(input.spliterator().estimateSize(), withExpressionResult.spliterator().estimateSize());

        var inputIterator = input.iterator();
        var withExpressionResultIterator = withExpressionResult.iterator();
        var zipResult = new LinkedList<Pair<Object, Object>>();
        for (int i = 0; i < zipResultSize; i++) {
            zipResult.add(ImmutablePair.of(inputIterator.next(), withExpressionResultIterator.next()));
        }

        return zipResult;
    }
}
