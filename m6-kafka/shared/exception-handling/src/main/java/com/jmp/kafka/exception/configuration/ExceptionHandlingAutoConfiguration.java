package com.jmp.kafka.exception.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.jmp.kafka.exception.handler.BaseExceptionHandler;

@Import(BaseExceptionHandler.class)
@Configuration
public class ExceptionHandlingAutoConfiguration {

}
