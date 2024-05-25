package ua.edu.cdu.vu.price.aggregator.api.job;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RedisTest {

    private static final String ANY_CHARACTERS = "*";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @ParameterizedTest
    @ValueSource(strings = {"ekatalog"})
    void removeByKeyContaining(String value) {
        Optional.ofNullable(redisTemplate.keys(String.join(value, ANY_CHARACTERS, ANY_CHARACTERS)))
                .ifPresent(keys -> {
                    System.out.println("About to delete these keys:");
                    keys.forEach(System.out::println);
                    redisTemplate.delete(keys);
                });
    }
}
