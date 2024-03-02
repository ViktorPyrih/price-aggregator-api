package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ByIdDslCommand extends DslCommand<Object, SelenideElement> implements StartDslCommand {

    @NonNull
    private final String id;

    @Override
    public SelenideElement executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (input == STUB) {
            return webDriver.getElementById(id);
        }

        if (input instanceof SelenideElement element) {
            return element.$(By.id(id));
        }

        throw new DslExecutionException("BY_ID command executed on not supported input");
    }
}
