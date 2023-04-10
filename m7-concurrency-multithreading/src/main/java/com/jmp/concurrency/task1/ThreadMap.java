package com.jmp.concurrency.task1;

public interface ThreadMap {

    void put(final Integer key, final Integer value);

    long sumOfValues();

    int size();

}
