package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class AnyDslCommand extends DslCommand<Iterable<Object>, Object> {

    @Override
    public Object executeInternal(String url, Iterable<Object> input, Map<String, Object> context, WebDriver webDriver) {
        var iterator = input.iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }
}
