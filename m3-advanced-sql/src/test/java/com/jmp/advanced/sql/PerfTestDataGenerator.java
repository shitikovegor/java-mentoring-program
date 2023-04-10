package com.jmp.advanced.sql;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class PerfTestDataGenerator {

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = { 10, 1000, 100_000 })
    void generateStudents(final int dataAmount) {
        final var skills = List.of("Stress management", "Research", "Data analysis", "Teamwork", "Problem-solving",
                "Business development");
        final var script = new StringBuilder(
                "INSERT INTO student(id, name, surname, dob, primary_skill, created_datetime, updated_datetime)\n" +
                        "VALUES ");
        for (var i = 1; i < dataAmount + 1; i++) {
            final var name = "name" + i;
            final var surname = "surname" + i;
            final var dob = LocalDate.of(new Random().nextInt(3) + 2000, new Random().nextInt(11) + 1,
                    new Random().nextInt(27) + 1);
            final var skillIndex = new Random().nextInt(4) + 1;
            final var template = "(%s, '%s', '%s', '%s', '%s', now(), now()),\n";
            final var value = String.format(i != 1 ? "       " + template : template, i, name, surname, dob,
                    skills.get(skillIndex));
            script.append(value);
        }
        script.replace(script.length() - 2, script.length(), ";");

        final var path = Paths.get("src/test/resources/data/students-generator-" + dataAmount + ".sql");
        Files.deleteIfExists(path);
        Files.write(path, script.toString().getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource(value = "examData")
    void generateExamResults(final int examAmount, final int students) {
        final var script = new StringBuilder(
                "INSERT INTO exam_result(id, student_id, subject_id, mark)\n" +
                        "VALUES ");
        for (var i = 1; i < examAmount + 1; i++) {
            final var studentId = new Random().nextInt(students) + 1;
            final var subjectId = new Random().nextInt(10) + 1;
            final var mark = new Random().nextInt(10) + 1;
            final var template = "(%s, %s, %s, %s),\n";
            final var value = String.format(i != 1 ? "       " + template : template, i, studentId, subjectId, mark);
            script.append(value);
        }
        script.replace(script.length() - 2, script.length(), ";");

        final var path = Paths.get("src/test/resources/data/exams-generator-" + examAmount + ".sql");
        Files.deleteIfExists(path);
        Files.write(path, script.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static Stream<Arguments> examData() {
        return Stream.of(Arguments.of(1000, 10), Arguments.of(10_000, 1000), Arguments.of(100_000, 100_000));
    }

}
