package com.jmp.concurrency.task4;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class BlockingObjectPool {

    private final BlockingQueue<Object> pool;

    public BlockingObjectPool(final int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Size must be more than 0");
        }
        this.pool = new LinkedBlockingDeque<>(size);
        fillPool(size);
    }

    public Object get() {
        try {
            return pool.take();
        } catch (final InterruptedException e) {
            throw new RuntimeException("Exception in time of getting element from pool", e);
        }
    }

    public void put(final Object object) {
        try {
            pool.put(object);
        } catch (final InterruptedException e) {
            throw new RuntimeException("Exception in time of putting element to pool", e);
        }
    }

    public int size() {
        return pool.size();
    }

    private void fillPool(final int size) {
        for (var i = 0; i < size; i++) {
            put(new Object());
        }
    }

}
