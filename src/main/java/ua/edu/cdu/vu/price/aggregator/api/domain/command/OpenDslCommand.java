package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import lombok.EqualsAndHashCode;

import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

@EqualsAndHashCode(callSuper = true)
public class OpenDslCommand extends DslCommand<Void, Void> {

    @Override
    public Void execute(String url, Void input, Map<String, Object> context) {
        if (!hasWebDriverStarted()) {
            open(url);
            resizeWindow();
        }
        return null;
    }
}
