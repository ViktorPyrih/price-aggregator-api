package ua.edu.cdu.vu.price.aggregator.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
@ConditionalOnProperty(name = "spring.caching.enabled", havingValue = "true", matchIfMissing = true)
public class CachingConfig {
}
