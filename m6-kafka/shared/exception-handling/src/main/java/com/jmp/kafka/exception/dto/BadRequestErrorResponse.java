package com.jmp.kafka.exception.dto;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BadRequestErrorResponse {

    private Instant timestamp;

    private String error;

    private String code;

    private String message;

    private String path;

    private List<ValidationDetail> details;

    @Data
    @Builder
    public static class ValidationDetail {

        private String field;

        private String message;

    }

}
