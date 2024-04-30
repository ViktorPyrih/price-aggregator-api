package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class EnterDslCommand extends DslCommand<SelenideElement, Void> {

    public static final EnterDslCommand INSTANCE = new EnterDslCommand();

    @Override
    Void executeInternal(String url, SelenideElement input, Map<String, Object> context, WebDriver webDriver) {
        input.pressEnter();
        return null;
    }
}
