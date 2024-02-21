package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class DistinctDslCommand extends DslCommand<Iterable<Object>, Set<Object>> {

    @Override
    public Set<Object> executeInternal(String url, Iterable<Object> input, Map<String, Object> context) {
        var set = new LinkedHashSet<>();
        for (Object o : input) {
            set.add(o);
        }

        return set;
    }
}
