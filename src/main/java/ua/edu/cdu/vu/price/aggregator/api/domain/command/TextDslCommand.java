package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import lombok.EqualsAndHashCode;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@EqualsAndHashCode(callSuper = true)
public class TextDslCommand extends DslCommand<Object, Object> {

    @Override
    public Object execute(String url, Object input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("TEXT command executed on null input");
        }

        if (input instanceof ElementsCollection elements) {
            return elements.texts();
        }

        if (input instanceof List<?> list) {
            return list.stream()
                    .map(elements -> (ElementsCollection) elements)
                    .map(ElementsCollection::texts)
                    .toList();
        }

        return Collections.emptyList();
    }
}
