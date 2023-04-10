package com.jmp.advanced.multithreading.task1;

import java.math.BigInteger;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SequentialFactorial {

    private final Integer finish;

    public BigInteger calculateFactorialByStream() {
        return IntStream.rangeClosed(1, finish)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);
    }

    public BigInteger calculateFactorial() {
        if (finish < 0)
            return BigInteger.ZERO;
        if (finish == 0)
            return BigInteger.ONE;
        if (finish == 1 || finish == 2)
            return BigInteger.valueOf(finish);
        return calculateRecursive(2, finish);
    }

    private BigInteger calculateRecursive(final int start, final int finish) {
        if (start > finish) {
            return BigInteger.ONE;
        }
        if (start == finish) {
            return BigInteger.valueOf(start);
        }
        if (finish - start == 1) {
            return BigInteger.valueOf(finish).multiply(BigInteger.valueOf(start));
        }
        final var middle = (start + finish) / 2;
        return calculateRecursive(start, middle).multiply(calculateRecursive(middle + 1, finish));
    }

}
