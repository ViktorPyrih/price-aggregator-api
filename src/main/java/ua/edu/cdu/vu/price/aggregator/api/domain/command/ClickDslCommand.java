package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;

import java.util.Map;

public class ClickDslCommand extends DslCommand<SelenideElement, Void> {

    @Override
    public Void executeInternal(String url, SelenideElement input, Map<String, Object> context) {
        input.click();
        return null;
    }
}
