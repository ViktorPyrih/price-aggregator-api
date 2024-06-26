package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.io.File;
import java.util.Map;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class ScreenshotDslCommand extends DslCommand<Object, Object> {

    private static final String SCROLL_OPTIONS = """
            {
                block: "center",
                inline: "center"
            }
    """;

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
        element.scrollIntoView(SCROLL_OPTIONS);
        File screenshot = element.screenshot();

        if (isNull(screenshot)) {
            throw new DslExecutionException("Unable to take a screenshot of element: %s".formatted(element));
        }

        return new FileSystemResource(screenshot);
    }
}
