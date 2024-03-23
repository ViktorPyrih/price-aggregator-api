package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class ToListDslCommand extends DslCommand<Collection<Object>, List<Object>> {

    public static final ToListDslCommand INSTANCE = new ToListDslCommand();

    @Override
    List<Object> executeInternal(String url, Collection<Object> input, Map<String, Object> context, WebDriver webDriver) {
        return input.stream().toList();
    }
}
