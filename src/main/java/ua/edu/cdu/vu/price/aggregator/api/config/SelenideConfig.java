package ua.edu.cdu.vu.price.aggregator.api.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.SelenideWebDriver;

@Getter
@Setter
@Configuration
@ConfigurationProperties("price-aggregator-api.selenide")
public class SelenideConfig {

    private int timeout;
    private boolean screenshotsEnabled;
    private boolean pageSourceEnabled;

    @PostConstruct
    public void init() {
        com.codeborne.selenide.Configuration.timeout = timeout;
        com.codeborne.selenide.Configuration.screenshots = screenshotsEnabled;
        com.codeborne.selenide.Configuration.savePageSource = pageSourceEnabled;
    }

    @Bean
    public SelenideWebDriver selenideWebDriver() {
        return new SelenideWebDriver();
    }
}
