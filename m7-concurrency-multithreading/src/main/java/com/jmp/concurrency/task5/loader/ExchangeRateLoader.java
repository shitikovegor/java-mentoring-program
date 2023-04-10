package com.jmp.concurrency.task5.loader;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.model.ExchangeRate;
import com.jmp.concurrency.task5.repository.ExchangeRateRepository;
import com.jmp.concurrency.task5.util.ExchangeRateValidator;

@Slf4j
@RequiredArgsConstructor
public class ExchangeRateLoader {

    private final ObjectMapper mapper;

    private final ExchangeRateRepository repository;

    private final ExchangeRateValidator validator;

    public void loadExchangeRates(final String resourceName) {
        try {
            log.info("Exchange rates loading started");
            final List<ExchangeRate> rates = mapper.readValue(
                    this.getClass().getClassLoader().getResourceAsStream(resourceName),
                    new TypeReference<>() {

                    });
            rates.forEach(rate -> {
                try {
                    validator.validateExchangeRate(rate);
                    repository.save(rate);
                } catch (final ExchangeRateException e) {
                    log.error(e.getMessage(), e);
                }
                log.info("Exchange rates loading finished");
            });
        } catch (final IOException e) {
            throw new ExchangeRateException("Exchange rates loading failed");
        }
    }

}
