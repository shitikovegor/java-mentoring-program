package com.jmp.concurrency.task2;

import java.util.List;

public record SumCalculator(List<Integer> numbers, int limit) implements Runnable {

    @Override
    public void run() {
        while (numbers.size() < limit) {
            final long sum;
            synchronized (numbers) {
                sum = numbers.stream()
                        .mapToInt(Integer::intValue)
                        .sum();
            }
            System.out.println("Sum of numbers - " + sum);
        }
    }

}
