package com.jmp.concurrency.task5.loader;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.model.ExchangeRate;
import com.jmp.concurrency.task5.repository.ExchangeRateRepository;
import com.jmp.concurrency.task5.util.ExchangeRateValidator;

@ExtendWith(MockitoExtension.class)
class ExchangeRateLoaderTests {

    @Mock
    private ExchangeRateRepository repository;

    @Mock
    private ExchangeRateValidator validator;

    @Test
    void loadExchangeRates() {
        final var mapper = new ObjectMapper();
        final var loader = new ExchangeRateLoader(mapper, repository, validator);
        loader.loadExchangeRates("task5/exchange-rates.json");
        Mockito.verify(repository)
                .save(new ExchangeRate(Currency.EUR, Currency.BYN, new BigDecimal("2.96"), new BigDecimal("2.97")));
        Mockito.verify(repository)
                .save(new ExchangeRate(Currency.USD, Currency.BYN, new BigDecimal("2.796"), new BigDecimal("2.798")));
        Mockito.verify(repository)
                .save(new ExchangeRate(Currency.EUR, Currency.USD, new BigDecimal("1.0645"), new BigDecimal("1.0646")));
    }

}
