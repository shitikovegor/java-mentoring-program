package com.jmp.effective.java.model.LFU;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import com.jmp.effective.java.model.Entry;

@Getter
@EqualsAndHashCode
@ToString
public class CacheItem {

    @NonNull
    private final Entry entry;

    private int hitCount = 0;

    @Setter
    private Instant expirationTime;

    public CacheItem(final Entry entry, final long expirationPeriodInMillis) {
        this.entry = entry;
        this.expirationTime = Instant.now().plusMillis(expirationPeriodInMillis);
    }

    public void increaseHitCount() {
        this.hitCount++;
    }

    public void updateExpirationTime(final long expirationPeriodInMillis) {
        this.expirationTime = Instant.now().plusMillis(expirationPeriodInMillis);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expirationTime);
    }

}
