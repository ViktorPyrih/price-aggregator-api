package ua.edu.cdu.vu.price.aggregator.api.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.SelenideWebDriver;

@Configuration
public class SelenideConfig {

    @Value("${price-aggregator-api.selenide.headless:true}")
    private boolean headless;

    @Value("${price-aggregator-api.selenide.timeout:4000}")
    private int timeout;

    @PostConstruct
    public void init() {
        com.codeborne.selenide.Configuration.headless = headless;
        com.codeborne.selenide.Configuration.timeout = timeout;
    }

    @Bean
    public SelenideWebDriver selenideWebDriver() {
        return new SelenideWebDriver();
    }
}
