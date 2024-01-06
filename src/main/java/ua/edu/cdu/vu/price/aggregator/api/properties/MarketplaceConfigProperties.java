package ua.edu.cdu.vu.price.aggregator.api.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value
@Validated
@ConfigurationProperties(prefix = "price-aggregator-api")
public class MarketplaceConfigProperties {

    @NotEmpty
    Map<@NotBlank String, @Valid Part> marketplaceConfig;

    public Part get(String marketplace) {
        return Optional.ofNullable(marketplaceConfig.get(marketplace))
                .orElseThrow(() -> new UnsupportedOperationException("Marketplace '%s' is not supported".formatted(marketplace)));
    }

    public record Part(@NotBlank String url,
                       @Valid MarketplaceConfigProperties.SelectorConfig categories,
                       @Valid MarketplaceConfigProperties.SelectorConfig subcategories1,
                       @Valid MarketplaceConfigProperties.SelectorConfig subcategories2) {
    }

    public record SelectorConfig(List<String> actions,
                                 @NotBlank String selector) {
    }
}
