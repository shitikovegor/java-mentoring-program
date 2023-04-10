package com.jmp.advanced.multithreading.task5.impl;

import java.util.List;
import java.util.concurrent.Semaphore;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.jmp.advanced.multithreading.task5.Datasource;

@Slf4j
@RequiredArgsConstructor
public class SemaphoreDatasource implements Datasource {

    private final List<String> datasource;

    private final Semaphore semaphore;

    private int addingCounter = 0;

    private int removingCounter = 0;

    @SneakyThrows
    @Override
    public void add(final String data) {
        semaphore.acquire();
        datasource.add(data);
        addingCounter++;
        log.info("Data {} is added to datasource", data);
    }

    @Override
    public void remove(final String data) {
        if (datasource.contains(data)) {
            datasource.remove(data);
            semaphore.release();
            log.info("Data {} is removed from datasource", data);
            removingCounter++;
        } else {
            log.info("QueueDatasource doesn't contain data {}", data);
        }
    }

    @Override
    public int getAvailableSlots() {
        return semaphore.availablePermits();
    }

    @Override
    public int getAddingCount() {
        return addingCounter;
    }

    @Override
    public int getRemovingCount() {
        return removingCounter;
    }

}
