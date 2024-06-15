package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.util.BooleanUtils;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
public class ClickDslCommand extends DslCommand<Object, Void> {

    private final boolean alignToTop;

    public ClickDslCommand(Map<String, String> arguments) {
        this.alignToTop = Optional.ofNullable(arguments.get("alignToTop"))
                .map(BooleanUtils::tryParse)
                .orElse(false);
    }

    @Override
    Void executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (input instanceof SelenideElement element) {
            click(element);
        } else if (input instanceof ElementsCollection collection) {
            collection.forEach(this::click);
        }
        return null;
    }

    private void click(SelenideElement element) {
        element.scrollIntoView(alignToTop);
        if (element.isDisplayed()) {
            element.click();
        }
    }
}
