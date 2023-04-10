package com.jmp.advanced.multithreading.task5.impl;

import java.util.concurrent.ArrayBlockingQueue;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.jmp.advanced.multithreading.task5.Datasource;

@Slf4j
@RequiredArgsConstructor
public class QueueDatasource implements Datasource {

    private final ArrayBlockingQueue<String> datasource;

    private int addingCounter = 0;

    private int removingCounter = 0;

    @SneakyThrows
    @Override
    public void add(final String data) {
        datasource.put(data);
        addingCounter++;
        log.info("Data {} is added to datasource", data);
    }

    @Override
    public void remove(final String data) {
        if (datasource.remove(data)) {
            log.info("Data {} is removed from datasource", data);
            removingCounter++;
        } else {
            log.info("QueueDatasource doesn't contain data {}", data);
        }
    }

    @Override
    public int getAvailableSlots() {
        return datasource.remainingCapacity();
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
