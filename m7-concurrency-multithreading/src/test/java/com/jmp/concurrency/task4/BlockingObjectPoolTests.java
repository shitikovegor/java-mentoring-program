package com.jmp.concurrency.task4;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BlockingObjectPoolTests {

    @SneakyThrows
    @Test
    void testPool() {
        final var pool = new BlockingObjectPool(3);
        final var isRunning = new AtomicBoolean(Boolean.TRUE);
        final var producer = new PoolProducer(pool, isRunning);
        final var consumer = new PoolConsumer(pool, isRunning);
        new Thread(producer).start();
        new Thread(consumer).start();
        Thread.sleep(500);
        isRunning.set(Boolean.FALSE);
        Thread.sleep(1000);
        Assertions.assertThat(pool.size()).isLessThanOrEqualTo(3);
        Assertions.assertThatNoException().isThrownBy(pool::get);
    }

    record PoolProducer(BlockingObjectPool pool, AtomicBoolean isRunning) implements Runnable {

        @Override
        public void run() {
            while (isRunning.get()) {
                pool.put(new Object());
            }
        }

    }

    record PoolConsumer(BlockingObjectPool pool, AtomicBoolean isRunning) implements Runnable {

        @Override
        public void run() {
            while (isRunning.get()) {
                final var object = pool.get();
                System.out.println("Pool size - " + pool.size() + ", object - " + object.toString());
            }
        }

    }

}
