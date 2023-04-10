package com.jmp.concurrency.task1;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DasExperiment {

    public static void main(final String[] args) {
        final Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(new HashMap<>());
        final Map<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();
        final Map<Integer, Integer> map = new HashMap<>();
        new Thread(new Producer(synchronizedMap)).start();
        new Thread(new Consumer(synchronizedMap)).start();
        new Thread(new Producer(concurrentMap)).start();
        new Thread(new Consumer(concurrentMap)).start();
        new Thread(new Producer(map)).start();
        new Thread(new Consumer(map)).start();
    }

    @Data
    public static class Producer implements Runnable {

        private final Map<Integer, Integer> map;

        @SneakyThrows
        @Override
        public void run() {
            var totalTime = 0.0;
            var count = 0;
            while (count < 1000) {
                final var start = System.currentTimeMillis();
                map.put(count, new Random().nextInt(10));
                final var finish = System.currentTimeMillis();
                totalTime += finish - start;
                count++;
                Thread.sleep(3);
            }
            log.info("map class - {}, average adding time - {}", map.getClass(), totalTime / count);
        }

    }

    @Data
    public static class Consumer implements Runnable {

        private final Map<Integer, Integer> map;

        private long sum = 0;

        private boolean failed;

        @Override
        public void run() {
            var totalTime = 0.0;
            var count = 0;
            while (map.size() < 1000 && !failed) {
                try {
                    final var start = System.currentTimeMillis();
                    sum = map.values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
                    final var finish = System.currentTimeMillis();
                    totalTime += finish - start;
                    count++;
                } catch (final ConcurrentModificationException e) {
                    failed = true;
                    log.error("Error in map class - {}, message - {}", map.getClass(), e.getMessage(), e);
                    log.error("map class - {}, map size - {}, sum - {}, count - {}", map.getClass(), map.size(), sum,
                            count);
                }
            }
            log.info("map class - {}, size - {},  sum - {}, average sum time - {}", map.getClass(), map.size(), sum,
                    totalTime / count);
        }

    }

}
