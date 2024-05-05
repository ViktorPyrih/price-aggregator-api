package ua.edu.cdu.vu.price.aggregator.api.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value
@Validated
@RefreshScope
@ConfigurationProperties(prefix = "price-aggregator-api")
public class MarketplaceConfigProperties {

    @NotEmpty
    Map<@NotBlank String, @Valid Part> marketplaceConfig;

    public record Part(@NotBlank String url,
                       @NotNull @Valid MarketplaceConfigProperties.SelectorConfig categories,
                       @NotEmpty @Size(max = 2) List<MarketplaceConfigProperties.@Valid SelectorConfig> subcategories,
                       @NotNull @Valid MarketplaceConfigProperties.SelectorConfig filters,
                       @NotNull @Valid MarketplaceConfigProperties.ProductsSelectorConfig products,
                       @Valid MarketplaceConfigProperties.SearchSelectorConfig search) {
    }

    public record SelectorConfig(List<@NotBlank String> actions,
                                 @NotBlank String selector,
                                 List<@NotBlank String> other) {
    }

    public record ProductsSelectorConfig(@NotNull @Valid TemplateConfig filters,
                                         @NotNull @Valid SelectorConfig self) {

        public record SelectorConfig(List<@NotBlank String> actions,
                                     @NotBlank String imageSelector,
                                     @NotBlank String linkSelector,
                                     @NotBlank String priceSelector,
                                     @NotBlank String descriptionSelector,
                                     @NotBlank String pagesCountSelector,
                                     List<@NotBlank String> other) {
        }
    }

    public record SearchSelectorConfig(List<@NotBlank String> actions,
                                       @NotBlank String imageSelector,
                                       @NotBlank String linkSelector,
                                       @NotBlank String priceSelector,
                                       @NotBlank String descriptionSelector,
                                       @NotBlank String pagesCountSelector,
                                       List<@NotBlank String> other) {
    }

    public record TemplateConfig(@NotBlank String template) {
    }

    public Part get(String marketplace) {
        return Optional.ofNullable(marketplaceConfig.get(marketplace))
                .orElseThrow(() -> new UnsupportedOperationException("Marketplace '%s' is not supported".formatted(marketplace)));
    }

}
