package com.jmp.elastic.dto.event;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import com.jmp.elastic.exception.ErrorCode;
import com.jmp.elastic.exception.EventException;

@Getter
@AllArgsConstructor
public enum EventTypeDto {

    WORKSHOP("workshop"), TECH_TALK("tech-talk");

    private final String name;

    @JsonValue
    public String toJson() {
        return getName();
    }

    public static EventTypeDto fromName(final String name) {
        return Arrays.stream(EventTypeDto.values())
                .filter(type -> type.getName().equals(name))
                .findFirst()
                .orElseThrow(
                        () -> new EventException("Invalid event type", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST));
    }

}
