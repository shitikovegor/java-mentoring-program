package com.jmp.elastic.controller.advice;

import java.time.Instant;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.jmp.elastic.exception.EventException;

@RestControllerAdvice
public class EventExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_CODE = "ISE-0";

    private static final String BAD_REQUEST_ERROR_CODE = "BRE-0";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(final ConstraintViolationException e, final WebRequest request) {
        final var httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(buildErrorResponse(e, request, BAD_REQUEST_ERROR_CODE, httpStatus), httpStatus);
    }

    @ExceptionHandler(EventException.class)
    public ResponseEntity<ErrorResponse> handle(final EventException e, final WebRequest request) {
        final var httpStatus = e.getHttpStatus();
        return new ResponseEntity<>(buildErrorResponse(e, request, e.getErrorCode(), httpStatus), httpStatus);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(final Throwable e, final WebRequest request) {
        final var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(buildErrorResponse(e, request, INTERNAL_SERVER_ERROR_CODE, httpStatus), httpStatus);
    }

    private ErrorResponse buildErrorResponse(final Throwable e, final WebRequest request, final String code,
            final HttpStatus httpStatus) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .error(httpStatus.getReasonPhrase())
                .code(code)
                .message(e.getMessage())
                .path(request.getDescription(false))
                .build();
    }

}
