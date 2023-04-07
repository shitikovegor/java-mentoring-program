package com.jmp.effective.java.model.LFU;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import com.jmp.effective.java.model.CachePerfomanceData;

@Slf4j
public class LFUCacheItemRemovalListener implements Runnable {

    private final Map<String, CacheItem> cacheItems;

    private final Set<CacheItem> sortedItems;

    private final CachePerfomanceData cachePerfomanceData;

    public LFUCacheItemRemovalListener(final Map<String, CacheItem> cacheItems, final Set<CacheItem> sortedItems,
            final CachePerfomanceData cachePerfomanceData) {
        this.cacheItems = cacheItems;
        this.sortedItems = sortedItems;
        this.cachePerfomanceData = cachePerfomanceData;
        final var executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        while (!cacheItems.values().isEmpty()) {
            cacheItems.values().parallelStream()
                    .filter(CacheItem::isExpired)
                    .forEach(item -> {
                        cacheItems.remove(item.getEntry().getData());
                        sortedItems.remove(item);
                        cachePerfomanceData.increaseCacheEvictionsAmount();
                        log.info("Item {} is removed by expiration time at {}", item, Instant.now());
                    });
        }
    }

}
