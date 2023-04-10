package com.jmp.grpc.avro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.jmp.grpc")
public class AvroServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AvroServiceApplication.class, args);
    }

}
