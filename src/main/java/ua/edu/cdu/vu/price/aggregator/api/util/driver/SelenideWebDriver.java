package ua.edu.cdu.vu.price.aggregator.api.util.driver;

import com.codeborne.selenide.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.driver;

public class SelenideWebDriver implements WebDriver {

    @Override
    public void open(String url) {
        Selenide.open(url);
    }

    @Override
    public void close() {
        closeWebDriver();
    }

    @Override
    public ElementsCollection getAllElementsByCssSelector(String selector) {
        return $$(selector);
    }

    @Override
    public SelenideElement getElementById(String id) {
        return $(By.id(id));
    }

    @Override
    public void setHttpProxy(String proxy) {
        WebDriverRunner.setProxy(new Proxy().setHttpProxy(proxy));
    }

    @Override
    public Driver unwrap() {
        return driver();
    }
}
