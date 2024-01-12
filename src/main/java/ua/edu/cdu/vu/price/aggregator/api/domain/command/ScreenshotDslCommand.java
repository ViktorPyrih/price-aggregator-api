package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import org.springframework.core.io.FileSystemResource;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class ScreenshotDslCommand extends DslCommand<SelenideElement, FileSystemResource> {

    @Override
    public FileSystemResource execute(String url, SelenideElement input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("SCREENSHOT command executed on null input");
        }
        input.scrollIntoView(false);

        return new FileSystemResource(requireNonNull(input.screenshot()));
    }
}
