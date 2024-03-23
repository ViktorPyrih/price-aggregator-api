package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class LastDslCommand extends DslCommand<Iterable<Object>, Object> {

    public static final LastDslCommand INSTANCE = new LastDslCommand();

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
