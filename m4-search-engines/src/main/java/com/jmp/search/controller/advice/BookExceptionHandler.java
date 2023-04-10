package com.jmp.search.controller.advice;

import java.time.Instant;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.jmp.search.exception.BookException;

@RestControllerAdvice
public class BookExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(final ConstraintViolationException e, final WebRequest request) {
        final var httpStatus = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(buildErrorResponse(e, request, "BRE-0", httpStatus), httpStatus);
    }

    @ExceptionHandler(BookException.class)
    public ResponseEntity<ErrorResponse> handle(final BookException e, final WebRequest request) {
        final var httpStatus = e.getHttpStatus();
        return new ResponseEntity<>(buildErrorResponse(e, request, e.getErrorCode(), httpStatus), httpStatus);
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
