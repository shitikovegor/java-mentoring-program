package com.jmp.advanced.multithreading.task3.model;

import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class ProgressBar extends Thread {

    @Setter
    private DirectoryStatistics statistics;

    private final ForkJoinPool pool;

    @SneakyThrows
    @Override
    public void run() {
        final var start = Instant.now();
        System.out.println("Loading...");
        while (statistics == null || !pool.isShutdown()) {
            for (var i = 0; i < 10; i++) {
                final var loadingSymbol = "=".repeat(i);
                final var emptySymbol = " ".repeat(10 - i);
                System.out.printf("|%s%s|\r", loadingSymbol, emptySymbol);
                Thread.sleep(10);
            }
        }
        final var finish = Instant.now();
        System.out.println("Loading done!");
        final var totalTime = finish.toEpochMilli() - start.toEpochMilli();
        System.out.printf("Total time - %ss, Total size - %s, Amount of directories - %s, Amount of files - %s.",
                TimeUnit.MILLISECONDS.toSeconds(totalTime), statistics.getTotalFileSize(),
                statistics.getDirectoryCounter(),
                statistics.getFileCounter());
    }

}
