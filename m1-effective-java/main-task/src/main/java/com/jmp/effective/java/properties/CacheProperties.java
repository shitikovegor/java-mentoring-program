package com.jmp.effective.java.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CacheProperties {

    private final long capacity;

    private final long expirationPeriodInMillis;

}
