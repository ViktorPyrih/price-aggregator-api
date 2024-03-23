package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class DistinctDslCommand extends DslCommand<Iterable<Object>, Set<Object>> {

    public static final DistinctDslCommand INSTANCE = new DistinctDslCommand();

    @Override
    Set<Object> executeInternal(String url, Iterable<Object> input, Map<String, Object> context, WebDriver webDriver) {
        var set = new LinkedHashSet<>();
        for (Object o : input) {
            set.add(o);
        }

        return set;
    }
}
