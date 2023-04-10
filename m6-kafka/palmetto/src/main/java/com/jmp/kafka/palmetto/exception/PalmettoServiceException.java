package com.jmp.kafka.palmetto.exception;

import org.springframework.http.HttpStatus;

import com.jmp.kafka.exception.exception.BaseException;

public class PalmettoServiceException extends BaseException {

    public PalmettoServiceException(final String message, final HttpStatus httpStatus,
            final String errorCode) {
        super(message, httpStatus, errorCode);
    }

}
