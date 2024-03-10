package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.LinkedList;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class TrimDslCommand extends DslCommand<Object, Object> {

    @Override
    public Object executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (input instanceof Iterable<?> iterable) {
            var result = new LinkedList<String>();
            for (Object object: iterable) {
                if (object instanceof String text) {
                    String trimmedText = text.trim();
                    if (!trimmedText.isEmpty()) {
                        result.add(trimmedText);
                    }
                } else {
                    throw new DslExecutionException("TRIM command executed on non-string element");
                }
            }

            return result;
        } else if (input instanceof String text) {
            return text.trim();
        }

        throw new DslExecutionException("TRIM command executed on non-string element");
    }
}
