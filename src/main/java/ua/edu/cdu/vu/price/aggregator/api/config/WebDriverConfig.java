package ua.edu.cdu.vu.price.aggregator.api.config;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.edu.cdu.vu.price.aggregator.api.properties.WebDriverProperties;
import ua.edu.cdu.vu.price.aggregator.api.util.pool.FixedWebDriverPool;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class WebDriverConfig {

    private final WebDriverProperties webDriverProperties;

    @Bean
    public Supplier<WebDriver> webDriverFactory() {
        ChromeOptions options = new ChromeOptions();
        if (webDriverProperties.isHeadless()) {
            options.addArguments("--headless");
        }

        return () -> new ChromeDriver(options);
    }

    @Bean(initMethod = "initialize", destroyMethod = "close")
    public FixedWebDriverPool webDriverPool(Supplier<WebDriver> webDriverFactory) {
        return new FixedWebDriverPool(webDriverProperties.getPool().getCapacity(), webDriverProperties.getPool().getTimeoutMillis(), webDriverFactory);
    }
}
