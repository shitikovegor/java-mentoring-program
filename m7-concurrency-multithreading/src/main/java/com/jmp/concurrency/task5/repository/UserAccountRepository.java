package com.jmp.concurrency.task5.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.jmp.concurrency.task5.model.Account;

@RequiredArgsConstructor
public class UserAccountRepository {

    private final Map<String, Account> accountsByUserId;

    public UserAccountRepository() {
        this.accountsByUserId = new HashMap<>();
    }

    public Optional<Account> getById(final String userId) {
        return Optional.ofNullable(accountsByUserId.get(userId));
    }

    public Map<String, Account> getAll() {
        return accountsByUserId;
    }

    public void save(final String userId, final Account account) {
        accountsByUserId.put(userId, account);
    }

    public void delete(final String userId) {
        accountsByUserId.remove(userId);
    }

}
