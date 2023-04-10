package com.jmp.advanced.multithreading.task2;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParallelMergeSort extends RecursiveTask<int[]> {

    private final int[] unsortedArray;

    @Override
    protected int[] compute() {
        final var size = unsortedArray.length;
        if (size == 1) {
            return unsortedArray;
        }
        final var middle = size / 2;
        final var firstPart = new ParallelMergeSort(Arrays.copyOfRange(unsortedArray, 0, middle));
        final var secondPart = new ParallelMergeSort(Arrays.copyOfRange(unsortedArray, middle, size));

        firstPart.fork();
        secondPart.fork();
        return merge(firstPart.join(), secondPart.join());
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
