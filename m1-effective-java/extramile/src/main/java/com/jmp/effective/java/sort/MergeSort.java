package com.jmp.effective.java.sort;

import java.util.Arrays;

public class MergeSort implements Sort {

    @Override
    public int[] sort(final int[] unsortedArray) {
        final var size = unsortedArray.length;
        if (size == 1) {
            return unsortedArray;
        }
        final var middle = size / 2;
        var firstPart = Arrays.copyOfRange(unsortedArray, 0, middle);
        var secondPart = Arrays.copyOfRange(unsortedArray, middle, size);

        firstPart = sort(firstPart);
        secondPart = sort(secondPart);
        return merge(firstPart, secondPart);
    }

    private int[] merge(final int[] firstArray, final int[] secondArray) {
        final var firstSize = firstArray.length;
        final var secondSize = secondArray.length;
        final var sortedArray = new int[firstSize + secondSize];

        var firstArrIndex = 0;
        var secondArrIndex = 0;
        var sortedArrIndex = 0;
        while (firstArrIndex < firstSize && secondArrIndex < secondSize) {
            if (firstArray[firstArrIndex] < secondArray[secondArrIndex]) {
                sortedArray[sortedArrIndex] = firstArray[firstArrIndex];
                firstArrIndex++;
            } else {
                sortedArray[sortedArrIndex] = secondArray[secondArrIndex];
                secondArrIndex++;
            }
            sortedArrIndex++;
        }
        while (firstArrIndex < firstSize) {
            sortedArray[sortedArrIndex] = firstArray[firstArrIndex];
            firstArrIndex++;
            sortedArrIndex++;
        }
        while (secondArrIndex < secondSize) {
            sortedArray[sortedArrIndex] = secondArray[secondArrIndex];
            secondArrIndex++;
            sortedArrIndex++;
        }
        return sortedArray;
    }

}
