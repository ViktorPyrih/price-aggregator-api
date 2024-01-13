package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.springframework.core.io.FileSystemResource;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class ScreenshotDslCommand extends DslCommand<Object, Object> {

    @Override
    public Object execute(String url, Object input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("SCREENSHOT command executed on null input");
        }

        if (input instanceof SelenideElement element) {
            return screenshot(element);
        }

        if (input instanceof ElementsCollection collection) {
            return collection.asFixedIterable().stream()
                    .map(this::screenshot)
                    .toList();
        }

        return null;
    }

    private FileSystemResource screenshot(SelenideElement element) {
        element.scrollIntoView(false);
        return new FileSystemResource(requireNonNull(element.screenshot()));
    }
}
