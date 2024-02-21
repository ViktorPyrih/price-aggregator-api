package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Map;

@RequiredArgsConstructor
public class WaitDslCommand extends DslCommand<Object, Object> implements StartDslCommand {

    private final long millis;

    @Override
    @SneakyThrows
    public Object executeInternal(String url, Object input, Map<String, Object> context) {
        Thread.sleep(millis);
        return input;
    }
}
