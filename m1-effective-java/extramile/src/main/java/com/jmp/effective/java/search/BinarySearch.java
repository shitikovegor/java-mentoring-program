package com.jmp.effective.java.search;

public class BinarySearch {

    public int recursiveSearch(final int[] items, final int itemToSearch) {
        return recursiveSearch(items, 0, items.length - 1, itemToSearch);
    }

    public int iterativeSearch(final int[] items, final int itemToSearch) {
        var start = 0;
        var finish = items.length - 1;
        while (start <= finish) {
            final var middle = start + (finish - start) / 2;
            if (items[middle] == itemToSearch)
                return middle;
            if (items[middle] < itemToSearch)
                start = middle + 1;
            else
                finish = middle - 1;
        }
        return -1;
    }

    private int recursiveSearch(final int[] items, final int start, final int finish, final int itemToSearch) {
        if (finish >= start) {
            final var middle = start + (finish - start) / 2;
            if (items[middle] == itemToSearch)
                return middle;
            if (items[middle] > itemToSearch)
                return recursiveSearch(items, start, middle - 1, itemToSearch);
            return recursiveSearch(items, middle + 1, finish, itemToSearch);
        }
        return -1;
    }

}
