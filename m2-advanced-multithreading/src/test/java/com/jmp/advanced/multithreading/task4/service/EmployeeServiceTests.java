package com.jmp.advanced.multithreading.task4.service;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jmp.advanced.multithreading.task4.dto.EmployeeDto;
import com.jmp.advanced.multithreading.task4.mock.EmployeeFileClient;
import com.jmp.advanced.multithreading.task4.mock.SalaryFileClient;
import com.jmp.advanced.multithreading.task4.repository.EmployeeClient;
import com.jmp.advanced.multithreading.task4.repository.SalaryClient;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeServiceTests {

    private EmployeeService service;

    @BeforeEach
    void init() {
        final EmployeeClient employeeClient = new EmployeeFileClient();
        final SalaryClient salaryClient = new SalaryFileClient();
        service = new EmployeeService(employeeClient, salaryClient);
    }

    @SneakyThrows
    @Test
    void hiredEmployees() {
        // given
        final List<EmployeeDto> expected = new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream(
                "task4/employees-expected.json"),
                new TypeReference<>() {

                });
        // when
        final var employees = service.hiredEmployees();
        // then
        assertThat(employees.toCompletableFuture().get()).isEqualTo(expected);

    }

    @Test
    void printHiredEmployees() {
        // when
        final var result = service.printHiredEmployees();
        result.toCompletableFuture().join();
        // then
        assertThat(result).isCompleted();
    }

}
