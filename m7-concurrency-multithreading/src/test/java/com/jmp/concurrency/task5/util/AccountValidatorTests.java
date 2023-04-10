package com.jmp.concurrency.task5.util;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jmp.concurrency.task5.exception.AccountException;
import com.jmp.concurrency.task5.model.Account;
import com.jmp.concurrency.task5.model.Currency;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountValidatorTests {

    private AccountValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AccountValidator();
    }

    @Test
    void validateAccount() {
        // given
        final var account = new Account(Map.of(Currency.USD, BigDecimal.valueOf(1000),
                Currency.EUR, BigDecimal.valueOf(3000)));
        // then
        assertThatNoException().isThrownBy(() -> validator.validateAccount(account));
    }

    @Test
    void validateAccount_ThrowException() {
        // given
        final var account = new Account(Map.of(Currency.USD, BigDecimal.valueOf(1000),
                Currency.EUR, BigDecimal.valueOf(-3000)));
        // then
        assertThatThrownBy(() -> validator.validateAccount(account))
                .isInstanceOf(AccountException.class);
    }

    @Test
    void validateAccountWithdrawalOption() {
        // given
        final var account = new Account(Map.of(Currency.USD, BigDecimal.valueOf(1000),
                Currency.EUR, BigDecimal.valueOf(3000)));
        // then
        assertThatNoException().isThrownBy(
                () -> validator.validateAccountWithdrawalOption(account, Currency.USD, BigDecimal.valueOf(500)));
    }

    @Test
    void validateAccountWithdrawalOption_ThrowException() {
        // given
        final var account = new Account(Map.of(Currency.USD, BigDecimal.valueOf(1000),
                Currency.EUR, BigDecimal.valueOf(-3000)));
        // then
        assertThatThrownBy(
                () -> validator.validateAccountWithdrawalOption(account, Currency.USD, BigDecimal.valueOf(1500)))
                        .isInstanceOf(AccountException.class);
    }

}
