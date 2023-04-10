package com.jmp.advanced.multithreading.task7;

import java.util.concurrent.RecursiveAction;

public class ForkBlur extends RecursiveAction {

    private final int[] mSource;

    private final int mStart;

    private final int mLength;

    private final int[] mDestination;

    public ForkBlur(final int[] src, final int start, final int length, final int[] dst) {
        mSource = src;
        mStart = start;
        mLength = length;
        mDestination = dst;
    }

    // Average pixels from source, write results into destination.
    protected void computeDirectly() {
        // Processing window size, should be odd.
        final var mBlurWidth = 15;
        final var sidePixels = (mBlurWidth - 1) / 2;
        for (var index = mStart; index < mStart + mLength; index++) {
            // Calculate average.
            float rt = 0, gt = 0, bt = 0;
            for (var mi = -sidePixels; mi <= sidePixels; mi++) {
                final var mindex = Math.min(Math.max(mi + index, 0), mSource.length - 1);
                final var pixel = mSource[mindex];
                rt += (float) ((pixel & 0x00ff0000) >> 16) / mBlurWidth;
                gt += (float) ((pixel & 0x0000ff00) >> 8) / mBlurWidth;
                bt += (float) ((pixel & 0x000000ff)) / mBlurWidth;
            }

            // Re-assemble destination pixel.
            final var dpixel = (0xff000000)
                    | (((int) rt) << 16)
                    | (((int) gt) << 8)
                    | (((int) bt));
            mDestination[index] = dpixel;
        }
    }

    protected static int sThreshold = 10000;

    @Override
    protected void compute() {
        if (mLength < sThreshold) {
            computeDirectly();
            return;
        }

        final var split = mLength / 2;

        invokeAll(new ForkBlur(mSource, mStart, split, mDestination),
                new ForkBlur(mSource, mStart + split, mLength - split,
                        mDestination));
    }

}
