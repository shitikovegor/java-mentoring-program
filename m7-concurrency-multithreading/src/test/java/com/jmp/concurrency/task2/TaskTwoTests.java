package com.jmp.concurrency.task2;

import java.util.ArrayList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TaskTwoTests {

    @ParameterizedTest
    @ValueSource(ints = { 100, 1000, 5000 })
    void testNumbers(final int limit) {
        final var numbers = new ArrayList<Integer>();
        final var numWriter = new Thread(new NumberWriter(numbers, limit));
        final var sumCalculator = new Thread(new SumCalculator(numbers, limit));
        final var sumOfSquareCalculator = new Thread(new SumOfSquaresCalculator(numbers, limit));

        numWriter.start();
        sumCalculator.start();
        sumOfSquareCalculator.start();
    }

}
