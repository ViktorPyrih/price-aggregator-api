package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.ArrayList;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class TextDslCommand extends DslCommand<Object, Object> {

    public static final TextDslCommand INSTANCE = new TextDslCommand();

    @Override
    Object executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
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
