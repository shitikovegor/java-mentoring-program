package com.jmp.concurrency.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.jmp.concurrency.task1.NonSynchronizedMap;
import com.jmp.concurrency.task1.SynchronizedMap;

@Fork(value = 1, warmups = 1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
public class ThreadSafeTest {

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        @Param({ "1000", "5000", "10000", "20000", "50000" })
        public int limit;

    }

    @Benchmark
    public void testNonSynchronizedMap(final BenchmarkState state) {
        final var map = new NonSynchronizedMap();
        new Thread(new Consumer(map, state.limit)).start();
        new Thread(new Producer(map, state.limit)).start();
    }

    @Benchmark
    public void testSynchronizedMap(final BenchmarkState state) {
        final var map = new SynchronizedMap();
        new Thread(new Consumer(map, state.limit)).start();
        new Thread(new Producer(map, state.limit)).start();
    }

}
