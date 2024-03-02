package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.conditions.InnerText;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslValidationException;
import ua.edu.cdu.vu.price.aggregator.api.util.InnerTextBySelector;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@EqualsAndHashCode(callSuper = true)
public class FilterDslCommand extends DslCommand<ElementsCollection, ElementsCollection> {

    private final String selector;
    private final String text;

    public FilterDslCommand(@NonNull Map<String, String> arguments) {
        this.selector = arguments.get("selector");
        this.text = requireNonNull(arguments.get("text"));
    }

    @Override
    public ElementsCollection executeInternal(String url, ElementsCollection input, Map<String, Object> context, WebDriver webDriver) {
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
