package com.vitorsaucedo.wordle.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEvictScheduler {

    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void evictDailyWordCaches() {
        cacheManager.getCacheNames()
                .forEach(name -> {
                    var cache = cacheManager.getCache(name);
                    if (cache != null) cache.clear();
                });
        log.info("Todos os caches invalidados à meia-noite.");
    }
}