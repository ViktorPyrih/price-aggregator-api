package ua.edu.cdu.vu.price.aggregator.api.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SelenideConfig {

    @Value("${price-aggregator-api.selenide.headless}")
    private boolean headless;

    @PostConstruct
    public void init() {
        com.codeborne.selenide.Configuration.headless = headless;
    }
}
