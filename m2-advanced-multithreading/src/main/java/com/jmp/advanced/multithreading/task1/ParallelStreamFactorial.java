package com.jmp.advanced.multithreading.task1;

import java.math.BigInteger;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParallelStreamFactorial {

    private final Integer finish;

    public BigInteger calculateFactorialByStream() {
        return IntStream.rangeClosed(1, finish).parallel()
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);
    }

}
