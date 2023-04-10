package com.jmp.concurrency.benchmark;

import lombok.Data;

import com.jmp.concurrency.task1.ThreadMap;

@Data
public class Consumer implements Runnable {

    private final ThreadMap map;

    private final int limit;

    private long sum = 0;

    public Consumer(final ThreadMap map, final int limit) {
        this.map = map;
        this.limit = limit;
    }

    @Override
    public void run() {
        while (map.size() <= limit) {
            sum = map.sumOfValues();
        }
    }

}
