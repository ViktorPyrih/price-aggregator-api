package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.util.BooleanUtils;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
public class ClickDslCommand extends DslCommand<SelenideElement, Void> {

    private final boolean alignToTop;

    public ClickDslCommand(Map<String, String> arguments) {
        this.alignToTop = Optional.ofNullable(arguments.get("alignToTop"))
                .map(BooleanUtils::tryParse)
                .orElse(false);
    }

    @Override
    Void executeInternal(String url, SelenideElement input, Map<String, Object> context, WebDriver webDriver) {
        input.scrollIntoView(alignToTop);
        input.click();
        return null;
    }
}
