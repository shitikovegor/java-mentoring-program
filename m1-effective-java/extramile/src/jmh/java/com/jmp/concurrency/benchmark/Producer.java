package com.jmp.concurrency.benchmark;

import java.util.Random;

import lombok.SneakyThrows;

import com.jmp.concurrency.task1.ThreadMap;

public class Producer implements Runnable {

    private final ThreadMap map;

    private final int limit;

    public Producer(final ThreadMap map, final int limit) {
        this.map = map;
        this.limit = limit;
    }

    @SneakyThrows
    @Override
    public void run() {
        var count = map.size();
        while (count <= limit) {
            map.put(count, new Random().nextInt(1000));
            count++;
        }
    }

}
