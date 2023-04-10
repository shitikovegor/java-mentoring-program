package com.jmp.concurrency.task1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NonSynchronizedMap implements ThreadMap {

    private final Map<Integer, Integer> map;

    private final ReentrantLock lock = new ReentrantLock();

    public NonSynchronizedMap() {
        this.map = new HashMap<>();
    }

    @Override
    public void put(final Integer key, final Integer value) {
        lock.lock();
        try {
            map.put(key, value);
        } catch (final Exception e) {
            throw new RuntimeException("Exception - " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long sumOfValues() {
        lock.lock();
        try {
            return map.values().stream()
                    .mapToLong(Integer::intValue)
                    .sum();
        } catch (final Exception e) {
            throw new RuntimeException("Exception - " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized int size() {
        lock.lock();
        try {
            return map.size();
        } catch (final Exception e) {
            throw new RuntimeException("Exception - " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

}
