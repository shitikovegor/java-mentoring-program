package com.jmp.advanced.sql;

import java.util.Random;

public class SqlDataGenerator {

    public static void main(String[] args) {
        // final var phones = generatePhones();
        final var results = generateExamResults();
        System.out.println(results);
    }

    private static String generatePhones() {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < 21; i++) {
            final var phone = new Random().nextInt(10000000) + 1;
            final var format = String.format("(%s, %s, '%s')", i, i, phone);
            result.append(format + ",\n");
        }
        return result.toString();
    }

    private static String generateExamResults() {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < 101; i++) {
            final var studentId = new Random().nextInt(20) + 1;
            final var subjectId = new Random().nextInt(10) + 1;
            final var mark = new Random().nextInt(10) + 1;
            final var format = String.format("(%s, %s, %s, %s)", i, studentId, subjectId, mark);
            result.append(format + ",\n");
        }
        return result.toString();
    }

}
