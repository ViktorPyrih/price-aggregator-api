package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

public class OpenDslCommand extends DslCommand<Void, Void> {

    @Override
    public Void execute(String url, Void input, Map<String, String> context) {
        if (!hasWebDriverStarted()) {
            open(url);
            webdriver().object().manage().window().fullscreen();
        }
        return null;
    }
}
