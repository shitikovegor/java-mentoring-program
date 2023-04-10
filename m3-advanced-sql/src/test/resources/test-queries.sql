-- Task 9 - Select students who does not pass any exam using each the following operator: – 0.5 points.
-- Outer join
-- Subquery with ‘not in’ clause
-- Subquery with ‘any ‘ clause
-- Check which approach is faster for 1000, 10K, 100K exams and 10, 1K, 100K students
-- 1. Outer join
EXPLAIN ANALYSE SELECT name, surname
                FROM student
                         LEFT JOIN exam_result er on student.id = er.student_id
                WHERE er.student_id IS NULL;
-- 2. Subquery with ‘not in’ clause
EXPLAIN ANALYSE SELECT name, surname
                FROM student
                WHERE student.id NOT IN (SELECT student_id FROM exam_result);
-- 3. Subquery with ‘any ‘ clause
EXPLAIN ANALYSE SELECT name, surname
                FROM student
                WHERE NOT student.id = ANY (SELECT student_id FROM exam_result);
