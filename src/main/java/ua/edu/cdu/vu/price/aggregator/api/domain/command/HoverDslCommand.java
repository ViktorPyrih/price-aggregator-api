package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static java.util.Objects.isNull;

public class HoverDslCommand extends DslCommand<SelenideElement, Void> {

    @Override
    public Void execute(String url, SelenideElement input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("Filter command executed on null input");
        }
        input.hover();
        return null;
    }
}
