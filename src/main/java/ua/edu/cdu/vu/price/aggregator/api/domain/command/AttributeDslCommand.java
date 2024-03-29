package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AttributeDslCommand extends DslCommand<Object, Object> {

    @NonNull
    private final String attribute;

    @Override
    public Object executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (input instanceof SelenideElement element) {
            return element.getAttribute(attribute);
        }

        if (input instanceof ElementsCollection collection) {
            return collection.asFixedIterable().stream()
                    .map(element -> element.getAttribute(attribute))
                    .toList();
        }

        throw new DslExecutionException("ATTRIBUTE command executed on not supported input");
    }
}
