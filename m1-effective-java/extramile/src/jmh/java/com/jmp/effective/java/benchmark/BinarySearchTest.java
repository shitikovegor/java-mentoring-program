package com.jmp.effective.java.benchmark;

import java.util.Comparator;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.jmp.effective.java.search.BinarySearch;

@Fork(value = 1, warmups = 1)
@BenchmarkMode({ Mode.AverageTime, Mode.Throughput })
@Warmup(iterations = 1)
@Measurement(iterations = 5)
public class BinarySearchTest {

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({ "10", "100", "1000", "10000" })
        private static int arraySize;

        public int[] array;

        public int intToSearch;

        @Setup(Level.Trial)
        public void setUp() {
            array = new Random()
                    .ints()
                    .limit(arraySize)
                    .boxed()
                    .sorted(Comparator.comparing(Integer::intValue))
                    .mapToInt(Integer::intValue)
                    .toArray();
            final var searchIntIndex = new Random().nextInt(arraySize - 2) + 1;
            intToSearch = array[searchIntIndex];
        }

    }

    @Benchmark
    public void testRecursiveSearch(final BenchmarkState state) {
        final var search = new BinarySearch();
        search.recursiveSearch(state.array, state.intToSearch);
    }

    @Benchmark
    public void testIterativeSearch(final BenchmarkState state) {
        final var search = new BinarySearch();
        search.iterativeSearch(state.array, state.intToSearch);
    }

}
