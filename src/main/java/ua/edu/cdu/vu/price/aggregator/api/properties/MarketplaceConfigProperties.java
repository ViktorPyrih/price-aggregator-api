package ua.edu.cdu.vu.price.aggregator.api.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Value
@Validated
@ConfigurationProperties(prefix = "price-aggregator-api")
public class MarketplaceConfigProperties {

    @NotEmpty
    Map<String, @Valid Part> marketplaceConfig;

    @Value
    public static class Part {
        @NotBlank
        String url;
        @Valid
        SelectorConfig categories;
        @Valid
        SelectorConfig subcategories1;
        @Valid
        SelectorConfig subcategories2;
    }

    @Value
    public static class SelectorConfig {
        List<String> actions;
        @NotBlank
        String selector;
    }
}
