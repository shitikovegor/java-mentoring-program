package com.jmp.concurrency.task5.repository;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jmp.concurrency.task5.model.Account;
import com.jmp.concurrency.task5.model.Currency;

import static org.assertj.core.api.Assertions.assertThat;

class UserAccountRepositoryTests {

    private UserAccountRepository repository;

    @Test
    void getById() {
        // given
        final var account = new Account(Map.of(Currency.USD, BigDecimal.valueOf(1000)));
        repository = new UserAccountRepository(Map.of("id", account));
        // when
        final var result = repository.getById("id");
        // then
        assertThat(result).isPresent()
                .get().isEqualTo(account);
    }

    @Test
    void getAll() {
        // given
        final var account = new Account(Map.of(Currency.USD, BigDecimal.valueOf(1000)));
        repository = new UserAccountRepository(Map.of("id", account));
        // when
        final var result = repository.getAll();
        // then
        assertThat(result).isNotEmpty()
                .isEqualTo(Map.of("id", account));
    }

}
