package com.jmp.concurrency.task3;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Consumer implements Runnable {

    private final MessageBus messageBus;

    private final AtomicBoolean isRunning;

    @Override
    public void run() {
        while (isRunning.get()) {
            messageBus.getMessage()
                    .ifPresentOrElse(message -> log.info("Message - {}", message),
                            () -> log.info("Message queue is empty"));
        }
    }

}
