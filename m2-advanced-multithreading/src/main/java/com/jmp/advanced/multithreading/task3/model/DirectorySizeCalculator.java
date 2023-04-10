package com.jmp.advanced.multithreading.task3.model;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DirectorySizeCalculator extends RecursiveTask<DirectoryStatistics> {

    private final DirectoryStatistics statistics;

    private final File file;

    public DirectorySizeCalculator(final File file) {
        this.file = file;
        this.statistics = new DirectoryStatistics();
    }

    @Override
    protected DirectoryStatistics compute() {
        if (file.isFile()) {
            log.debug("File - {}", file.getName());
            return new DirectoryStatistics(1, 0, file.length());
        }
        log.debug("Directory - {}", file.getName());
        statistics.increaseDirectoryCounter();
        final var calculators = new ArrayList<DirectorySizeCalculator>();
        final var files = file.listFiles();
        if (files != null) {
            for (final var file : files) {
                final var calculator = new DirectorySizeCalculator(file);
                calculator.fork();
                calculators.add(calculator);
            }
        }

        calculators.forEach(calculator -> {
            final var calcStatistics = calculator.join();
            statistics.addTotalFileSize(calcStatistics.getTotalFileSize());
            statistics.addFileCount(calcStatistics.getFileCounter());
            statistics.addDirectoryCount(calcStatistics.getDirectoryCounter());
        });
        return statistics;
    }

}
