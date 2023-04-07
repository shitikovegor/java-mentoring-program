package com.jmp.effective.java.model;

import java.time.Instant;

import lombok.Getter;

@Getter
public class CachePerfomanceData {

    private long cacheEvictionsAmount;

    private long totalAddingTime;

    private long totalAddingAmount;

    public void increaseCacheEvictionsAmount() {
        cacheEvictionsAmount++;
    }

    public void increaseTotalAddingAmount() {
        totalAddingAmount++;
    }

    public void updateTotalAddingTime(final Instant startTime, final Instant endTime) {
        final var addingTime = endTime.toEpochMilli() - startTime.toEpochMilli();
        totalAddingTime += addingTime;
    }

    public double getAverageAddingTime() {
        return (totalAddingTime * 1.0) / totalAddingAmount;
    }

}
