package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@RequiredArgsConstructor
public class WaitDslCommand extends DslCommand<Object, Object> implements StartDslCommand {

    private final long millis;

    @Override
    @SneakyThrows
    public Object executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        Thread.sleep(millis);
        return input;
    }
}
