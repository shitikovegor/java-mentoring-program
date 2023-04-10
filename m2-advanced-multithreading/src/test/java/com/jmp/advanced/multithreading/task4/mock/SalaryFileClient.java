package com.jmp.advanced.multithreading.task4.mock;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import com.jmp.advanced.multithreading.task4.repository.SalaryClient;

public class SalaryFileClient implements SalaryClient {

    private final Map<String, BigDecimal> salariesByEmployeeId;

    @SneakyThrows
    public SalaryFileClient() {
        this.salariesByEmployeeId = new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream(
                "task4/salary.json"),
                new TypeReference<>() {

                });
    }

    @Override
    public BigDecimal getSalary(final String hiredEmployeeId) {
        return salariesByEmployeeId.get(hiredEmployeeId);
    }

}
