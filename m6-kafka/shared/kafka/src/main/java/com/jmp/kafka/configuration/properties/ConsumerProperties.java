package com.jmp.kafka.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.kafka.consumer")
public class ConsumerProperties {

    private String topic;

    private int threadsNumber;

    private int maxTasks;

    private int schedulerTtl;

}
