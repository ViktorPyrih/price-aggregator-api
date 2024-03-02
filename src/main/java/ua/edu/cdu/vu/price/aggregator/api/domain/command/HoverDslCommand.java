package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class HoverDslCommand extends DslCommand<SelenideElement, Void> {

    @Override
    public Void executeInternal(String url, SelenideElement input, Map<String, Object> context, WebDriver webDriver) {
        input.hover();
        return null;
    }
}
