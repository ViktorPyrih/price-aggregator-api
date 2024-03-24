package ua.edu.cdu.vu.price.aggregator.api.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import ua.edu.cdu.vu.price.aggregator.api.cache.ActionsUrlCacheManager;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Value
@Builder
public class DslEvaluationScenario<T> implements AutoCloseable {

    private static final int SECONDS_TO_SLEEP_BETWEEN_OPERATIONS = 5;
    private static final int SECONDS_TO_SLEEP_ON_CLOSE = 100;

    List<DslExpression<Void>> actions;
    @NonNull
    DslExpression<T> expression;
    boolean debug;
    @NonNull
    WebDriver webDriver;
    @NonNull
    ActionsUrlCacheManager cacheManager;

    public T run(String url, Map<String, Object> context) {
        log.debug("DSL evaluation scenario started with url: {} and context: {}", url, context);

        if (!CollectionUtils.isEmpty(actions)) {
            var urlAndRemainingActionsOptional = cacheManager.findUrlByActionsAndContext(actions, context);
            if (urlAndRemainingActionsOptional.isEmpty()) {
                evaluateActions(url, context);
            } else {
                var urlAndRemainingActions = urlAndRemainingActionsOptional.get();
                log.debug("Cache hit detected for url: {}", urlAndRemainingActions.getValue());
                webDriver.open(urlAndRemainingActions.getValue());
            }
        }

        log.debug("About to execute main expression: {}", expression);
        T result = expression.evaluate(url, context, webDriver);
        log.debug("Main expression: {} executed successfully", expression);

        return result;
    }

    @Override
    public void close() {
        if (debug) {
            log.debug("DSL evaluation scenario finished. About to sleep for debugging purposes for {} second(s)", SECONDS_TO_SLEEP_ON_CLOSE);
            sleep();
        }
        webDriver.close();
        log.debug("Web driver closed");
    }

    private void evaluateActions(String url, Map<String, Object> context) {
        var cachedActions = new LinkedList<DslExpression<Void>>();
        for (var action: actions) {
            String previousUrl = webDriver.url(url);

            log.debug("About to execute action: {}", action);
            action.evaluate(url, context, webDriver);
            log.debug("Action: {} executed successfully", action);

            String currentUrl = webDriver.url(url);
            cachedActions.add(action);
            if (!currentUrl.equals(previousUrl)) {
                cacheManager.put(cachedActions, currentUrl, context);
            }
        }
    }

    @SneakyThrows
    private void sleep() {
        TimeUnit.SECONDS.sleep(SECONDS_TO_SLEEP_ON_CLOSE);
    }
}
