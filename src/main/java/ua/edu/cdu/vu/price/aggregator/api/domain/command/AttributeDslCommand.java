package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static java.util.Objects.isNull;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AttributeDslCommand extends DslCommand<Object, Object> {

    @NonNull
    private final String attribute;

    @Override
    public Object execute(String url, Object input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("Attribute command executed on null input");
        }

        if (input instanceof SelenideElement element) {
            return element.getAttribute(attribute);
        }

        if (input instanceof ElementsCollection collection) {
            return collection.asFixedIterable().stream()
                    .map(element -> element.getAttribute(attribute))
                    .toList();
        }

        return null;
    }
}
