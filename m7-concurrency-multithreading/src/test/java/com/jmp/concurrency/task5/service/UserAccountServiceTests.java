package com.jmp.concurrency.task5.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jmp.concurrency.task5.exception.AccountException;
import com.jmp.concurrency.task5.loader.ExchangeRateLoader;
import com.jmp.concurrency.task5.loader.UserAccountLoader;
import com.jmp.concurrency.task5.model.Account;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.repository.ExchangeRateRepository;
import com.jmp.concurrency.task5.repository.UserAccountRepository;
import com.jmp.concurrency.task5.util.AccountValidator;
import com.jmp.concurrency.task5.util.ExchangeRateValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserAccountServiceTests {

    private static final String USER_ID = "0be8a43a-0ee1-4c5b-8e0b-4875f1c47772";

    private UserAccountService service;

    @BeforeEach
    void setUp() {
        final var mapper = new ObjectMapper();
        final var rateValidator = new ExchangeRateValidator();
        final var rateRepository = new ExchangeRateRepository();
        final var rateLoader = new ExchangeRateLoader(mapper, rateRepository, rateValidator);
        final var rateService = new ExchangeRateService(rateRepository, rateValidator);
        final var accountRepository = new UserAccountRepository();
        final var accountValidator = new AccountValidator();
        final var accountLoader = new UserAccountLoader(mapper, accountRepository, accountValidator);
        service = new UserAccountService(accountRepository, rateService, accountValidator);

        rateLoader.loadExchangeRates("task5/exchange-rates.json");
        accountLoader.loadUserAccounts("task5/accounts");
    }

    @Test
    void getAccountByUserId() {
        // given
        final var expected = new Account(
                Map.of(Currency.EUR, BigDecimal.valueOf(1000), Currency.USD, BigDecimal.valueOf(6800), Currency.BYN,
                        BigDecimal.valueOf(1150)));
        // when
        final var result = service.getAccountByUserId(USER_ID);
        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAccountByUserIdNotFound() {
        // then
        assertThatThrownBy(() -> service.getAccountByUserId("id"))
                .isInstanceOf(AccountException.class);
    }

    @SneakyThrows
    @Test
    void makeExchange() {
        // given
        final var expected = new Account(
                Map.of(Currency.EUR, new BigDecimal("1000"), Currency.USD, new BigDecimal("7400"), Currency.BYN,
                        new BigDecimal("1988.800")));
        final var executorService = Executors.newFixedThreadPool(2);
        final var thread1 = new User(3, Boolean.FALSE, USER_ID, Currency.BYN, Currency.USD, BigDecimal.valueOf(100),
                service);
        final var thread2 = new User(3, Boolean.FALSE, USER_ID, Currency.USD, Currency.BYN, BigDecimal.valueOf(279.8),
                service);
        final var thread3 = new User(3, Boolean.FALSE, USER_ID, Currency.USD, Currency.BYN, BigDecimal.valueOf(279.8),
                service);
        // when
        executorService.submit(thread1);
        executorService.submit(thread2);
        executorService.submit(thread3);
        Thread.sleep(1000);
        // then
        assertThat(service.getAccountByUserId(USER_ID).getAccountsByCurrency())
                .isEqualTo(expected.getAccountsByCurrency());
    }

    @SneakyThrows
    @Test
    void convertAccountCurrency() {
        // given
        final var expected = new Account(
                Map.of(Currency.EUR, new BigDecimal("1000"), Currency.USD, new BigDecimal("6500"), Currency.BYN,
                        new BigDecimal("1988.200")));
        final var executorService = Executors.newFixedThreadPool(2);
        final var thread1 = new User(3, Boolean.TRUE, USER_ID, Currency.USD, Currency.BYN, BigDecimal.valueOf(279.8),
                service);
        final var thread2 = new User(3, Boolean.TRUE, USER_ID, Currency.BYN, Currency.USD, BigDecimal.valueOf(100),
                service);
        final var thread3 = new User(3, Boolean.TRUE, USER_ID, Currency.BYN, Currency.USD, BigDecimal.valueOf(100),
                service);
        // when
        executorService.submit(thread1);
        executorService.submit(thread2);
        executorService.submit(thread3);
        Thread.sleep(1000);
        // then
        assertThat(service.getAccountByUserId(USER_ID).getAccountsByCurrency())
                .isEqualTo(expected.getAccountsByCurrency());
    }

    record User(int limit, boolean isConversionOperation, String userId, Currency purchaseCurrency,
            Currency saleCurrency, BigDecimal saleCurrencyAmount, UserAccountService service) implements Runnable {

        @Override
        public void run() {
            var count = 0;
            while (count < limit) {
                if (isConversionOperation) {
                    service.convertAccountCurrency(userId, saleCurrency, purchaseCurrency, saleCurrencyAmount);
                } else {
                    service.makeExchange(userId, purchaseCurrency, saleCurrency, saleCurrencyAmount);
                }
                count++;
            }
        }

    }

}
