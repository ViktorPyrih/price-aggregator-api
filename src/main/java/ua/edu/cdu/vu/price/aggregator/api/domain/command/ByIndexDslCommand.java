package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

@RequiredArgsConstructor
public class ByIndexDslCommand extends DslCommand<Iterable<Object>, Object> {

    private final int index;

    @Override
    public Object executeInternal(String url, Iterable<Object> input, Map<String, Object> context) {
        int i = 0;
        for (var object: input) {
            if (i == index) {
                return object;
            }
            i++;
        }

        throw new DslExecutionException("No element found by index: %d".formatted(index));
    }
}
