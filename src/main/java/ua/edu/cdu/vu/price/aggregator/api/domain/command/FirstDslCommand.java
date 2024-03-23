package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class FirstDslCommand extends DslCommand<Iterable<Object>, Object> {

    public static final FirstDslCommand INSTANCE = new FirstDslCommand();

    @Override
    Object executeInternal(String url, Iterable<Object> input, Map<String, Object> context, WebDriver webDriver) {
        var iterator = input.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }

        throw new DslExecutionException("FIRST command executed on empty element collection");
    }
}
