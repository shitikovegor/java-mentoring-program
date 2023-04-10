package com.jmp.concurrency.task5.util;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;

import com.jmp.concurrency.task5.exception.AccountException;
import com.jmp.concurrency.task5.model.Account;
import com.jmp.concurrency.task5.model.Currency;

@NoArgsConstructor
public class AccountValidator {

    public void validateAccount(final Account account) {
        if (account.getAccountsByCurrency().values().stream()
                .anyMatch(sum -> sum == null || sum.compareTo(BigDecimal.ZERO) < 0)) {
            throw new AccountException("Sum of account by currency must be more than 0");
        }
    }

    public void validateAccountWithdrawalOption(final Account account, final Currency currency,
            final BigDecimal withdrawAmount) {
        if (account.getAccountsByCurrency().get(currency).compareTo(withdrawAmount) < 0) {
            throw new AccountException("Sum of account by currency must be more than withdraw amount");
        }
    }

}
