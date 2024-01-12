package ua.edu.cdu.vu.price.aggregator.api.domain.command;

import com.codeborne.selenide.SelenideElement;
import ua.edu.cdu.vu.price.aggregator.api.exception.DslExecutionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.driver;
import static java.util.Objects.isNull;

public abstract class CacheableDslCommand extends DslCommand<SelenideElement, String> {

    private static final Map<CacheEntry, String> CACHE = new ConcurrentHashMap<>();

    private record CacheEntry(String url, String element) {
    }

    @Override
    public String execute(String url, SelenideElement input, Map<String, String> context) {
        if (isNull(input)) {
            throw new DslExecutionException("CLICK command executed on null input");
        }

        CacheEntry cacheEntry = new CacheEntry(url, input.toString());
        String urlFromCache = CACHE.get(cacheEntry);

        if (isNull(urlFromCache)) {
            action(input);
            if (!url.equals(driver().url())) {
                CACHE.put(cacheEntry, driver().url());
            }
        } else {
            open(urlFromCache);
            resizeWindow();
        }

        return null;
    }

    protected abstract void action(SelenideElement element);
}
