package com.jmp.kafka.client.exception;

import org.springframework.http.HttpStatus;

import com.jmp.kafka.exception.exception.BaseException;

public class ClientServiceException extends BaseException {

    public ClientServiceException(final String message, final HttpStatus httpStatus,
            final String errorCode) {
        super(message, httpStatus, errorCode);
    }

    public static ClientServiceException createOrderNotFoundException(final String orderId) {
        return new ClientServiceException("Order with id " + orderId + " not found", HttpStatus.NOT_FOUND,
                ClientServiceErrorCode.NOT_FOUND);
    }

}
