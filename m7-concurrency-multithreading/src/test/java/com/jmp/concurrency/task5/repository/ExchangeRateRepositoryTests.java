package com.jmp.concurrency.task5.repository;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.model.ExchangeRate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeRateRepositoryTests {

    private ExchangeRateRepository repository;

    @Test
    void save() {
        // given
        repository = new ExchangeRateRepository();
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.5),
                BigDecimal.valueOf(2.6));
        // then
        assertThatNoException().isThrownBy(() -> repository.save(exchangeRate));
    }

    @Test
    void saveWhenRateIsPresent_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.5),
                BigDecimal.valueOf(2.6));
        repository = new ExchangeRateRepository(List.of(exchangeRate));
        // then
        assertThatThrownBy(() -> repository.save(exchangeRate))
                .isInstanceOf(ExchangeRateException.class);
    }

    @Test
    void getAll() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.5),
                BigDecimal.valueOf(2.6));
        repository = new ExchangeRateRepository(List.of(exchangeRate));
        // when
        final var result = repository.getAll();
        // then
        assertThat(result).isNotEmpty()
                .first().satisfies(rate -> assertThat(rate).isEqualTo(exchangeRate));
    }

    @Test
    void findCurrencies() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.5),
                BigDecimal.valueOf(2.6));
        repository = new ExchangeRateRepository(List.of(exchangeRate));
        // when
        final var result = repository.findByCurrencies(Currency.USD, Currency.BYN);
        // then
        assertThat(result).isPresent()
                .get().isEqualTo(exchangeRate);
    }

}
