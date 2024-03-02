package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslEvaluationScenario;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationResponse;
import ua.edu.cdu.vu.price.aggregator.api.parser.DslExpressionParser;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Collection;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DslEvaluationService {

    private final DslExpressionParser dslExpressionParser;
    private final WebDriver webDriver;

    @Value("${price-aggregator-api.dsl.evaluation.scenario.debug:false}")
    private boolean debug;

    @Cacheable("dsl-evaluation-cache")
    public <T> DslEvaluationResponse<T> evaluate(DslEvaluationRequest request) {
        String url = request.getTarget().url();
        var actions = Stream.ofNullable(request.getActions())
                .flatMap(Collection::stream)
                .map(dslExpressionParser::<Void>parse)
                .toList();
        DslExpression<T> expression = dslExpressionParser.parse(request.getExpression(), request.getOtherExpressions());

        try (var scenario = DslEvaluationScenario.<T>builder()
                .actions(actions)
                .expression(expression)
                .debug(debug)
                .webDriver(webDriver)
                .build()) {
            return DslEvaluationResponse.<T>builder()
                    .value(scenario.run(url, request.getArguments()))
                    .build();
        }
    }
}
