-- Task 1 - Select all primary skills that contain more than one word (please note that both ‘-‘ and ‘ ’ could be used as a separator).
SELECT DISTINCT primary_skill
FROM student
WHERE primary_skill SIMILAR TO '%( |-)+%';

-- Task 2 - Select all students who does not have second name (it is absent or consists from only one letter/letter with dot).
SELECT name
FROM student
WHERE name ~ '^[\w]+(\s?\w?.?)$';

-- Task 3 - Select number of students passed exams for each subject and order result by number of student descending.
SELECT s.name, count(student_id) as students_amount
FROM exam_result
         JOIN subject s on s.id = exam_result.subject_id
GROUP BY s.name
ORDER BY students_amount DESC;

-- Task 4 - Select number of students with the same exam marks for each subject.
SELECT s.name, mark, count(student_id) as students_amount
FROM exam_result
         JOIN subject s on s.id = exam_result.subject_id
GROUP BY s.name, mark
ORDER BY s.name, mark DESC;

-- Task 5 - Select students who passed at least two exams for different subject.
SELECT s.name, s.surname, count(subject_id)
FROM exam_result
         JOIN student s on s.id = exam_result.student_id
GROUP BY s.name, s.surname
HAVING count(distinct subject_id) > 1;

-- Task 6 - Select students who passed at least two exams for the same subject.
SELECT name, surname
FROM student
WHERE id IN (SELECT student_id
             FROM exam_result
             GROUP BY student_id, subject_id
             HAVING count(subject_id) > 1);

-- Task 7 - Select all subjects which exams passed only students with the same primary skills.
SELECT subj.name, count(distinct s.primary_skill)
FROM exam_result
         JOIN student s on s.id = exam_result.student_id
         JOIN subject subj on exam_result.subject_id = subj.id
GROUP BY subj.name
HAVING count(distinct s.primary_skill) = 1;

-- Task 8 - Select all subjects which exams passed only students with the different primary skills.
-- It means that all students passed the exam for the one subject must have different primary skill.
SELECT subj.name, count(distinct s.primary_skill) AS dist_skills
FROM exam_result
         JOIN student s on s.id = exam_result.student_id
         JOIN subject subj on exam_result.subject_id = subj.id
GROUP BY subj.name
HAVING count(distinct s.primary_skill) = count(distinct s.id);

-- Task 9 - Select students who does not pass any exam using each the following operator: – 0.5 points.
-- Outer join
-- Subquery with ‘not in’ clause
-- Subquery with ‘any ‘ clause
-- Check which approach is faster for 1000, 10K, 100K exams and 10, 1K, 100K students
-- 1. Outer join
SELECT name, surname
FROM student
         LEFT JOIN exam_result er on student.id = er.student_id
WHERE er.student_id IS NULL;
-- 2. Subquery with ‘not in’ clause
SELECT name, surname
FROM student
WHERE student.id NOT IN (SELECT student_id FROM exam_result);
-- 3. Subquery with ‘any ‘ clause
SELECT name, surname
FROM student
WHERE NOT student.id = ANY (SELECT student_id FROM exam_result);

-- Task 10 - Select all students whose average mark is bigger than overall average mark.
SELECT s.name, s.surname, avg(mark)
FROM exam_result
         JOIN student s on s.id = exam_result.student_id
GROUP BY s.name, s.surname
HAVING avg(mark) > (SELECT avg(mark) FROM exam_result);

-- Task 11 - Select top 5 students who passed their last exam better than average students.
WITH ordered_marks AS
         (SELECT id, student_id,
                 subject_id,
                 mark,
                 row_number() over (PARTITION BY student_id ORDER BY exam_result.id DESC) AS row_number
          FROM exam_result)

SELECT s.name, s.surname, mark
FROM ordered_marks
         LEFT JOIN student s on s.id = ordered_marks.student_id
WHERE row_number = 1
ORDER BY mark DESC
LIMIT 5;

-- Task 12 - Select biggest mark for each student and add text description for the mark (use COALESCE and WHEN operators) – 0.3 points.
-- In case if student has not passed any exam ‘not passed' should be returned.
-- If student mark is 1,2,3 – it should be returned as ‘BAD’
-- If student mark is 4,5,6 – it should be returned as ‘AVERAGE’
-- If student mark is 7,8 – it should be returned as ‘GOOD’
-- If student mark is 9,10 – it should be returned as ‘EXCELLENT’
SELECT student.id,
       name,
       surname,
       coalesce(max(er.mark), 0) AS best_mark,
       CASE
           WHEN max(er.mark) BETWEEN 1 AND 3 THEN 'BAD'
           WHEN max(er.mark) BETWEEN 4 AND 6 THEN 'AVERAGE'
           WHEN max(er.mark) BETWEEN 7 AND 8 THEN 'GOOD'
           WHEN max(er.mark) >= 9 THEN 'EXCELLENT'
           ELSE 'NOT PASSED'
           END                   AS mark_description
FROM student
         LEFT JOIN exam_result er on student.id = er.student_id
GROUP BY student.id
ORDER BY best_mark;

-- Task 13 - Select number of all marks for each mark type (‘BAD’, ‘AVERAGE’,…)
SELECT count(mark),
       CASE
           WHEN mark BETWEEN 1 AND 3 THEN 'BAD'
           WHEN mark BETWEEN 4 AND 6 THEN 'AVERAGE'
           WHEN mark BETWEEN 7 AND 8 THEN 'GOOD'
           ELSE 'EXCELLENT'
           END                   AS mark_description
FROM exam_result
GROUP BY mark_description
