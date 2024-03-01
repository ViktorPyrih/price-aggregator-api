package ua.edu.cdu.vu.price.aggregator.api.domain;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.driver;
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

    public T run(String url, Map<String, Object> context) {
        log.debug("DSL evaluation scenario started with url: {} and context: {}", url, context);

        var key = new Cacheable<>(Optional.ofNullable(actions).orElse(Collections.emptyList()));
        String urlFromCache = URL_CACHE.getIfPresent(key);

        if (isNull(urlFromCache)) {
            Stream.ofNullable(actions)
                    .flatMap(List::stream)
                    .forEach(action -> {
                        log.debug("About to execute action: {}", action);
                        sleep(SECONDS_TO_SLEEP_BETWEEN_OPERATIONS);
                        action.evaluate(url, context);
                        log.debug("Action: {} executed successfully", action);
                    });
            log.debug("URL: {} will be cached", driver().url());
            URL_CACHE.put(key, driver().url());
        } else {
            log.debug("Cache hit detected for url: {}", urlFromCache);
            open(urlFromCache);
        }

        log.debug("About to execute main expression: {}", expression);
        T result = expression.evaluate(url, context);
        log.debug("Main expression: {} executed successfully", expression);

        return result;
    }

    @Override
    public void close() {
        if (debug) {
            log.debug("DSL evaluation scenario finished. About to sleep for debugging purposes for {} second(s)", SECONDS_TO_SLEEP_ON_CLOSE);
            sleep(SECONDS_TO_SLEEP_ON_CLOSE);
        }
        closeWebDriver();
        log.debug("Web driver closed");
    }

    @SneakyThrows
    private void sleep(int seconds) {
        if (debug) {
            TimeUnit.SECONDS.sleep(seconds);
        }
    }
}
