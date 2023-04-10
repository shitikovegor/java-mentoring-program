package com.jmp.kafka.exception.handler;

import java.time.Instant;

import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import com.jmp.kafka.exception.dto.BadRequestErrorResponse;
import com.jmp.kafka.exception.dto.BaseErrorResponse;
import com.jmp.kafka.exception.exception.BaseErrorCode;
import com.jmp.kafka.exception.exception.BaseException;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred";

    private static final String BAD_REQUEST_MESSAGE = "Invalid request parameters";

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseErrorResponse> handle(final BaseException e, final ServerWebExchange serverWebExchange) {
        final var httpStatus = e.getHttpStatus();
        final var errorCode = e.getErrorCode();
        final var message = e.getMessage();
        return new ResponseEntity<>(buildBaseErrorResponse(serverWebExchange, errorCode, message, httpStatus),
                httpStatus);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<BadRequestErrorResponse> handle(final WebExchangeBindException e,
            final ServerWebExchange serverWebExchange) {
        final var httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                buildBadRequestErrorResponse(serverWebExchange, BaseErrorCode.BAD_REQUEST_ERROR_CODE,
                        httpStatus, BAD_REQUEST_MESSAGE, e),
                httpStatus);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<BaseErrorResponse> handle(final ServerWebInputException e,
            final ServerWebExchange serverWebExchange) {
        final var httpStatus = HttpStatus.BAD_REQUEST;
        final var message = e.getReason();
        return new ResponseEntity<>(buildBaseErrorResponse(serverWebExchange, BaseErrorCode.BAD_REQUEST_ERROR_CODE,
                message, httpStatus), httpStatus);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseErrorResponse> handle(final ConstraintViolationException e,
            final ServerWebExchange serverWebExchange) {
        final var httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                buildBaseErrorResponse(serverWebExchange, BaseErrorCode.BAD_REQUEST_ERROR_CODE, e.getMessage(),
                        httpStatus),
                httpStatus);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<BaseErrorResponse> handle(final Throwable e, final ServerWebExchange serverWebExchange) {
        log.error("Error occurred while processing request", e);
        final var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(buildBaseErrorResponse(serverWebExchange, BaseErrorCode.INTERNAL_SERVER_ERROR_CODE,
                INTERNAL_SERVER_ERROR_MESSAGE, httpStatus), httpStatus);
    }

    private BaseErrorResponse buildBaseErrorResponse(final ServerWebExchange exchange, final String code,
            final String message,
            final HttpStatus status) {
        return BaseErrorResponse.builder()
                .timestamp(Instant.now())
                .error(status.getReasonPhrase())
                .code(code)
                .path(exchange.getRequest().getPath().value())
                .message(message)
                .build();
    }

    private BadRequestErrorResponse buildBadRequestErrorResponse(final ServerWebExchange exchange, final String code,
            final HttpStatus status, final String message,
            final WebExchangeBindException e) {
        final var validationDetails = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> BadRequestErrorResponse.ValidationDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .toList();
        return BadRequestErrorResponse.builder()
                .timestamp(Instant.now())
                .error(status.getReasonPhrase())
                .code(code)
                .path(exchange.getRequest().getPath().value())
                .message(message)
                .details(validationDetails)
                .build();
    }

}
