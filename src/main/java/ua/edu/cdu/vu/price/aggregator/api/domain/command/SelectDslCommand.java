package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$$;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class SelectDslCommand extends DslCommand<SelenideElement, ElementsCollection> {

    @NonNull
    private final String selector;

    @Override
    public ElementsCollection execute(String url, SelenideElement input, Map<String, String> context) {
        if (isNull(input)) {
            return $$(selector);
        }

        return input.$$(selector);
    }
}
