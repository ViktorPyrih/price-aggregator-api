package ua.edu.cdu.vu.price.aggregator.api.util.driver;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Dimension;

import java.util.Optional;


public interface WebDriver {

    int WINDOW_WIDTH = 4048;
    int WINDOW_HEIGHT = 4048;

    void open(String url);

    void close();

    ElementsCollection getAllElementsByCssSelector(String selector);

    SelenideElement getElementById(String id);

    Driver unwrap();

    default Optional<String> url() {
        if (isStarted()) {
            return Optional.of(unwrap().url());
        }

        return Optional.empty();
    }

    default String url(String defaultUrl) {
        return url().orElse(defaultUrl);
    }

    default boolean isStarted() {
        return unwrap().hasWebDriverStarted();
    }

    default void resizeWindow() {
        unwrap().getWebDriver().manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    }
}
