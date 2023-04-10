package com.jmp.effective.java.service;

import com.jmp.effective.java.model.CacheClient;
import com.jmp.effective.java.model.Entry;

public class CacheService {

    private final CacheClient cache;

    public CacheService(final CacheClient cache) {
        this.cache = cache;
    }

    public Entry get(final String data) {
        return cache.get(data);
    }

    public void put(final String data) {
        cache.put(data);
    }

    public double getAverageAddingTime() {
        return cache.getAverageAddingTime();
    }

    public long getTotalAddingAmount() {
        return cache.getTotalAddingAmount();
    }

    public long getCacheEvictionsAmount() {
        return cache.getCacheEvictionsAmount();
    }

}
