package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$$;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class SelectDslCommand extends DslCommand<ElementsCollection, ElementsCollection> {

    @NonNull
    private final String selector;

    @Override
    public ElementsCollection execute(String url, ElementsCollection input, Map<String, String> context) {
        if (isNull(input)) {
            return $$(selector);
        }

        if (input.size() != 1) {
            throw new DslExecutionException("Cannot select on more than 1 element at the same time");
        }

        return input.get(0).$$(selector);
    }
}
