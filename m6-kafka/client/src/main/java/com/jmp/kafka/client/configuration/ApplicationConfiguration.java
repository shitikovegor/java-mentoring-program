package com.jmp.kafka.client.configuration;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.jmp.kafka.api.palmetto.client.PalmettoApi;
import com.jmp.kafka.client.configuration.webclient.properties.ApplicationProperties;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

    private static final String PALMETTO_SERVICE_NAME = "palmetto-service";

    @Bean
    public ObjectMapper objectMapper() {
        final var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(InstantSerializer.INSTANCE);
        final var objectMapper = new ObjectMapper();
        return objectMapper.registerModule(new Jdk8Module())
                .registerModule(javaTimeModule).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP, Nulls.SKIP));
    }

    @Bean
    public PalmettoApi palmettoApi(final ApplicationProperties properties) {
        final var config = properties.getApiConfig(PALMETTO_SERVICE_NAME);
        final var webClient = WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
        return new PalmettoApi(webClient);
    }

}
