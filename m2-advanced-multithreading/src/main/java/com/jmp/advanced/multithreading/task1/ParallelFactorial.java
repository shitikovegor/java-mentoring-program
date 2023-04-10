package com.jmp.advanced.multithreading.task1;

import java.math.BigInteger;
import java.util.concurrent.RecursiveTask;

public class ParallelFactorial extends RecursiveTask<BigInteger> {

    private Integer start = 2;

    private final Integer finish;

    public ParallelFactorial(final Integer finish) {
        this.finish = finish;
    }

    public ParallelFactorial(final Integer start, final Integer finish) {
        this.start = start;
        this.finish = finish;
    }

    @Override
    protected BigInteger compute() {
        if (start > finish) {
            return BigInteger.ONE;
        }
        if (start.equals(finish)) {
            return BigInteger.valueOf(start);
        }
        if (finish - start == 1) {
            return BigInteger.valueOf(finish).multiply(BigInteger.valueOf(start));
        }
        final var middle = (start + finish) / 2;
        final var task1 = new ParallelFactorial(start, middle);
        final var task2 = new ParallelFactorial(middle + 1, finish);
        task1.fork();
        task2.fork();
        return task1.join().multiply(task2.join());
    }

}
