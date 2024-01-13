package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.NonNull;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UnionDslCommand extends CompositeDslCommand<List<Object>, List<Object>> {

    public UnionDslCommand(@NonNull List<DslExpression<List<Object>>> otherDslExpressions, @NonNull Map<String, String> arguments) {
        super(otherDslExpressions, arguments);
    }

    @Override
    protected List<Object> combine(List<Object> input1, List<Object> input2) {
        return Stream.concat(input1.stream(), input2.stream()).toList();
    }
}
