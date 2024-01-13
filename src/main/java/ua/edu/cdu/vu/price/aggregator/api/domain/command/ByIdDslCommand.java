package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class ByIdDslCommand extends DslCommand<SelenideElement, SelenideElement> {

    @NonNull
    private final String id;

    @Override
    public SelenideElement execute(String url, SelenideElement input, Map<String, Object> context) {
        if (isNull(input)) {
            return $(By.id(id));
        }

        return input.$(By.id(id));
    }
}
