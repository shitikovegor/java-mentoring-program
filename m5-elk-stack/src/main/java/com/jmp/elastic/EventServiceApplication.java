package com.jmp.elastic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.jmp.elastic.configuration.properties.EventProperties;
import com.jmp.elastic.configuration.properties.LoggerProperties;

@SpringBootApplication
@EnableConfigurationProperties({ EventProperties.class, LoggerProperties.class })
public class EventServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }

}
