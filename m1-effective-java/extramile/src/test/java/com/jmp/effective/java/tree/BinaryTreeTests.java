package com.jmp.effective.java.tree;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BinaryTreeTests {

    @Test
    void find_DataFound() {
        // given
        final var five = new Node(5);
        final var one = new Node(1);
        final var two = new Node(2, one, null);
        final var fifteen = new Node(15);
        final var nine = new Node(9, null, fifteen);
        five.setLeft(two);
        five.setRight(nine);
        final var tree = new BinaryTree(five);
        // when
        final var result = tree.find(9);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getData()).isEqualTo(9);
        assertThat(result.getLeft()).isNull();
        assertThat(result.getRight().getData()).isEqualTo(15);
    }

    @Test
    void findInEmptyTree_Null() {
        // given
        final var tree = new BinaryTree();
        // when
        final var result = tree.find(10);
        // then
        assertThat(result).isNull();
    }

    @Test
    void addToExistingTree() {
        // given
        final var five = new Node(5);
        final var one = new Node(1);
        final var two = new Node(2, one, null);
        final var fifteen = new Node(15);
        final var nine = new Node(9, null, fifteen);
        five.setLeft(two);
        five.setRight(nine);
        final var tree = new BinaryTree(five);
        // when
        tree.add(8);
        tree.add(4);
        // then
        assertThat(nine.getLeft().getData()).isEqualTo(8);
        assertThat(two.getRight().getData()).isEqualTo(4);
    }

    @Test
    void addToNewTree() {
        // given
        final var tree = new BinaryTree();
        // when
        tree.add(5);
        tree.add(2);
        tree.add(6);
        // then
        assertThat(tree.getRoot().getData()).isEqualTo(5);
        assertThat(tree.getRoot().getLeft().getData()).isEqualTo(2);
        assertThat(tree.getRoot().getRight().getData()).isEqualTo(6);
    }

}
