package com.jmp.effective.java.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BinarySearchTests {

    private BinarySearch search;

    @BeforeEach
    void setUp() {
        search = new BinarySearch();
    }

    @Test
    void recursiveSearch() {
        // given
        final var array = new int[] { 2, 5, 8, 10, 15, 25, 33 };
        // when
        final var result = search.recursiveSearch(array, 10);
        // then
        assertThat(result).isEqualTo(3);
    }

    @Test
    void recursiveSearch_NotFound() {
        // given
        final var array = new int[] { 2, 5, 8, 10, 15, 25, 33 };
        // when
        final var result = search.recursiveSearch(array, 55);
        // then
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void iterativeSearch() {
        // given
        final var array = new int[] { 2, 5, 8, 10, 15, 25, 33 };
        // when
        final var result = search.iterativeSearch(array, 10);
        // then
        assertThat(result).isEqualTo(3);
    }

    @Test
    void iterativeSearch_NotFound() {
        // given
        final var array = new int[] { 2, 5, 8, 10, 15, 25, 33 };
        // when
        final var result = search.iterativeSearch(array, 55);
        // then
        assertThat(result).isEqualTo(-1);
    }

}
