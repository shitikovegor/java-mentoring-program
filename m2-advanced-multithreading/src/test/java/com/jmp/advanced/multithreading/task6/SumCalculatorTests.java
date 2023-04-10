package com.jmp.advanced.multithreading.task6;

import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

class SumCalculatorTests {

    @Test
    void compute() {
        // given
        final var pool = new ForkJoinPool(10);
        final var array = createArray(20_000_000);
        final var calculator = new SumCalculator(array, 0, array.length, null);
        // when
        final var parallelStart = Instant.now();
        pool.invoke(calculator);
        final var parallelFinish = Instant.now();
        final var linearStart = Instant.now();
        final var result = calculateSumLinearly(array);
        final var linearFinish = Instant.now();
        // then
        System.out.printf("Time of parallel calculation - %s ms, of linear calculation - %s ms.",
                parallelFinish.toEpochMilli() - parallelStart.toEpochMilli(),
                linearFinish.toEpochMilli() - linearStart.toEpochMilli());
        Assertions.assertThat(calculator.getResult()).isCloseTo(result, Offset.offset(0.1));
    }

    private static double[] createArray(final int arraySize) {
        final var doubles = new double[arraySize];
        for (int i = 0; i < arraySize; i++) {
            final var randomDouble = new Random().nextDouble() * (10 - 1) + 1;
            doubles[i] = randomDouble;
        }
        return doubles;
    }

    private double calculateSumLinearly(double[] array) {
        return Arrays.stream(array)
                .reduce(0, (sum, number) -> sum + Math.pow(number, 2));
    }

}
