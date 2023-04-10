package com.jmp.advanced.multithreading.task3.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class DirectoryStatistics {

    private int fileCounter;

    private int directoryCounter;

    private long totalFileSize;

    public void addFileCount(final int fileAmount) {
        fileCounter += fileAmount;
    }

    public void addDirectoryCount(final int directoryAmount) {
        directoryCounter += directoryAmount;
    }

    public void addTotalFileSize(final long fileSize) {
        totalFileSize += fileSize;
    }

    public void increaseDirectoryCounter() {
        directoryCounter++;
    }

}
