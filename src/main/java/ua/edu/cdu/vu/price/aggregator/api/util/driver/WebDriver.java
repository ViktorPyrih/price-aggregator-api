package ua.edu.cdu.vu.price.aggregator.api.util.driver;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Dimension;

import java.util.Optional;


public interface WebDriver {

    int WINDOW_WIDTH = 4048;
    int WINDOW_HEIGHT = 4048;
    String INITIAL_URL = "data:,";

    void setDriver(org.openqa.selenium.WebDriver webDriver);

    void open(String url);

    void close();

    ElementsCollection getAllElementsByCssSelector(String selector);

    SelenideElement getElementById(String id);

    void setHttpProxy(String proxy);

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
        return !INITIAL_URL.equals(unwrap().url());
    }

    default void resizeWindow() {
        unwrap().getWebDriver().manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    }
}
