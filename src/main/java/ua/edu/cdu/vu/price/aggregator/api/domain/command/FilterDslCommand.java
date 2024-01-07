package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.conditions.InnerText;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;
import ua.edu.cdu.vu.price.aggregator.api.util.InnerTextBySelector;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class FilterDslCommand extends DslCommand<ElementsCollection, ElementsCollection> {

    private final String selector;
    @NonNull
    private final String text;

    public FilterDslCommand(@NonNull Map<String, String> arguments) {
        this.selector = arguments.get("selector");
        this.text = requireNonNull(arguments.get("text"));
    }

    @Override
    public ElementsCollection execute(String url, ElementsCollection input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("FILTER command executed on null input");
        }

        String resolvedText = parse(text, context);

        if (isNull(resolvedText)) {
            throw new DslValidationException("Expression: %s shouldn't return null".formatted(text));
        }

        if (isNull(selector)) {
            return input.filter(new InnerText(resolvedText));
        }

        return input.filter(new InnerTextBySelector(selector, resolvedText));
    }
}
