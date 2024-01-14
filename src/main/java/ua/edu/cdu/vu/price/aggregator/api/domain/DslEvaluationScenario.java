package ua.edu.cdu.vu.price.aggregator.api.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static java.util.Objects.nonNull;

@Value
@Builder
public class DslEvaluationScenario<T> implements AutoCloseable {

    public record CacheEntry(DslExpression<Cacheable<String>> action) {
    }

    private static final Map<CacheEntry, String> URL_CACHE = new ConcurrentHashMap<>();

    List<DslExpression<Cacheable<String>>> actions;
    @NonNull DslExpression<T> expression;

    public T run(String url, Map<String, Object> context) {
        Stream.ofNullable(actions)
                .flatMap(List::stream)
                .forEach(action -> execute(url, context, action));

        return expression.evaluate(url, context);
    }

    @Override
    public void close() {
        closeWebDriver();
    }

    private void execute(String url, Map<String, Object> context, DslExpression<Cacheable<String>> action) {
        CacheEntry entry = new CacheEntry(action);
        if (URL_CACHE.containsKey(entry)) {
            open(URL_CACHE.get(entry));
        } else {
            var cacheable = action.evaluate(url, context);
            if (nonNull(cacheable)) {
                URL_CACHE.put(entry, cacheable.value());
            }
        }
    }
}
