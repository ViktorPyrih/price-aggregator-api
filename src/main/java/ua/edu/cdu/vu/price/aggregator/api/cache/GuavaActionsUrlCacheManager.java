package ua.edu.cdu.vu.price.aggregator.api.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.edu.cdu.vu.price.aggregator.api.domain.DslExpression;

import java.util.List;

@Slf4j
@Component
public class GuavaActionsUrlCacheManager implements ActionsUrlCacheManager {

    private final Cache<Cacheable<List<DslExpression<Void>>>, String> cache = CacheBuilder.newBuilder()
            .softValues()
            .build();

    @Override
    public String getUrlByActions(List<DslExpression<Void>> actions) {
        var key = new Cacheable<>(actions);
        return cache.getIfPresent(key);
    }

    @Override
    public void put(List<DslExpression<Void>> actions, String url) {
        log.debug("URL: {} will be cached for actions: {}", url, actions);
        var key = new Cacheable<>(actions);
        cache.put(key, url);
    }
}
