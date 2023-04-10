package com.jmp.concurrency.task3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Producer implements Runnable {

    private final MessageBus messageBus;

    private final List<String> messages;

    private final AtomicBoolean isRunning;

    @Override
    public void run() {
        final var random = ThreadLocalRandom.current();
        while (isRunning.get()) {
            messageBus.sendMessage(messages.get(random.nextInt(messages.size())));
        }
    }

}
