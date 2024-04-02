package com.apple.WeatherService.config;



import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static com.apple.WeatherService.constant.AppConstants.WEATHER_CACHE;

@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager cacheManager = new
                CaffeineCacheManager(WEATHER_CACHE);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    @Bean
    Caffeine caffeineSpec() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(100)
                .expireAfterAccess(30, TimeUnit.MINUTES);
    }
}
