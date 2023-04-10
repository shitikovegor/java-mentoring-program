package com.jmp.kafka.client.configuration.webclient.properties;

import java.util.Map;
import java.util.Optional;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jmp.kafka.client.configuration.webclient.WebClientConfiguration;

@Data
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private Map<String, WebClientConfiguration> api;

    public WebClientConfiguration getApiConfig(final String serviceName) {
        return Optional.ofNullable(api.get(serviceName))
                .orElseThrow(() -> new IllegalArgumentException("Configuration not found for service " + serviceName));
    }

}
