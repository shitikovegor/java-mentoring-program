package com.jmp.advanced.multithreading.task3;

import java.io.File;
import java.util.concurrent.ForkJoinPool;

import com.jmp.advanced.multithreading.task3.model.AppKeyController;
import com.jmp.advanced.multithreading.task3.model.DirectorySizeCalculator;
import com.jmp.advanced.multithreading.task3.model.ProgressBar;

public class DirectoryCalculatorApp {

    public static void main(final String[] args) {
        final var pool = new ForkJoinPool(10);
        final var progressBar = new ProgressBar(pool);
        final var scanner = new AppKeyController();
        final var calculator = new DirectorySizeCalculator(new File("/Users/Yahor_Shytsikau"));
        progressBar.start();
        scanner.start();
        final var statistics = pool.invoke(calculator);
        progressBar.setStatistics(statistics);
    }

}
