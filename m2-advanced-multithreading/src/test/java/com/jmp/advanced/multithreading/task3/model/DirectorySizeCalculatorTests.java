package com.jmp.advanced.multithreading.task3.model;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DirectorySizeCalculatorTests {

    @Test
    void compute() {
        // given
        final var pool = new ForkJoinPool(10);
        final var file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("task3")).getFile());
        final var calculator = new DirectorySizeCalculator(file);
        final var expected = new DirectoryStatistics(6, 7, 444888);
        // when
        final var result = pool.invoke(calculator);
        // then
        Assertions.assertThat(result).isEqualTo(expected);
    }

}
