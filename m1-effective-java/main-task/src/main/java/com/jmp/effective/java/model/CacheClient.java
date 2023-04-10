package com.jmp.effective.java.model;

import java.util.List;

public interface CacheClient {

    void loadCache(List<String> cacheData);

    void put(String data);

    Entry get(String data);

    double getAverageAddingTime();

    long getTotalAddingAmount();

    long getCacheEvictionsAmount();

}
