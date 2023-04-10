package com.jmp.search.controller.advice;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {

    private Instant timestamp;

    private String error;

    private String code;

    private String message;

    private String path;

}
