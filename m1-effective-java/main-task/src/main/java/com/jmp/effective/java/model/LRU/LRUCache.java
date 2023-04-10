package com.jmp.effective.java.model.LRU;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import com.jmp.effective.java.model.CacheClient;
import com.jmp.effective.java.model.CachePerfomanceData;
import com.jmp.effective.java.model.Entry;
import com.jmp.effective.java.properties.CacheProperties;

@Slf4j
public class LRUCache implements CacheClient {

    private final CachePerfomanceData cachePerfomanceData;

    private final Cache<String, Entry> cache;

    public LRUCache(final CacheProperties properties) {
        this.cachePerfomanceData = new CachePerfomanceData();
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(properties.getExpirationPeriodInMillis(), TimeUnit.MILLISECONDS)
                .concurrencyLevel(5000)
                .maximumSize(properties.getCapacity())
                .removalListener(new LRUCacheItemRemovalListener())
                .recordStats()
                .build();
    }

    @Override
    public void loadCache(final List<String> cacheData) {
        final var cacheItems = cacheData.stream()
                .map(Entry::new)
                .collect(Collectors.toMap(Entry::getData, Function.identity()));
        cache.putAll(cacheItems);
    }

    @Override
    public void put(final String data) {
        final var startTime = Instant.now();
        final var entry = new Entry(data);
        cache.put(data, entry);
        log.info("Item {} is added to cache", entry);
        final var endTime = Instant.now();
        cachePerfomanceData.updateTotalAddingTime(startTime, endTime);
        cachePerfomanceData.increaseTotalAddingAmount();
    }

    @Override
    public Entry get(final String data) {
        return cache.getIfPresent(data);
    }

    @Override
    public double getAverageAddingTime() {
        return cachePerfomanceData.getAverageAddingTime();
    }

    @Override
    public long getTotalAddingAmount() {
        return cachePerfomanceData.getTotalAddingAmount();
    }

    @Override
    public long getCacheEvictionsAmount() {
        return cache.stats().evictionCount();
    }

}
