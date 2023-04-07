package com.jmp.effective.java.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.jmp.effective.java.model.Entry;
import com.jmp.effective.java.model.LFU.LFUCache;
import com.jmp.effective.java.properties.CacheProperties;

import static org.assertj.core.api.Assertions.assertThat;

class LFUCacheServiceTests {

    @Test
    void putFourElements_RemoveOldestItem1() throws Exception {
        // given
        final var expirationTime = TimeUnit.SECONDS.toMillis(20);
        final var properties = new CacheProperties(3, expirationTime);
        final var cache = new LFUCache(properties);
        final var service = new CacheService(cache);
        final var item1 = "item1";
        final var item2 = "item2";
        final var item3 = "item3";
        final var item4 = "item4";
        final var testData = List.of(item1, item2, item3, item4);
        // when
        for (final var item : testData) {
            service.put(item);
            Thread.sleep(10);
        }
        // then
        assertThat(service.getTotalAddingAmount()).isEqualTo(4);
        assertThat(service.get(item1)).isNull();
        assertThat(service.get(item3)).isNotNull();
        System.out.printf("Average adding time - %s millis", service.getAverageAddingTime());
    }

    @Test
    void putTwoElements_RemoveAllByExpirationTime() throws Exception {
        // given
        final var expirationTimeInMillis = TimeUnit.SECONDS.toMillis(1);
        final var properties = new CacheProperties(3, expirationTimeInMillis);
        final var cache = new LFUCache(properties);
        final var service = new CacheService(cache);
        final var item1 = "item1";
        final var item2 = "item2";
        final var testData = List.of(item1, item2);
        // when
        for (final var item : testData) {
            service.put(item);
        }
        Thread.sleep(expirationTimeInMillis * 2);
        // then
        assertThat(service.getTotalAddingAmount()).isEqualTo(2);
        assertThat(service.getCacheEvictionsAmount()).isEqualTo(2);
        assertThat(service.get(item1)).isNull();
        assertThat(service.get(item2)).isNull();
        System.out.printf("Average adding time - %s millis", service.getAverageAddingTime());
    }

    @Test
    void getTwoElements_CacheContainsThree_RemoveLeastFrequencyUsedItem2() {
        // given
        final var expirationTime = TimeUnit.SECONDS.toMillis(5);
        final var properties = new CacheProperties(3, expirationTime);
        final var cache = new LFUCache(properties);
        final var service = new CacheService(cache);
        final var item1 = "item1";
        final var item2 = "item2";
        final var item3 = "item3";
        final var item4 = "item4";
        final var testData = List.of(item1, item2, item3);
        cache.loadCache(testData);
        // when
        final var result1 = service.get(item1);
        final var result2 = service.get(item3);
        service.put(item4);
        final var result3 = service.get(item4);
        // then
        assertThat(service.getTotalAddingAmount()).isEqualTo(1);
        assertThat(service.getCacheEvictionsAmount()).isEqualTo(1);
        assertThat(service.get(item2)).isNull();
        assertThat(result1).isEqualTo(new Entry(item1));
        assertThat(result2).isEqualTo(new Entry(item3));
        assertThat(result3).isEqualTo(new Entry(item4));
        System.out.printf("Average adding time - %s millis", service.getAverageAddingTime());
    }

    @Test
    void getFromEmptyCache_ReturnNull() {
        // given
        final var expirationTime = TimeUnit.SECONDS.toMillis(5);
        final var properties = new CacheProperties(3, expirationTime);
        final var cache = new LFUCache(properties);
        final var service = new CacheService(cache);
        // when
        final var result = service.get("any");
        // then
        assertThat(result).isNull();
        assertThat(service.getTotalAddingAmount()).isEqualTo(0);
        assertThat(service.getCacheEvictionsAmount()).isEqualTo(0);
    }

    @Test
    void putOneHundredThousandElements_RemoveAllElementsByExpirationTime() {
        // given
        final var expirationTimeInMillis = 30;
        final var properties = new CacheProperties(100000, expirationTimeInMillis);
        final var cache = new LFUCache(properties);
        final var service = new CacheService(cache);
        for (var i = 0; i < 100000; i++) {
            service.put(String.valueOf(i));
        }
        // then
        assertThat(service.getTotalAddingAmount()).isEqualTo(100000);
        assertThat(service.get("0")).isNull();
        System.out.printf("Average adding time - %s millis", service.getAverageAddingTime());
    }

}
