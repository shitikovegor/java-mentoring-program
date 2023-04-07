package com.jmp.effective.java.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BinaryTree {

    private Node root;

    public void add(final int data) {
        root = addRecursive(root, data);
    }

    public Node find(final int data) {
        return findRecursive(root, data);
    }

    private Node addRecursive(final Node current, final int data) {
        if (current == null) {
            return new Node(data);
        }
        if (data < current.getData()) {
            current.setLeft(addRecursive(current.getLeft(), data));
        }
        if (data > current.getData()) {
            current.setRight(addRecursive(current.getRight(), data));
        }
        return current;
    }

    private Node findRecursive(final Node current, final int data) {
        if (current == null) {
            return null;
        }
        if (data == current.getData()) {
            return current;
        }
        return data < current.getData() ? findRecursive(current.getLeft(), data)
                : findRecursive(current.getRight(), data);
    }

}
