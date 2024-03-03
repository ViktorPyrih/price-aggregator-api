package ua.edu.cdu.vu.price.aggregator.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfig {

    @Bean
    @ConditionalOnProperty(name = "price-aggregator-api.products.scrapping.concurrent", havingValue = "true", matchIfMissing = true)
    public ExecutorService productsScrapingExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    @ConditionalOnProperty(name = "price-aggregator-api.products.scrapping.concurrent", havingValue = "false")
    public ExecutorService localProductsScrapingExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
