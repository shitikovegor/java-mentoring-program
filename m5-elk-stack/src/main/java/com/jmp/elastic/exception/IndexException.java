package com.jmp.elastic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IndexException extends EventException {

    public IndexException(final String message, final String errorCode, final HttpStatus httpStatus,
            final Throwable cause) {
        super(message, errorCode, httpStatus, cause);
    }

}
