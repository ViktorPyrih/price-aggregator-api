package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class ToListDslCommand extends DslCommand<Collection<Object>, List<Object>> {

    @Override
    List<Object> executeInternal(String url, Collection<Object> input, Map<String, Object> context, WebDriver webDriver) {
        return input.stream().toList();
    }
}
