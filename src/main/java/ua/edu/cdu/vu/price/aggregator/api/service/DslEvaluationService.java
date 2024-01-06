package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
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

    public DslEvaluationResponse evaluate(DslEvaluationRequest request) {
        String url = request.target().url();
        List<DslExpression<Void>> actions = Stream.ofNullable(request.actions())
                .flatMap(Collection::stream)
                .map(dslExpressionParser::<Void>parse)
                .toList();
        DslExpression<List<String>> expression = dslExpressionParser.parse(request.expression());

        try (var scenario = DslEvaluationScenario.<List<String>>builder()
                .actions(actions)
                .expression(expression)
                .build()) {
            return DslEvaluationResponse.builder()
                    .values(scenario.run(url, request.arguments()))
                    .build();
        }
    }
}
