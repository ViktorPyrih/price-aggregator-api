package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.driver;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class SelectDslCommand extends DslCommand<Object, ElementsCollection> implements StartDslCommand {

    @NonNull
    private final String selector;

    @Override
    public ElementsCollection executeInternal(String url, Object input, Map<String, Object> context) {
        if (input == STUB) {
            return $$(selector);
        }

        if (input instanceof ElementsCollection collection) {
            return new ElementsCollection(driver(), collection.asFixedIterable().stream()
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
