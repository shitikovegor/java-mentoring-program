package com.jmp.advanced.multithreading.task5;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import com.jmp.advanced.multithreading.task5.impl.QueueDatasource;
import com.jmp.advanced.multithreading.task5.impl.SemaphoreDatasource;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class DatasourceTests {

    @RequiredArgsConstructor
    static class Producer implements Runnable {

        private final Integer id;

        private final String data;

        private final Datasource datasource;

        @Override
        public void run() {
            log.info("Producer with id {} try to add data {} to datasource", id, data);
            datasource.add(data);
            log.info("Available slots after adding by {} - {}", id, datasource.getAvailableSlots());
        }

    }

    @RequiredArgsConstructor
    static class Consumer implements Runnable {

        private final Integer id;

        private final String data;

        private final Datasource datasource;

        @Override
        public void run() {
            log.info("Consumer with id {} try to remove data {} from datasource", id, data);
            datasource.remove(data);
            log.info("Available slots after removing by {} - {}", id, datasource.getAvailableSlots());
        }

    }

    @SneakyThrows
    @Test
    void testSemaphoreDataSource() {
        // given
        final var semaphore = new Semaphore(5, true);
        final var datasource = new SemaphoreDatasource(new ArrayList<>(), semaphore);
        final var producers = new ArrayList<Producer>();
        final var consumers = new ArrayList<Consumer>();
        createProducersAndConsumers(datasource, producers, consumers);
        // when
        startProducersAndConsumers(producers, consumers);
        // then
        assertThat(datasource.getAvailableSlots()).isEqualTo(5);
        assertThat(datasource.getAddingCount()).isEqualTo(8);
        assertThat(datasource.getRemovingCount()).isEqualTo(8);
    }

    @SneakyThrows
    @Test
    void testQueueDataSource() {
        // given
        final var datasource = new QueueDatasource(new ArrayBlockingQueue<>(5));
        final var producers = new ArrayList<Producer>();
        final var consumers = new ArrayList<Consumer>();
        createProducersAndConsumers(datasource, producers, consumers);
        // when
        startProducersAndConsumers(producers, consumers);
        // then
        assertThat(datasource.getAvailableSlots()).isEqualTo(5);
        assertThat(datasource.getAddingCount()).isEqualTo(8);
        assertThat(datasource.getRemovingCount()).isEqualTo(8);
    }

    private static void startProducersAndConsumers(final ArrayList<Producer> producers,
            final ArrayList<Consumer> consumers)
            throws InterruptedException {
        for (final var producer : producers) {
            new Thread(producer).start();
            Thread.sleep(100);
        }
        for (final var consumer : consumers) {
            new Thread(consumer).start();
            Thread.sleep(100);
        }
    }

    private static void createProducersAndConsumers(final Datasource datasource, final ArrayList<Producer> producers,
            final ArrayList<Consumer> consumers) {
        for (var i = 1; i <= 8; i++) {
            final var data = UUID.randomUUID().toString();
            final var producer = new Producer(i, data, datasource);
            producers.add(producer);
            final var consumer = new Consumer(i, data, datasource);
            consumers.add(consumer);
        }
    }

}
