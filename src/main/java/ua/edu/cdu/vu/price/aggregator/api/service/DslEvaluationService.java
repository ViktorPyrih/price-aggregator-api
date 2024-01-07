package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslEvaluationScenario;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationResponse;
import ua.edu.cdu.vu.price.aggregator.api.parser.DslExpressionParser;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DslEvaluationService {

    private final DslExpressionParser dslExpressionParser;

    @Cacheable("dsl-evaluation-cache")
    public <T> DslEvaluationResponse<T> evaluate(DslEvaluationRequest request) {
        String url = request.target().url();
        List<DslExpression<Void>> actions = Stream.ofNullable(request.actions())
                .flatMap(Collection::stream)
                .map(dslExpressionParser::<Void>parse)
                .toList();
        DslExpression<T> expression = dslExpressionParser.parse(request.expression());

        try (var scenario = DslEvaluationScenario.<T>builder()
                .actions(actions)
                .expression(expression)
                .build()) {
            return DslEvaluationResponse.<T>builder()
                    .value(scenario.run(url, request.arguments()))
                    .build();
        }
    }
}
