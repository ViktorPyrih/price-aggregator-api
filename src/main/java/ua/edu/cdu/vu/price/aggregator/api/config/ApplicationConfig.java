package ua.edu.cdu.vu.price.aggregator.api.config;

import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.edu.cdu.vu.price.aggregator.api.domain.Category;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ApplicationConfig {

    @Bean
    @ConditionalOnProperty(name = "price-aggregator-api.products.scrapping.concurrent", havingValue = "true", matchIfMissing = true)
    public ExecutorService taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    @ConditionalOnProperty(name = "price-aggregator-api.products.scrapping.concurrent", havingValue = "false")
    public ExecutorService localProductsScrapingExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public BeanOutputParser<Category> categoryBeanOutputParser() {
        return new BeanOutputParser<>(Category.class);
    }
}
