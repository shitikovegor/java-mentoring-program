package com.jmp.effective.java.sort;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MergeSortTests {

    @Test
    void sort() {
        // given
        final var sort = new MergeSort();
        final var array = new int[] { 45, 23, 47, 12, 43, 10, 22, 5, 12 };
        final var expected = new int[] { 5, 10, 12, 12, 22, 23, 43, 45, 47 };
        // when
        final var result = sort.sort(array);
        // then
        assertThat(result).isEqualTo(expected);
    }

}
