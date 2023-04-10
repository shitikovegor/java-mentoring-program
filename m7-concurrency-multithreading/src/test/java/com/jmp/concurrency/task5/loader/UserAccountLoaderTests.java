package com.jmp.concurrency.task5.loader;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jmp.concurrency.task5.model.Account;
import com.jmp.concurrency.task5.model.Currency;
import com.jmp.concurrency.task5.repository.UserAccountRepository;
import com.jmp.concurrency.task5.util.AccountValidator;

@ExtendWith(MockitoExtension.class)
class UserAccountLoaderTests {

    @Mock
    private UserAccountRepository repository;

    @Mock
    private AccountValidator validator;

    @Test
    void loadUserAccounts() {
        final var mapper = new ObjectMapper();
        final var loader = new UserAccountLoader(mapper, repository, validator);
        loader.loadUserAccounts("task5/accounts");
        Mockito.verify(repository)
                .save("0be8a43a-0ee1-4c5b-8e0b-4875f1c47772", new Account(Map.of(
                        Currency.EUR, new BigDecimal("1000"),
                        Currency.USD, new BigDecimal("6800"),
                        Currency.BYN, new BigDecimal("1150"))));
    }

}
