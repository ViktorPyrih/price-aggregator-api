package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class ScreenshotDslCommand extends DslCommand<Object, Object> {

    public static final ScreenshotDslCommand INSTANCE = new ScreenshotDslCommand();

    @Override
    Object executeInternal(String url, Object input, Map<String, Object> context, WebDriver webDriver) {
        if (input instanceof SelenideElement element) {
            return screenshot(element);
        }

        if (input instanceof ElementsCollection collection) {
            return collection.asFixedIterable().stream()
                    .map(this::screenshot)
                    .toList();
        }

        throw new DslExecutionException("SCREENSHOT command executed on not supported input");
    }

    private FileSystemResource screenshot(SelenideElement element) {
        element.scrollIntoView(true);
        return new FileSystemResource(requireNonNull(element.screenshot()));
    }
}
