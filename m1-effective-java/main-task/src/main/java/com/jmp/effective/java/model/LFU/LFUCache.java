package com.jmp.effective.java.model.LFU;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.jmp.effective.java.model.CacheClient;
import com.jmp.effective.java.model.CachePerfomanceData;
import com.jmp.effective.java.model.Entry;
import com.jmp.effective.java.properties.CacheProperties;

@Slf4j
public class LFUCache implements CacheClient {

    private final CacheProperties properties;

    private final Map<String, CacheItem> items;

    private final SortedSet<CacheItem> sortedItems;

    private final CachePerfomanceData cachePerfomanceData;

    @Getter
    private final LFUCacheItemRemovalListener listener;

    public LFUCache(final CacheProperties properties) {
        this.properties = properties;
        this.items = new ConcurrentHashMap<>();
        this.sortedItems = Collections.synchronizedSortedSet(
                new TreeSet<>(Comparator.comparingInt(CacheItem::getHitCount)
                        .thenComparing(CacheItem::getExpirationTime)
                        .thenComparing(item -> item.getEntry().getData())));
        this.cachePerfomanceData = new CachePerfomanceData();
        this.listener = new LFUCacheItemRemovalListener(items, sortedItems, cachePerfomanceData);
    }

    @Override
    public void loadCache(final List<String> cacheData) {
        final var cacheItems = cacheData.stream()
                .map(data -> new CacheItem(new Entry(data), properties.getExpirationPeriodInMillis()))
                .collect(Collectors.toMap(item -> item.getEntry().getData(), Function.identity()));
        items.putAll(cacheItems);
        sortedItems.addAll(cacheItems.values());
    }

    @Override
    public void put(final String data) {
        final var startTime = Instant.now();
        while (items.size() >= properties.getCapacity()) {
            removeOldestItem();
        }
        final var entry = new Entry(data);
        final var cacheItem = new CacheItem(entry, properties.getExpirationPeriodInMillis());
        items.put(data, cacheItem);
        log.info("Item {} is added to cache", cacheItem);
        updateSortedItems(cacheItem);
        final var endTime = Instant.now();
        cachePerfomanceData.updateTotalAddingTime(startTime, endTime);
        cachePerfomanceData.increaseTotalAddingAmount();
    }

    @Override
    public Entry get(final String data) {
        if (!items.containsKey(data)) {
            return null;
        }
        final var cacheItem = items.get(data);
        cacheItem.increaseHitCount();
        cacheItem.updateExpirationTime(properties.getExpirationPeriodInMillis());
        updateSortedItems(cacheItem);
        return cacheItem.getEntry();
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
        return cachePerfomanceData.getCacheEvictionsAmount();
    }

    private void removeOldestItem() {
        final var item = sortedItems.first();
        sortedItems.remove(item);
        items.remove(item.getEntry().getData());
        cachePerfomanceData.increaseCacheEvictionsAmount();
        log.info("Item {} is removed by cache capacity", item);
    }

    private void updateSortedItems(final CacheItem cacheItem) {
        sortedItems.removeIf(item -> item.getEntry().equals(cacheItem.getEntry()));
        sortedItems.add(cacheItem);
    }

}
