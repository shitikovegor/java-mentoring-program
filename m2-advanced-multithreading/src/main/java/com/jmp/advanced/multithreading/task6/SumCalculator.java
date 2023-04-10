package com.jmp.advanced.multithreading.task6;

import java.util.concurrent.RecursiveAction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SumCalculator extends RecursiveAction {

    private final double[] array;

    private final int lo, hi;

    private double result;

    private final SumCalculator next;

    protected void compute() {
        final var l = lo;
        var h = hi;
        SumCalculator right = null;
        while (h - l > 1 && getSurplusQueuedTaskCount() <= 3) {
            final var mid = (l + h) >>> 1;
            right = new SumCalculator(array, mid, h, right);
            right.fork();
            h = mid;
        }
        var sum = atLeaf(l, h);
        while (right != null) {
            if (right.tryUnfork()) // directly calculate if not stolen
                sum += right.atLeaf(right.lo, right.hi);
            else {
                right.join();
                sum += right.result;
            }
            right = right.next;
        }
        result = sum;
    }

    private double atLeaf(final int l, final int h) {
        double sum = 0;
        for (var i = l; i < h; ++i) // perform leftmost base step
            sum += array[i] * array[i];
        return sum;
    }

}
