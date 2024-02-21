package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import java.util.Map;

public class AnyDslCommand extends DslCommand<Iterable<Object>, Object> {

    @Override
    public Object executeInternal(String url, Iterable<Object> input, Map<String, Object> context) {
        var iterator = input.iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }
}
