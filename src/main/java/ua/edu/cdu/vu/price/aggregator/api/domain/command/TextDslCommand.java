package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class TextDslCommand extends DslCommand<ElementsCollection, List<String>> {

    @Override
    public List<String> execute(String url, ElementsCollection input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("TEXT command executed on null input");
        }
        return input.texts();
    }
}
