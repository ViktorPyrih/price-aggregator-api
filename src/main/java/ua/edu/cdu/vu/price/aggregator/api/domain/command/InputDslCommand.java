package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.NonNull;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static java.util.Objects.isNull;

public class InputDslCommand extends DslCommand<SelenideElement, Void> {

    @NonNull
    private final String value;

    public InputDslCommand(@NonNull Map<String, String> arguments) {
        this.value = arguments.get("value");
    }

    @Override
    public Void execute(String url, SelenideElement input, Map<String, Object> context) {
        if (isNull(input)) {
            throw new DslExecutionException("INPUT command executed on null input");
        }
        String resolvedValue = parse(value, context);
        input.sendKeys(resolvedValue);
        return null;
    }
}
