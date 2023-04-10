package com.jmp.search.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BookException extends RuntimeException {

    private final String errorCode;

    private final HttpStatus httpStatus;

    public BookException(final String message, final String errorCode, final HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}
