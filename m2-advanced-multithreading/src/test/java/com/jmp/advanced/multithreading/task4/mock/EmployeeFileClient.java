package com.jmp.advanced.multithreading.task4.mock;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import com.jmp.advanced.multithreading.task4.model.Employee;
import com.jmp.advanced.multithreading.task4.repository.EmployeeClient;

public class EmployeeFileClient implements EmployeeClient {

    private final List<Employee> employees;

    @SneakyThrows
    public EmployeeFileClient() {
        this.employees = new ObjectMapper().readValue(
                this.getClass().getClassLoader().getResource("task4/employees.json"),
                new TypeReference<>() {

                });
    }

    @Override
    public List<Employee> hiredEmployees() {
        return employees;
    }

}
