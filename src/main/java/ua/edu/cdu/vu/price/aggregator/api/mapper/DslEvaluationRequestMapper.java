package ua.edu.cdu.vu.price.aggregator.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.cdu.vu.price.aggregator.api.domain.SelectorConfig;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;

import java.util.Map;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DslEvaluationRequestMapper {

    @Mapping(target = "target.url", source = "url")
    @Mapping(target = "expression", source = "selectorConfig.selector")
    @Mapping(target = "actions", source = "selectorConfig.actions")
    @Mapping(target = "arguments", source = "arguments")
    DslEvaluationRequest convertToRequest(String url, SelectorConfig selectorConfig, Map<String, String> arguments);

    default DslEvaluationRequest convertToRequest(String url, SelectorConfig selectorConfig) {
        return convertToRequest(url, selectorConfig, Map.of());
    }
}
