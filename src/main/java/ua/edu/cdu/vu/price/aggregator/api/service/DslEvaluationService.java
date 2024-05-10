package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.cache.ActionsUrlCacheManager;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslEvaluationScenario;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationRequest;
import ua.edu.cdu.vu.price.aggregator.api.dto.DslEvaluationResponse;
import ua.edu.cdu.vu.price.aggregator.api.parser.DslExpressionParser;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;
import ua.edu.cdu.vu.price.aggregator.api.util.pool.WebDriverPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DslEvaluationService {

    private static final String PROXY = "proxy";

    private final DslExpressionParser dslExpressionParser;
    private final WebDriver webDriver;
    private final ActionsUrlCacheManager cacheManager;
    private final WebDriverPool webDriverPool;

    @Value("${price-aggregator-api.dsl.evaluation.scenario.debug:false}")
    private boolean debug;

    @Value("${price-aggregator-api.selenide.http.proxy:}")
    private String proxy;

    public DslEvaluationResponse evaluate(DslEvaluationRequest request) {
        String url = request.getTarget().url();
        var actions = Stream.ofNullable(request.getActions())
                .flatMap(Collection::stream)
                .map(dslExpressionParser::parse)
                .toList();
        var expressions = request.getExpressions().stream()
                .map(expression -> dslExpressionParser.parse(expression, request.getOtherExpressions()))
                .toList();
        var arguments = new HashMap<>(request.getArguments());
        if (!proxy.isBlank()) {
            arguments.put(PROXY, proxy);
        }

        try (var scenario = DslEvaluationScenario.builder()
                .actions(actions)
                .expressions(expressions)
                .debug(debug)
                .webDriver(webDriver)
                .cacheManager(cacheManager)
                .webDriverPool(webDriverPool)
                .build()) {
            return DslEvaluationResponse.builder()
                    .values(scenario.run(url, arguments))
                    .build();
        }
    }
}
