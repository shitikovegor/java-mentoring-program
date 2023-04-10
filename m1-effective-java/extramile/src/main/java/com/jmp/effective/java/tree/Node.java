package com.jmp.effective.java.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Node {

    private final int data;

    @Setter
    private Node left;

    @Setter
    private Node right;

}
