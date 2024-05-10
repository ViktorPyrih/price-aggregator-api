package ua.edu.cdu.vu.price.aggregator.api.util.pool;

import org.openqa.selenium.WebDriver;

public interface WebDriverPool {

    void initialize();

    WebDriver getDriver();

    void close();
}
