package ua.edu.cdu.vu.price.aggregator.api.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@Value
@Builder
public class DslEvaluationScenario<T> implements AutoCloseable {

    List<DslExpression<Void>> actions;
    @NonNull DslExpression<T> expression;

    public T run(String url, Map<String, Object> context) {
        Stream.ofNullable(actions)
                .flatMap(List::stream)
                .forEach(action -> action.evaluate(url, context));

        return expression.evaluate(url, context);
    }

    @Override
    public void close() {
        closeWebDriver();
    }
}
