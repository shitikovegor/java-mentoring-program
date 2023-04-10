package com.jmp.advanced.multithreading.task4.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jmp.advanced.multithreading.task4.dto.EmployeeDto;
import com.jmp.advanced.multithreading.task4.model.Employee;
import com.jmp.advanced.multithreading.task4.repository.EmployeeClient;
import com.jmp.advanced.multithreading.task4.repository.SalaryClient;

@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeClient employeeClient;

    private final SalaryClient salaryClient;

    public CompletionStage<List<EmployeeDto>> hiredEmployees() {
        final var employeesWithSalaries = CompletableFuture.completedFuture(employeeClient.hiredEmployees())
                .thenApply(employees -> employees.stream()
                        .map(employee -> CompletableFuture.supplyAsync(() -> mapEmployeeWithSalary(employee)))
                        .collect(Collectors.toList()))
                .join();
        final var allFutures = CompletableFuture.allOf(
                employeesWithSalaries.toArray(new CompletableFuture[0]));
        return allFutures.thenApply(future -> employeesWithSalaries.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
    }

    public CompletionStage<Void> printHiredEmployees() {
        return hiredEmployees().thenAcceptAsync(System.out::println);
    }

    private EmployeeDto mapEmployeeWithSalary(final Employee employee) {
        log.info("Fill salary for employee - {}", employee);
        final var salary = salaryClient.getSalary(employee.getId());
        return EmployeeDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .salary(salary)
                .build();
    }

}
