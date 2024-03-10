package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ByIndexDslCommand extends DslCommand<Iterable<Object>, Object> {

    private static final int SLEEP_DURATION_MILLIS = 1000;

    private final int index;

    @Override
    @SneakyThrows
    public Object executeInternal(String url, Iterable<Object> input, Map<String, Object> context, WebDriver webDriver) {
        var result = executeInternal(input);

        if (result.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(SLEEP_DURATION_MILLIS);
            result = executeInternal(input);
        }

        return result.orElseThrow(() -> new DslExecutionException("No element found by index: %d".formatted(index)));
    }

    private Optional<Object> executeInternal(Iterable<Object> input) {
        int i = 0;
        for (var object: input) {
            if (i == index) {
                return Optional.ofNullable(object);
            }
            i++;
        }

        return Optional.empty();
    }
}
