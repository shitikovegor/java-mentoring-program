package com.jmp.kafka.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.jmp.kafka.client.repository")
@ComponentScan(basePackages = "com.jmp.kafka")
public class ClientServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }

}
