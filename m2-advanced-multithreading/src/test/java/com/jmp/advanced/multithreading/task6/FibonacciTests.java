package com.jmp.advanced.multithreading.task6;

import java.util.concurrent.ForkJoinPool;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FibonacciTests {

    @Test
    void compute() {
        // given
        final var pool = new ForkJoinPool(10);
        final var fibonacci = new Fibonacci(45);
        // when
        final var result = pool.invoke(fibonacci);
        // then
        Assertions.assertThat(result).isEqualTo(1134903170L);
    }

}
