package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.cache.ActionsUrlCacheManager;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslEvaluationScenario;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationResponse;
import ua.edu.cdu.vu.price.aggregator.api.parser.DslExpressionParser;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DslEvaluationService {

    private static final String PROXY = "proxy";

    private final DslExpressionParser dslExpressionParser;
    private final WebDriver webDriver;
    private final ActionsUrlCacheManager cacheManager;

    @Value("${price-aggregator-api.dsl.evaluation.scenario.debug:false}")
    private boolean debug;

    @Value("${price-aggregator-api.selenide.http.proxy:}")
    private String proxy;

    public <T> DslEvaluationResponse<T> evaluate(DslEvaluationRequest request) {
        String url = request.getTarget().url();
        var actions = Stream.ofNullable(request.getActions())
                .flatMap(Collection::stream)
                .map(dslExpressionParser::<Void>parse)
                .toList();
        DslExpression<T> expression = dslExpressionParser.parse(request.getExpression(), request.getOtherExpressions());
        Map<String, Object> arguments = new HashMap<>(request.getArguments()) {{
            if (!proxy.isBlank()) {
                put(PROXY, proxy);
            }
        }};

        try (var scenario = DslEvaluationScenario.<T>builder()
                .actions(actions)
                .expression(expression)
                .debug(debug)
                .webDriver(webDriver)
                .cacheManager(cacheManager)
                .build()) {
            return DslEvaluationResponse.<T>builder()
                    .value(scenario.run(url, arguments))
                    .build();
        }
    }
}
