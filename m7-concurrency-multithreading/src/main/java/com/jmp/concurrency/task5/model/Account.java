package com.jmp.concurrency.task5.model;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Map<Currency, BigDecimal> accountsByCurrency;

    public void deposit(final Currency currency, final BigDecimal amount) {
        final var accountAmountByCurrency = accountsByCurrency.get(currency);
        accountsByCurrency.put(currency, accountAmountByCurrency.add(amount));
    }

    public void withdraw(final Currency currency, final BigDecimal amount) {
        final var accountAmountByCurrency = accountsByCurrency.get(currency);
        accountsByCurrency.put(currency, accountAmountByCurrency.subtract(amount));
    }

}
