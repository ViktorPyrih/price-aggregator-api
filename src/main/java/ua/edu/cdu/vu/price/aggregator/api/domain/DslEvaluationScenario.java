package ua.edu.cdu.vu.price.aggregator.api.domain;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import ua.edu.cdu.vu.price.aggregator.api.util.driver.WebDriver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@Slf4j
@Value
@Builder
public class DslEvaluationScenario<T> implements AutoCloseable {

    private static final int SECONDS_TO_SLEEP_BETWEEN_OPERATIONS = 5;
    private static final int SECONDS_TO_SLEEP_ON_CLOSE = 100;

    private static final Cache<Cacheable<List<DslExpression<Void>>>, String> URL_CACHE = CacheBuilder.newBuilder()
            .softValues()
            .build();

    List<DslExpression<Void>> actions;
    @NonNull DslExpression<T> expression;
    boolean debug;
    @NonNull WebDriver webDriver;

    public T run(String url, Map<String, Object> context) {
        log.debug("DSL evaluation scenario started with url: {} and context: {}", url, context);

        var key = new Cacheable<>(Optional.ofNullable(actions).orElse(Collections.emptyList()));
        String urlFromCache = URL_CACHE.getIfPresent(key);

        if (!CollectionUtils.isEmpty(actions)) {
            if (isNull(urlFromCache)) {
                actions.forEach(action -> {
                    log.debug("About to execute action: {}", action);
                    sleep(SECONDS_TO_SLEEP_BETWEEN_OPERATIONS);
                    action.evaluate(url, context, webDriver);
                    log.debug("Action: {} executed successfully", action);
                });
                if (webDriver.isStarted()) {
                    log.debug("URL: {} will be cached", webDriver.url());
                    URL_CACHE.put(key, webDriver.url());
                }
            } else {
                log.debug("Cache hit detected for url: {}", urlFromCache);
                webDriver.open(urlFromCache);
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
            sleep(SECONDS_TO_SLEEP_ON_CLOSE);
        }
        webDriver.close();
        log.debug("Web driver closed");
    }

    @SneakyThrows
    private void sleep(int seconds) {
        if (debug) {
            TimeUnit.SECONDS.sleep(seconds);
        }
    }
}
