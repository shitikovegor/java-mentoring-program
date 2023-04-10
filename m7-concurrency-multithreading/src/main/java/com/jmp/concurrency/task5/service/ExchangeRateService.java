package com.jmp.concurrency.task5.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.model.ExchangeRate;
import com.jmp.concurrency.task5.repository.ExchangeRateRepository;
import com.jmp.concurrency.task5.util.ExchangeRateValidator;

@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository repository;

    private final ExchangeRateValidator validator;

    public void addExchangeRate(final ExchangeRate exchangeRate) {
        validator.validateExchangeRate(exchangeRate);
        repository.save(exchangeRate);
    }

    public void updateExchangeRate(final ExchangeRate exchangeRate) {
        validator.validateExchangeRate(exchangeRate);
        repository.update(exchangeRate);
    }

    public List<ExchangeRate> getExchangeRates() {
        return repository.getAll();
    }

    public ExchangeRate getByCurrencies(final Currency first, final Currency second) {
        return repository.findByCurrencies(first, second)
                .orElseThrow(
                        () -> new ExchangeRateException(
                                "Exchange rate by currencies [" + first + "] and [" + second + "] not found"));
    }

    public void delete(final Currency baseCurrency, final Currency counterCurrency) {
        repository.delete(baseCurrency, counterCurrency);
    }

}
