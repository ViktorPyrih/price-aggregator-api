package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.OutputType;
import org.springframework.core.io.FileSystemResource;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static com.codeborne.selenide.Selenide.screenshot;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class ScreenshotDslCommand extends DslCommand<SelenideElement, FileSystemResource> {

    @Override
    public FileSystemResource execute(String url, SelenideElement input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("SCREENSHOT command executed on null input");
        }
        input.scrollIntoView(true);

        return new FileSystemResource(requireNonNull(screenshot(OutputType.FILE)));
    }
}
