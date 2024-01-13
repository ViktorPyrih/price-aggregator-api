package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static java.util.Objects.isNull;

public class FirstDslCommand extends DslCommand<ElementsCollection, SelenideElement> {

    @Override
    public SelenideElement execute(String url, ElementsCollection input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("FIRST command executed on null input");
        }

        if (input.isEmpty()) {
            throw new DslExecutionException("First command executed on empty element collection");
        }

        return input.get(0);
    }
}
