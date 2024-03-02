package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class OpenDslCommand extends DslCommand<Object, Object> {

    @Override
    public Object executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (!webDriver.isStarted()) {
            webDriver.open(url);
            webDriver.resizeWindow();
        }
        return StartDslCommand.STUB;
    }
}
