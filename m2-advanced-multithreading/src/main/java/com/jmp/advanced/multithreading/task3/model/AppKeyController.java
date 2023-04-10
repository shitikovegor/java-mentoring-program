package com.jmp.advanced.multithreading.task3.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class AppKeyController extends Thread {

    @SneakyThrows
    @Override
    public void run() {
        final var reader = new BufferedReader(new InputStreamReader(System.in));
        var line = "";
        while (true) {
            line = reader.readLine();
            if (line.equals("c")) {
                System.out.println("Pool is terminated by user");
                System.exit(0);
            }
        }
    }

}
