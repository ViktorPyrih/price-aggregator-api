package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class AnyDslCommand extends DslCommand<Iterable<Object>, Object> {

    public static final AnyDslCommand INSTANCE = new AnyDslCommand();

    @Override
    Object executeInternal(String url, Iterable<Object> input, Map<String, Object> context, WebDriver webDriver) {
        var iterator = input.iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }
}
