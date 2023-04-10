package com.jmp.concurrency.task5.loader;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jmp.concurrency.task5.exception.AccountException;
import com.jmp.concurrency.task5.exception.ExchangeRateException;
import com.jmp.concurrency.task5.model.Account;
import com.jmp.concurrency.task5.repository.UserAccountRepository;
import com.jmp.concurrency.task5.util.AccountValidator;

@Slf4j
@RequiredArgsConstructor
public class UserAccountLoader {

    private final ObjectMapper mapper;

    private final UserAccountRepository repository;

    private final AccountValidator validator;

    public void loadUserAccounts(final String accountsResourceName) {
        try {
            log.info("User accounts loading started");
            final var accountsFolder = new File(
                    Objects.requireNonNull(this.getClass().getClassLoader().getResource(accountsResourceName))
                            .getFile());
            final var accountFiles = accountsFolder.list();
            for (final var accountFile : accountFiles) {
                final var fileName = accountsResourceName + "/" + accountFile;
                final var userId = accountFile.substring(0, accountFile.indexOf("."));
                final var account = mapper.readValue(
                        this.getClass().getClassLoader().getResourceAsStream(fileName), Account.class);
                try {
                    validator.validateAccount(account);
                    repository.save(userId, account);
                } catch (final AccountException e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("User accounts loading finished");
        } catch (final IOException e) {
            log.error("User accounts loading failed - {}", e.getMessage(), e);
            throw new ExchangeRateException("User accounts loading failed");
        }
    }

}
