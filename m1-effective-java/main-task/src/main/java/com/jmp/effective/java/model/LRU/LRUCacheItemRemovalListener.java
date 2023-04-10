package com.jmp.effective.java.model.LRU;

import java.time.Instant;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jmp.effective.java.model.Entry;

@Slf4j
@RequiredArgsConstructor
public class LRUCacheItemRemovalListener implements RemovalListener<String, Entry> {

    @Override
    public void onRemoval(final RemovalNotification<String, Entry> notification) {
        log.info("Item {} is removed by cause {} at {}", notification.getValue(), notification.getCause(),
                Instant.now());
    }

}
