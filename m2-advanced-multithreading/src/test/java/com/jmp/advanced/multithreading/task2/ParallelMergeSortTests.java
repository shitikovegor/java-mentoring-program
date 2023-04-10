package com.jmp.advanced.multithreading.task2;

import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.jmp.effective.java.sort.MergeSort;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ParallelMergeSortTests {

    @Order(1)
    @ParameterizedTest(name = "{displayName} [{index}]")
    @MethodSource("arrayForCalculating")
    void sortSequentialArray(final int[] array, final int[] sortedArray) {
        // given
        final var sort = new MergeSort();
        // when
        final var start = Instant.now();
        final var result = sort.sort(array);
        final var finish = Instant.now();
        // then
        assertThat(result).isEqualTo(sortedArray);
        System.out.printf("Sequential sorted array with %s items, execution time - %s millis\n", result.length,
                finish.toEpochMilli() - start.toEpochMilli());
    }

    @Order(2)
    @ParameterizedTest(name = "{displayName} [{index}]")
    @MethodSource("arrayForCalculating")
    void sort(final int[] array, final int[] sortedArray) {
        // given
        final var pool = new ForkJoinPool(10);
        // when
        final var start = Instant.now();
        final var result = pool.invoke(new ParallelMergeSort(array));
        final var finish = Instant.now();
        // then
        assertThat(result).isEqualTo(sortedArray);
        System.out.printf("Parallel sorted array with %s items, execution time - %s millis\n", result.length,
                finish.toEpochMilli() - start.toEpochMilli());
    }

    private static Stream<Arguments> arrayForCalculating() {
        final var unsortedArray100 = createUnsortedArray(100);
        final var sortedArray100 = createSortedArray(unsortedArray100);
        final var unsortedArray1k = createUnsortedArray(1000);
        final var sortedArray1k = createSortedArray(unsortedArray1k);
        final var unsortedArray10k = createUnsortedArray(10000);
        final var sortedArray10k = createSortedArray(unsortedArray10k);
        final var unsortedArray100k = createUnsortedArray(100000);
        final var sortedArray100k = createSortedArray(unsortedArray100k);
        final var unsortedArray1m = createUnsortedArray(1000000);
        final var sortedArray1m = createSortedArray(unsortedArray1m);
        final var unsortedArray10m = createUnsortedArray(10000000);
        final var sortedArray10m = createSortedArray(unsortedArray10m);
        return Stream.of(Arguments.of(unsortedArray100, sortedArray100),
                Arguments.of(unsortedArray1k, sortedArray1k),
                Arguments.of(unsortedArray10k, sortedArray10k),
                Arguments.of(unsortedArray100k, sortedArray100k),
                Arguments.of(unsortedArray1m, sortedArray1m),
                Arguments.of(unsortedArray10m, sortedArray10m));
    }

    private static int[] createUnsortedArray(final int arraySize) {
        return new Random()
                .ints()
                .limit(arraySize)
                .toArray();
    }

    private static int[] createSortedArray(final int[] array) {
        return Arrays.stream(array)
                .sorted()
                .toArray();
    }

}
