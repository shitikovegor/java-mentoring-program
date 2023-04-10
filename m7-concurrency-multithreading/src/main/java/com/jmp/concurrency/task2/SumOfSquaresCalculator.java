package com.jmp.concurrency.task2;

import java.util.List;

public record SumOfSquaresCalculator(List<Integer> numbers, int limit) implements Runnable {

    @Override
    public void run() {
        while (numbers.size() < limit) {
            final long sumOfSquares;
            synchronized (numbers) {
                sumOfSquares = numbers.stream()
                        .mapToInt(Integer::intValue)
                        .map(number -> number * number)
                        .sum();
            }
            final var squareOfSum = sumOfSquares * sumOfSquares;
            System.out.println("Root square - " + squareOfSum);
        }
    }

}
