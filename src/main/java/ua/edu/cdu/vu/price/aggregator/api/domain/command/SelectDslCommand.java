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
public class SelectDslCommand extends DslCommand<Object, ElementsCollection> implements StartDslCommand {

    @NonNull
    private final String selector;

    @Override
    public ElementsCollection executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (input == STUB) {
            return webDriver.getAllElementsByCssSelector(selector);
        }

        if (input instanceof ElementsCollection collection) {
            return new ElementsCollection(webDriver.unwrap(), collection.asFixedIterable().stream()
                    .map(element -> element.$$(selector))
                    .map(ElementsCollection::asFixedIterable)
                    .flatMap(ElementsCollection.SelenideElementIterable::stream)
                    .toList());
        }

        if (input instanceof SelenideElement element) {
            return element.$$(selector);
        }

        throw new DslExecutionException("SELECT command executed on not supported input");
    }
}
