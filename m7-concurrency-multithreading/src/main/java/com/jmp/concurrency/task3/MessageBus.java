package com.jmp.concurrency.task3;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MessageBus {

    private final Queue<String> messages = new LinkedList<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public void sendMessage(final String message) {
        if (lock.writeLock().tryLock()) {
            try {
                messages.add(message);
            } catch (final Exception e) {
                log.error("Exception in time of sending message: {}", e.getMessage(), e);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    public Optional<String> getMessage() {
        Optional<String> message = Optional.empty();
        if (lock.readLock().tryLock()) {
            try {
                message = Optional.ofNullable(messages.poll());
            } catch (final Exception e) {
                log.error("Exception in time of getting message: {}", e.getMessage(), e);
            } finally {
                lock.readLock().unlock();
            }
        }
        return message;
    }

}
