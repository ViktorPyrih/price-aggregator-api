package ua.edu.cdu.vu.price.aggregator.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ua.edu.cdu.vu.price.aggregator.api.properties.MarketplaceConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(MarketplaceConfigProperties.class)
public class PriceAggregatorApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriceAggregatorApiApplication.class, args);
    }

}
