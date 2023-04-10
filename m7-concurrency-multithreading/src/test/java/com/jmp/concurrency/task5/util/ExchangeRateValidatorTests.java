package com.jmp.concurrency.task5.util;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.model.ExchangeRate;

import static org.assertj.core.api.Assertions.assertThatNoException;

class ExchangeRateValidatorTests {

    private ExchangeRateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ExchangeRateValidator();
    }

    @Test
    void validateExchangeRate() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.75),
                BigDecimal.valueOf(2.78));
        // then
        assertThatNoException().isThrownBy(() -> validator.validateExchangeRate(exchangeRate));
    }

    @Test
    void validateExchangeRate_BaseCurrencyNull_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(null, Currency.BYN, BigDecimal.valueOf(2.75),
                BigDecimal.valueOf(2.78));
        // then
        Assertions.assertThatThrownBy(() -> validator.validateExchangeRate(exchangeRate))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessage("Base currency must be defined");
    }

    @Test
    void validateExchangeRate_CounterCurrencyNull_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.BYN, null, BigDecimal.valueOf(2.75),
                BigDecimal.valueOf(2.78));
        // then
        Assertions.assertThatThrownBy(() -> validator.validateExchangeRate(exchangeRate))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessage("Counter currency must be defined");
    }

    @Test
    void validateExchangeRate_SimilarCurrencies_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.BYN, Currency.BYN, BigDecimal.valueOf(2.75),
                BigDecimal.valueOf(2.78));
        // then
        Assertions.assertThatThrownBy(() -> validator.validateExchangeRate(exchangeRate))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessage("Base currency must be different from counter");
    }

    @Test
    void validateExchangeRate_PurchaseRateNull_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, null,
                BigDecimal.valueOf(2.78));
        // then
        Assertions.assertThatThrownBy(() -> validator.validateExchangeRate(exchangeRate))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessage("Purchase rate must be more than 0");
    }

    @Test
    void validateExchangeRate_PurchaseRateLessZero_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(-2.75),
                BigDecimal.valueOf(2.78));
        // then
        Assertions.assertThatThrownBy(() -> validator.validateExchangeRate(exchangeRate))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessage("Purchase rate must be more than 0");
    }

    @Test
    void validateExchangeRate_SaleRateNull_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.78), null);
        // then
        Assertions.assertThatThrownBy(() -> validator.validateExchangeRate(exchangeRate))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessage("Sale rate must be more than 0");
    }

    @Test
    void validateExchangeRate_SaleRateLessZero_ThrowException() {
        // given
        final var exchangeRate = new ExchangeRate(Currency.USD, Currency.BYN, BigDecimal.valueOf(2.75),
                BigDecimal.valueOf(-2.78));
        // then
        Assertions.assertThatThrownBy(() -> validator.validateExchangeRate(exchangeRate))
                .isInstanceOf(ExchangeRateException.class)
                .hasMessage("Sale rate must be more than 0");
    }

}
