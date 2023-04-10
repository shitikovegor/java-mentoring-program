package com.jmp.concurrency.task5.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.model.ExchangeRate;

public class ExchangeRateRepository {

    private final List<ExchangeRate> exchangeRates;

    public ExchangeRateRepository(final List<ExchangeRate> exchangeRates) {
        this.exchangeRates = new CopyOnWriteArrayList<>(exchangeRates);
    }

    public ExchangeRateRepository() {
        this.exchangeRates = new CopyOnWriteArrayList<>();
    }

    public void save(final ExchangeRate exchangeRate) {
        final var isRatePresent = findByCurrencies(exchangeRate.getBaseCurrency(), exchangeRate.getCounterCurrency())
                .isPresent();
        if (isRatePresent) {
            throw new ExchangeRateException("Exchange rate is present in rates");
        }
        exchangeRates.add(exchangeRate);
    }

    public void update(final ExchangeRate exchangeRate) {
        findByCurrencies(exchangeRate.getBaseCurrency(), exchangeRate.getCounterCurrency())
                .ifPresent(exchangeRates::remove);
        exchangeRates.add(exchangeRate);
    }

    public void delete(final Currency baseCurrency, final Currency counterCurrency) {
        exchangeRates.removeIf(
                rate -> rate.getBaseCurrency() == baseCurrency && rate.getCounterCurrency() == counterCurrency);
    }

    public List<ExchangeRate> getAll() {
        return exchangeRates;
    }

    public Optional<ExchangeRate> findByCurrencies(final Currency first, final Currency second) {
        return exchangeRates.stream()
                .filter(rate -> rate.getBaseCurrency() == first && rate.getCounterCurrency() == second ||
                        rate.getBaseCurrency() == second && rate.getCounterCurrency() == first)
                .findFirst();
    }

}
