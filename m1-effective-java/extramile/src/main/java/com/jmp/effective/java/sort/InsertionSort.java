package com.jmp.effective.java.sort;

public class InsertionSort implements Sort {

    @Override
    public int[] sort(final int[] arrayToSort) {
        final var size = arrayToSort.length;
        for (var i = 1; i < size; ++i) {
            final var key = arrayToSort[i];
            var j = i - 1;
            while (j >= 0 && arrayToSort[j] > key) {
                arrayToSort[j + 1] = arrayToSort[j];
                j = j - 1;
            }
            arrayToSort[j + 1] = key;
        }
        return arrayToSort;
    }

}
