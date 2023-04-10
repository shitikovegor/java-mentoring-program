package com.jmp.elastic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EventException extends RuntimeException {

    private final String errorCode;

    private final HttpStatus httpStatus;

    public EventException(final String message, final String errorCode, final HttpStatus httpStatus,
            final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public EventException(final String message, final String errorCode, final HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}
