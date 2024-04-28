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

    private static final int NO_CACHE_HIT_INDEX = -1;

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
            var actionIndexAndUrlOptional = cacheManager.findUrlByActionsAndContext(actions, context);
            if (actionIndexAndUrlOptional.isEmpty()) {
                evaluateActions(url, context, NO_CACHE_HIT_INDEX);
            } else {
                var actionIndexAndUrl = actionIndexAndUrlOptional.get();
                log.debug("Cache hit detected for url: {}, index: {}", actionIndexAndUrl.getValue(), actionIndexAndUrl.getKey());
                webDriver.open(actionIndexAndUrl.getValue());
                if (actionIndexAndUrl.getKey() != actions.size() - 1) {
                    evaluateActions(actionIndexAndUrl.getValue(), context, actionIndexAndUrl.getKey());
                }
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

    private void evaluateActions(String url, Map<String, Object> context, int index) {
        var cachedActions = new LinkedList<>(actions.subList(0, index + 1));
        for (int i = index + 1; i < actions.size(); i++) {
            String previousUrl = webDriver.url(url);

            log.debug("About to execute action: {}", actions.get(i));
            actions.get(i).evaluate(url, context, webDriver);
            log.debug("Action: {} executed successfully", actions.get(i));

            String currentUrl = webDriver.url(url);
            cachedActions.add(actions.get(i));
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
