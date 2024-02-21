package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class HoverDslCommand extends DslCommand<SelenideElement, Void> {

    @Override
    public Void executeInternal(String url, SelenideElement input, Map<String, Object> context) {
        input.hover();
        return null;
    }
}
