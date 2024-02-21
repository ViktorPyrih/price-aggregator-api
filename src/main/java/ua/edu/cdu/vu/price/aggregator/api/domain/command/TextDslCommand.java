package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.ArrayList;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class TextDslCommand extends DslCommand<Object, Object> {

    @Override
    public Object executeInternal(String url, Object input, Map<String, Object> context) {
        if (input instanceof Iterable<?> iterable) {
            var result = new ArrayList<>();
            for (var element: iterable) {
                if (element instanceof SelenideElement selenideElement) {
                    result.add(selenideElement.text());
                } else if (element instanceof ElementsCollection collection) {
                    result.add(collection.texts());
                }
            }

            return result;
        }

        if (input instanceof SelenideElement element) {
            return element.text();
        }

        throw new DslExecutionException("TEXT command executed on not supported input");
    }
}
