package com.jmp.kafka.courier.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusDto {

    ACCEPTED, COOKING, READY, ON_DELIVERY, DELIVERED;

    @JsonValue
    public String toJson() {
        return name().toLowerCase(Locale.ROOT);
    }

}
