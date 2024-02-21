package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static java.util.Objects.*;

@EqualsAndHashCode(callSuper = true)
public class InputDslCommand extends DslCommand<SelenideElement, Void> {

    private final String value;

    public InputDslCommand(@NonNull Map<String, String> arguments) {
        this.value = requireNonNull(arguments.get("value"));
    }

    @Override
    public Void executeInternal(String url, SelenideElement input, Map<String, Object> context) {
        String resolvedValue = parse(value, context);
        input.setValue(StringUtils.EMPTY);
        input.setValue(resolvedValue);
        return null;
    }
}
