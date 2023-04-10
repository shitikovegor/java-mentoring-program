package com.jmp.concurrency.task5.service;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.loader.ExchangeRateLoader;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.model.ExchangeRate;
import com.jmp.concurrency.task5.repository.ExchangeRateRepository;
import com.jmp.concurrency.task5.util.ExchangeRateValidator;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateServiceTests {

    private ExchangeRateRepository repository;

    private ExchangeRateService service;

    @BeforeEach
    void setUp() {
        final var validator = new ExchangeRateValidator();
        repository = new ExchangeRateRepository();
        final var loader = new ExchangeRateLoader(new ObjectMapper(), repository, validator);
        loader.loadExchangeRates("task5/exchange-rates.json");
        service = new ExchangeRateService(repository, validator);
    }

    @Test
    void addExchangeRate() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.EUR, Currency.GBP, BigDecimal.valueOf(0.88),
                BigDecimal.valueOf(0.88));
        // when
        service.addExchangeRate(exchangeRate);
        // then
        assertThat(repository.findByCurrencies(Currency.EUR, Currency.GBP)).isPresent();

    }

    @Test
    void updateExchangeRate() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.EUR, Currency.BYN, BigDecimal.valueOf(0.88),
                BigDecimal.valueOf(0.88));
        // when
        service.updateExchangeRate(exchangeRate);
        // then
        assertThat(repository.findByCurrencies(Currency.EUR, Currency.BYN)).isPresent()
                .get().isEqualTo(exchangeRate);
    }

    @Test
    void getExchangeRates() {
        // given
        final var exchangeRate1 = new ExchangeRate(Currency.EUR, Currency.BYN, BigDecimal.valueOf(2.96),
                BigDecimal.valueOf(2.97));
        final var exchangeRate2 = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.796),
                BigDecimal.valueOf(2.798));
        final var exchangeRate3 = new ExchangeRate(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.0645),
                BigDecimal.valueOf(1.0646));
        // when
        final var result = service.getExchangeRates();
        // then
        assertThat(result).isNotEmpty().hasSize(3)
                .isEqualTo(List.of(exchangeRate1, exchangeRate2, exchangeRate3));
    }

    @Test
    void getByCurrencies() {
        // given
        final var expected = new ExchangeRate(Currency.EUR, Currency.BYN, BigDecimal.valueOf(2.96),
                BigDecimal.valueOf(2.97));
        // when
        final var result = service.getByCurrencies(Currency.EUR, Currency.BYN);
        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getByCurrencies_NotFound() {
        // then
        Assertions.assertThatThrownBy(() -> service.getByCurrencies(Currency.EUR, Currency.GBP))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void delete() {
        // when
        service.delete(Currency.EUR, Currency.BYN);
        // then
        assertThat(repository.findByCurrencies(Currency.EUR, Currency.BYN)).isNotPresent();
    }

}
