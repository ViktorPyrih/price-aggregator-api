package ua.edu.cdu.vu.price.aggregator.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.api.cache.ActionsUrlCacheManager;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;
    private final List<ActionsUrlCacheManager> actionsUrlCacheManagers;

    public void clear() {
        log.info("About to clear all caches");
        cacheManager.getCacheNames().stream()
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(Cache::clear);
        actionsUrlCacheManagers.forEach(ActionsUrlCacheManager::clear);
        log.info("All caches have been cleared");
    }
}
