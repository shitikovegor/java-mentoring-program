Results of tests:

Task 1 - Factorial calculation with ForkJoinPool (time in milliseconds):

| Number    | Sequential | ForJoinPool | Sequential stream | Parallel stream |
|-----------|------------|-------------|-------------------|-----------------|
| 10        | 0          | 6           | 0                 | 5               |
| 100       | 0          | 7           | 0                 | 2               |
| 1000      | 3          | 15          | 1                 | 2               |
| 10 000    | 37         | 38          | 36                | 5               |
| 100 000   | 529        | 308         | 6978              | 113             |
| 1 000 000 | 5306       | 4255        | -                 | 8532            |

If number is up to 1000, sequential factorial works faster, on order of 10 000 all algorithms work about the same, but if the number for calculation is more than 100 000, parallel calculation is the best solution. ForkJoinPool works faster than other with very big numbers (1 000 000 and more).

Task 2 - Merge sorting with ForkJoinPool (time in milliseconds):

| Amount of elements | Sequential | ForkJoinPool |
|--------------------|------------|--------------|
| 100                | 0          | 9            |
| 1000               | 1          | 9            |
| 10 000             | 1          | 18           |
| 100 000            | 17         | 66           |
| 1 000 000          | 223        | 343          |
| 10 000 000         | 1567       | 800          |

The same trends are observed in sorting, ForkJoinPool realization works faster than sequential solution only on arrays with amount of elements more than 10 000 000.

Task 6 - Sum of double squares on array with 500_000_000 numbers

On array with 500mil numbers solution with Recursive Action works much faster:\
Time of parallel calculation - 179 ms, of linear calculation - 643 ms\
On 100mil parallel calculation is faster too:\
Time of parallel calculation - 72 ms, of linear calculation - 116 ms.\
But on 20mil of numbers and less linear calculation is faster than parallel:\
Time of parallel calculation - 56 ms, of linear calculation - 28 ms

