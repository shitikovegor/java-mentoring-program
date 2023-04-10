package com.jmp.concurrency.task3;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class TaskThreeTests {

    @SneakyThrows
    @Test
    void testMessageBus() {
        final var messageBus = new MessageBus();
        final var isRunning = new AtomicBoolean(true);
        final var messages = List.of("Message 1", "Message 2", "Message 3", "Message 4", "Message 5", "Message 6");
        final var producer1 = new Producer(messageBus, messages, isRunning);
        final var producer2 = new Producer(messageBus, messages, isRunning);
        final var consumer1 = new Consumer(messageBus, isRunning);
        final var consumer2 = new Consumer(messageBus, isRunning);
        final var threads = List.of(producer1, producer2, consumer1, consumer2);
        final var pool = Executors.newFixedThreadPool(4);
        threads.forEach(pool::submit);
        Thread.sleep(500);
        isRunning.set(false);
        pool.shutdown();
    }

}
