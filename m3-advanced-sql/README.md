-- Task 9 - Select students who does not pass any exam using each the following operator: – 0.5 points.\
-- Outer join\
-- Subquery with ‘not in’ clause\
-- Subquery with ‘any ‘ clause\
-- Check which approach is faster for 1000, 10K, 100K exams and 10, 1K, 100K students

For 1000 exams and 10 students\
-- Outer join\
![img.png](perf-results/img.png)
-- Subquery with ‘not in’ clause\
![img_1.png](perf-results/img_1.png)
-- Subquery with ‘any ‘ clause\
![img_2.png](perf-results/img_2.png)

For 10K exams and 1K students\
-- Outer join\
![img_3.png](perf-results/img_3.png)
-- Subquery with ‘not in’ clause\
![img_4.png](perf-results/img_4.png)
-- Subquery with ‘any ‘ clause\
![img_5.png](perf-results/img_5.png)

For 100K exams and 100K students\
-- Outer join\
![img_6.png](perf-results/img_6.png)
-- Subquery with ‘not in’ clause\
![img_7.png](perf-results/img_7.png)
-- Subquery with ‘any ‘ clause\
![img_8.png](perf-results/img_8.png)
