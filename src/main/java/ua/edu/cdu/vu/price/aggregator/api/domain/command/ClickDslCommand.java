package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class ClickDslCommand extends DslCommand<SelenideElement, Void> {

    public static final ClickDslCommand INSTANCE = new ClickDslCommand();

    @Override
    Void executeInternal(String url, SelenideElement input, Map<String, Object> context, WebDriver webDriver) {
        input.scrollIntoView(false);
        input.click();
        return null;
    }
}
