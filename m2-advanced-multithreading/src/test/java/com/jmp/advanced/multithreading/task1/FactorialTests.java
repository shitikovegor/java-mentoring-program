package com.jmp.advanced.multithreading.task1;

import java.math.BigInteger;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class FactorialTests {

    @Test
    void calculateParallel_CheckAlgorithm() {
        // given
        final var pool = new ForkJoinPool(10);
        // when
        final var result = pool.invoke(new ParallelFactorial(5));
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(120));
    }

    @Test
    void calculateSequentialByStream_CheckAlgorithm() {
        // given
        final var factorial = new SequentialFactorial(5);
        // when
        final var result = factorial.calculateFactorialByStream();
        // then
        assertThat(result).isEqualTo(BigInteger.valueOf(120));
    }

    @Test
    void calculateSequential_CheckAlgorithm() {
        // given
        final var factorial = new SequentialFactorial(5);
        // when
        final var result = factorial.calculateFactorial();
        assertThat(result).isEqualTo(BigInteger.valueOf(120));
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @MethodSource("numbersForCalculating")
    void calculateParallel(final int numberForCalculating) {
        // given
        final var pool = new ForkJoinPool(10);
        // when
        final var start = Instant.now();
        pool.invoke(new ParallelFactorial(numberForCalculating));
        final var finish = Instant.now();
        // then
        System.out.printf("Parallel factorial for %s - %s millis\n", numberForCalculating,
                finish.toEpochMilli() - start.toEpochMilli());
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @MethodSource("numbersForCalculating")
    void calculateSequential(final int numberForCalculating) {
        // given
        final var factorial = new SequentialFactorial(numberForCalculating);
        // when
        final var start = Instant.now();
        factorial.calculateFactorial();
        final var finish = Instant.now();
        // then
        System.out.printf("Sequential factorial for %s - %s millis\n", numberForCalculating,
                finish.toEpochMilli() - start.toEpochMilli());
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @MethodSource("numbersForCalculating")
    void calculateParallelByStream(final int numberForCalculating) {
        // given
        final var factorial = new ParallelStreamFactorial(numberForCalculating);
        // when
        final var start = Instant.now();
        factorial.calculateFactorialByStream();
        final var finish = Instant.now();
        // then
        System.out.printf("Parallel factorial by stream for %s - %s millis\n", numberForCalculating,
                finish.toEpochMilli() - start.toEpochMilli());
    }

    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    @MethodSource("numbersForCalculatingByStream")
    void calculateSequentialByStream(final int numberForCalculating) {
        // given
        final var factorial = new SequentialFactorial(numberForCalculating);
        // when
        final var start = Instant.now();
        factorial.calculateFactorialByStream();
        final var finish = Instant.now();
        // then
        System.out.printf("Sequential factorial by stream for %s - %s millis\n", numberForCalculating,
                finish.toEpochMilli() - start.toEpochMilli());
    }

    private static Stream<Integer> numbersForCalculating() {
        return Stream.of(10, 100, 1000, 10000, 100000, 1000000);
    }

    private static Stream<Integer> numbersForCalculatingByStream() {
        return Stream.of(10, 100, 1000, 10000, 100000);
    }

}
