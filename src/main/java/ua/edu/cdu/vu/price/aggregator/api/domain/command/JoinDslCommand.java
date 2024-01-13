package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JoinDslCommand extends CompositeDslCommand<Iterable<Object>, List<Pair<Object, Object>>> {

    public JoinDslCommand(@NonNull List<DslExpression<Iterable<Object>>> otherDslExpressions, @NonNull Map<String, String> arguments) {
        super(otherDslExpressions, arguments);
    }

    @Override
    protected List<Pair<Object, Object>> combine(Iterable<Object> input1, Iterable<Object> input2) {
        long zipResultSize = Math.min(input1.spliterator().estimateSize(), input2.spliterator().estimateSize());

        var inputIterator = input1.iterator();
        var withExpressionResultIterator = input2.iterator();
        var zipResult = new LinkedList<Pair<Object, Object>>();
        for (int i = 0; i < zipResultSize; i++) {
            zipResult.add(ImmutablePair.of(inputIterator.next(), withExpressionResultIterator.next()));
        }

        return zipResult;
    }
}
