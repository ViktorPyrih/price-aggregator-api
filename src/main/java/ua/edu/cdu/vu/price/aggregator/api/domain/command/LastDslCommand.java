package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

import static java.util.Objects.isNull;

@EqualsAndHashCode(callSuper = true)
public class LastDslCommand extends DslCommand<Iterable<Object>, Object> {

    @Override
    Object executeInternal(String url, Iterable<Object> input, Map<String, Object> context, WebDriver webDriver) {
        Object result = null;
        for (Object element: input) {
            result = element;
        }

        if (isNull(result)) {
            throw new DslExecutionException("LAST command executed on empty element collection");
        }

        return result;
    }
}
