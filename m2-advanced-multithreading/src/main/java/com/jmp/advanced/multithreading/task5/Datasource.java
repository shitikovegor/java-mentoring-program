package com.jmp.advanced.multithreading.task5;

public interface Datasource {

    void add(final String data);

    void remove(final String data);

    int getAvailableSlots();

    int getAddingCount();

    int getRemovingCount();

}
