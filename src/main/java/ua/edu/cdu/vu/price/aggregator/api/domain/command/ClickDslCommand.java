package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import ua.edu.cdu.vu.price.aggregator.api.domain.Cacheable;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static com.codeborne.selenide.WebDriverRunner.driver;
import static java.util.Objects.isNull;

public class ClickDslCommand extends DslCommand<SelenideElement, Cacheable<String>> {

    @Override
    public Cacheable<String> execute(String url, SelenideElement input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("CLICK command executed on null input");
        }

        input.click();

        if (url.equals(driver().url())) {
            return null;
        }

        return new Cacheable<>(driver().url());
    }
}
