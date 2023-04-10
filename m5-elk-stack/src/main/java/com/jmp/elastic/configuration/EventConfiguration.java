package com.jmp.elastic.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EventConfiguration extends ElasticsearchRestClientAutoConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        final var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(InstantSerializer.INSTANCE);
        final var objectMapper = new ObjectMapper();
        return objectMapper.registerModule(new Jdk8Module())
                .registerModule(javaTimeModule).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP, Nulls.SKIP))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    @Primary
    public RestClient restClient(final ElasticsearchProperties properties) {
        final var hostname = properties.getUris().get(0);
        return RestClient.builder(HttpHost.create(hostname)).build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(final RestClient restClient, final ObjectMapper objectMapper) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(final ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

}
