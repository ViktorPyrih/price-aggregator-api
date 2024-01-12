package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.NonNull;

import java.util.Map;

public class InputDslCommand extends DslCommand<SelenideElement, Void> {

    @NonNull
    private final String value;

    public InputDslCommand(@NonNull Map<String, String> arguments) {
        this.value = arguments.get("value");
    }

    @Override
    public Void execute(String url, SelenideElement input, Map<String, String> context) {
        String resolvedValue = parse(value, context);
        input.sendKeys(resolvedValue);
        return null;
    }
}
