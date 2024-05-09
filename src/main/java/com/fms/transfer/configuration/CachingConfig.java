package com.fms.transfer.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
public class CachingConfig {

    public static final String EXCHANGE_RATES = "exchangeRates";

    private static final long EXCHANGE_RATES_TIMOEOUT = (long) 5 * 60 * 1000;

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(EXCHANGE_RATES);
    }

    @CacheEvict(allEntries = true, value = {EXCHANGE_RATES})
    @Scheduled(fixedDelay = EXCHANGE_RATES_TIMOEOUT, initialDelay = 500)
    public void flushRatesCache() {
        log.info("Flushing exchange rates cache...");
    }
}