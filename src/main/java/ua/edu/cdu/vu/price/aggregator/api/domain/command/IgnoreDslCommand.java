package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class IgnoreDslCommand extends DslCommand<Iterable<Object>, Iterable<Object>> {

    @NonNull
    private final Set<Integer> indexes;

    @Override
    public Iterable<Object> executeInternal(String url, Iterable<Object> input, Map<String, Object> context) {
        var iterator = input.iterator();
        var remainingElements = new LinkedList<>();
        for (int i = 0; iterator.hasNext(); i++) {
            Object element = iterator.next();
            if (!indexes.contains(i)) {
                remainingElements.add(element);
            }
        }

        return remainingElements;
    }
}
