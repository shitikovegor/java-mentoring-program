package com.jmp.concurrency.task5.util;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;

import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.model.ExchangeRate;

@NoArgsConstructor
public class ExchangeRateValidator {

    public void validateExchangeRate(final ExchangeRate exchangeRate) {
        if (exchangeRate.getBaseCurrency() == null) {
            throw new ExchangeRateException("Base currency must be defined");
        }
        if (exchangeRate.getCounterCurrency() == null) {
            throw new ExchangeRateException("Counter currency must be defined");
        }
        if (exchangeRate.getBaseCurrency() == exchangeRate.getCounterCurrency()) {
            throw new ExchangeRateException("Base currency must be different from counter");
        }
        if (exchangeRate.getPurchaseRate() == null || exchangeRate.getPurchaseRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExchangeRateException("Purchase rate must be more than 0");
        }
        if (exchangeRate.getSaleRate() == null || exchangeRate.getSaleRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExchangeRateException("Sale rate must be more than 0");
        }
    }

}
