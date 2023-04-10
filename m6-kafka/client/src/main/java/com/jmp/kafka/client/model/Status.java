package com.jmp.kafka.client.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    ACCEPTED, COOKING, READY, ON_DELIVERY, DELIVERED

}
