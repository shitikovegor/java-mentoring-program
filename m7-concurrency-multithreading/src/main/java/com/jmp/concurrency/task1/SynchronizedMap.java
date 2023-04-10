package com.jmp.concurrency.task1;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class SynchronizedMap implements ThreadMap {

    private final Map<Integer, Integer> map = new HashMap<>();

    @Override
    public synchronized void put(final Integer key, final Integer value) {
        map.put(key, value);
    }

    @Override
    public synchronized long sumOfValues() {
        return map.values().stream()
                .mapToLong(Integer::intValue)
                .sum();
    }

    @Override
    public synchronized int size() {
        return map.size();
    }

}
