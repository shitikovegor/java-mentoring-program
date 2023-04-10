package com.jmp.concurrency.task5.service;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jmp.concurrency.task5.exception.AccountException;
import com.jmp.concurrency.task5.model.Account;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.model.ExchangeRate;
import com.jmp.concurrency.task5.repository.UserAccountRepository;
import com.jmp.concurrency.task5.util.AccountValidator;

@Slf4j
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository repository;

    private final ExchangeRateService rateService;

    private final AccountValidator validator;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void addAccount(final String userId, final Account account) {
        validator.validateAccount(account);
        repository.save(userId, account);
    }

    public void deleteAccount(final String userId) {
        repository.delete(userId);
    }

    public Account getAccountByUserId(final String userId) {
        final var optionalAccount = repository.getById(userId);
        if (optionalAccount.isEmpty()) {
            throw new AccountException("Account for user " + userId + " not found");
        }
        return optionalAccount.get();
    }

    public BigDecimal makeExchange(final String userId, final Currency purchaseCurrency, final Currency saleCurrency,
            final BigDecimal saleCurrencyAmount) {
        log.info("Make exchange for user {}, purchase currency - {}", userId, purchaseCurrency);
        try {
            lock.writeLock().lock();
            final var exchangeRate = rateService.getByCurrencies(purchaseCurrency, saleCurrency);
            final var exchangedAmount = calculate(purchaseCurrency, exchangeRate, saleCurrencyAmount);
            final var account = getAccountByUserId(userId);
            account.deposit(purchaseCurrency, exchangedAmount);
            repository.save(userId, account);
            return exchangedAmount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Account convertAccountCurrency(final String userId, final Currency currencyFrom,
            final Currency currencyTo, final BigDecimal amountToConvert) {
        log.info("Make conversion for user {}, from currency - {} to - {}", userId, currencyFrom, currencyTo);
        try {
            lock.writeLock().lock();
            final var account = getAccountByUserId(userId);
            validator.validateAccountWithdrawalOption(account, currencyFrom, amountToConvert);
            final var exchangeRate = rateService.getByCurrencies(currencyFrom, currencyTo);
            final var convertedAmount = calculate(currencyTo, exchangeRate, amountToConvert);
            account.deposit(currencyTo, convertedAmount);
            account.withdraw(currencyFrom, amountToConvert);
            log.info("Actual user {} account - {}", userId, account);
            return account;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private BigDecimal calculate(final Currency purchaseCurrency, final ExchangeRate exchangeRate,
            final BigDecimal saleCurrencyAmount) {
        return exchangeRate.getBaseCurrency() == purchaseCurrency
                ? saleCurrencyAmount.divide(exchangeRate.getSaleRate())
                : saleCurrencyAmount.multiply(exchangeRate.getPurchaseRate());
    }

}
