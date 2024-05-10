package ua.edu.cdu.vu.price.aggregator.api.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "price-aggregator-api.web-driver")
public class WebDriverProperties {

    private boolean headless;
    @Valid
    @NotNull
    private PoolProperties pool;

    @Getter
    @Setter
    public static class PoolProperties {

        @Positive
        private int capacity;
        @Positive
        private int timeoutMillis;

    }
}
