package ua.edu.cdu.vu.price.aggregator.api.util.driver;

import com.codeborne.selenide.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SelenideWebDriver implements WebDriver {

    private static final Duration TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);
    private static final ExpectedCondition<Boolean> PAGE_LOAD_CONDITION = driver -> ((JavascriptExecutor) Objects.requireNonNull(driver))
            .executeScript("return document.readyState").equals("complete");

    @Override
    public void setDriver(org.openqa.selenium.WebDriver webDriver) {
        WebDriverRunner.setWebDriver(webDriver);
    }

    @Override
    public void open(String url) {
        Selenide.open(url);
        waitForPageLoad(url);
    }

    @Override
    public void waitForPageLoad(String expectedUrl) {
        org.openqa.selenium.WebDriver driver = unwrap().getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(d -> d.getCurrentUrl().equals(expectedUrl));
        waitForPageLoad();
    }

    @Override
    public void waitForPageLoad() {
        org.openqa.selenium.WebDriver driver = unwrap().getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(PAGE_LOAD_CONDITION);
    }

    @Override
    public void close() {
        // as a workaround to reuse web driver instances
        try {
            open(INITIAL_URL);
        } finally {
            Selenide.closeWebDriver();
        }
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
        return WebDriverRunner.driver();
    }
}
