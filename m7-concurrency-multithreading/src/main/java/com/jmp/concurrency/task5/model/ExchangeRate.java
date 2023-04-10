package com.jmp.concurrency.task5.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    private Currency baseCurrency;

    private Currency counterCurrency;

    private BigDecimal purchaseRate;

    private BigDecimal saleRate;

}
