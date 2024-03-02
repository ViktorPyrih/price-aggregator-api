package ua.edu.cdu.vu.price.aggregator.api.util.driver;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Dimension;


public interface WebDriver {

    int WINDOW_WIDTH = 2048;
    int WINDOW_HEIGHT = 2048;

    void open(String url);

    void close();

    ElementsCollection getAllElementsByCssSelector(String selector);

    SelenideElement getElementById(String id);

    Driver unwrap();

    default String url() {
        return unwrap().url();
    }

    default boolean isStarted() {
        return unwrap().hasWebDriverStarted();
    }

    default void resizeWindow() {
        unwrap().getWebDriver().manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    }
}
