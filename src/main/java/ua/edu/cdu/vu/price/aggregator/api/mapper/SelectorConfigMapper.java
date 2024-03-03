package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.cdu.vu.price.aggregator.api.domain.ProductsSelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.SelectorConfig;

import java.util.function.Function;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface SelectorConfigMapper {

    @Mapping(target = "actions", source = "productsSelectorConfig.self.actions")
    @Mapping(target = "selector", expression = "java(extractor.apply(productsSelectorConfig.self()))")
    @Mapping(target = "other", source = "productsSelectorConfig.self.other")
    SelectorConfig convertToSelectorConfig(ProductsSelectorConfig productsSelectorConfig, Function<ProductsSelectorConfig.SelectorConfig, String> extractor);
}
