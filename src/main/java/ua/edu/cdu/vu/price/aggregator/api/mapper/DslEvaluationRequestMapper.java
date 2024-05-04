package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.cdu.vu.price.aggregator.api.domain.ProductsSelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.SearchSelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.domain.SelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;

import java.util.List;
import java.util.Map;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, imports = List.class)
public interface DslEvaluationRequestMapper {

    @Mapping(target = "target.url", source = "url")
    @Mapping(target = "expressions", source = "selectors")
    @Mapping(target = "actions", source = "selectorConfig.actions")
    @Mapping(target = "otherExpressions", source = "selectorConfig.other")
    @Mapping(target = "arguments", source = "arguments")
    DslEvaluationRequest convertToRequest(String url, SelectorConfig selectorConfig, Map<String, Object> arguments, String... selectors);

    default DslEvaluationRequest convertToRequest(String url, SelectorConfig selectorConfig, Map<String, Object> arguments) {
        return convertToRequest(url, selectorConfig, arguments, selectorConfig.selector());
    }

    @Mapping(target = "target.url", source = "url")
    @Mapping(target = "expressions", source = "selectors")
    @Mapping(target = "actions", source = "selectorConfig.actions")
    @Mapping(target = "otherExpressions", source = "selectorConfig.other")
    @Mapping(target = "arguments", source = "arguments")
    DslEvaluationRequest convertToRequest(String url, SearchSelectorConfig selectorConfig, Map<String, Object> arguments, String... selectors);

    @Mapping(target = "target.url", source = "url")
    @Mapping(target = "expressions", source = "selectors")
    @Mapping(target = "actions", source = "selectorConfig.self.actions")
    @Mapping(target = "otherExpressions", source = "selectorConfig.self.other")
    @Mapping(target = "arguments", source = "arguments")
    DslEvaluationRequest convertToRequest(String url, ProductsSelectorConfig selectorConfig, Map<String, Object> arguments, String... selectors);
}
