package com.jmp.concurrency.task2;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lombok.SneakyThrows;

public record NumberWriter(List<Integer> numbers, int limit) implements Runnable {

    @SneakyThrows
    @Override
    public void run() {
        final var random = ThreadLocalRandom.current();
        while (numbers.size() < limit) {
            synchronized (numbers) {
                numbers.add(random.nextInt(20));
            }
            Thread.sleep(10);
        }
    }

}
