package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class OpenDslCommand extends DslCommand<Object, Object> {

    public static final OpenDslCommand INSTANCE = new OpenDslCommand();

    @Override
    public Object executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (!webDriver.isStarted()) {
            if (context.containsKey("proxy")) {
                webDriver.setHttpProxy((String) context.get("proxy"));
            }
            webDriver.open(url);
            webDriver.resizeWindow();
        }
        return StartDslCommand.STUB;
    }
}
