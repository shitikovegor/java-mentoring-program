package com.jmp.advanced.multithreading.task6;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Fibonacci extends RecursiveTask<Long> {

    private final long n;

    public Fibonacci(final long n) {
        this.n = n;
    }

    @Override
    protected Long compute() {
        if (n <= 10) {
            return calculateLinearly(n);
        }
        final var f1 = new Fibonacci(n - 1);
        final var f2 = new Fibonacci(n - 2);
        ForkJoinTask.invokeAll(f1, f2);
        return f2.join() + f1.join();

    }

    private Long calculateLinearly(final long n) {
        var n1 = 0L;
        var n2 = 1L;
        var result = 0L;
        for (var i = 1; i < n; i++) {
            result = n1 + n2;
            n1 = n2;
            n2 = result;
        }
        return result;
    }

}
