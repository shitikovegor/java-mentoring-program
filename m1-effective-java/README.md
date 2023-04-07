Benchmark tests show the following info:
Iterative search is a bit faster than recursive search, especially on arrays with 1000 items and more.
What about sorting - insertion sort on array with 10 000 items is a lot faster than merge sort (average time per operation for merge sort - 0.631 ms/op, insertion sort - 0.007)


| Benchmark                            | size  | Mode  | Cnt | Score        | Error      | Units  |
| ------------------------------------ | ----- | ----- | --- | ------------ | ---------- | ------ |
| BinarySearchTest.testIterativeSearch | 10    | thrpt | 5   | 146154.115 ± | 139182.857 | ops/ms |
| BinarySearchTest.testIterativeSearch | 100   | thrpt | 5   | 51211.635 ±  | 9480.625   | ops/ms |
| BinarySearchTest.testIterativeSearch | 1000  | thrpt | 5   | 48513.542 ±  | 9220.247   | ops/ms |
| BinarySearchTest.testIterativeSearch | 10000 | thrpt | 5   | 31080.995 ±  | 5205.791   | ops/ms |
| BinarySearchTest.testRecursiveSearch | 10    | thrpt | 5   | 143397.487 ± | 68519.125  | ops/ms |
| BinarySearchTest.testRecursiveSearch | 100   | thrpt | 5   | 60613.956 ±  | 21042.455  | ops/ms |
| BinarySearchTest.testRecursiveSearch | 1000  | thrpt | 5   | 24666.902 ±  | 7945.396   | ops/ms |
| BinarySearchTest.testRecursiveSearch | 10000 | thrpt | 5   | 24943.567 ±  | 5759.848   | ops/ms |
| SortBenchmarkTest.testInsertionSort  | 10    | thrpt | 5   | 100401.968 ± | 19242.251  | ops/ms |
| SortBenchmarkTest.testInsertionSort  | 100   | thrpt | 5   | 17337.969 ±  | 3017.910   | ops/ms |
| SortBenchmarkTest.testInsertionSort  | 1000  | thrpt | 5   | 1639.092 ±   | 613.517    | ops/ms |
| SortBenchmarkTest.testInsertionSort  | 10000 | thrpt | 5   | 176.782 ±    | 54.024     | ops/ms |
| SortBenchmarkTest.testMergeSort      | 10    | thrpt | 5   | 2827.035 ±   | 903.913    | ops/ms |
| SortBenchmarkTest.testMergeSort      | 100   | thrpt | 5   | 231.425 ±    | 69.127     | ops/ms |
| SortBenchmarkTest.testMergeSort      | 1000  | thrpt | 5   | 23.127 ±     | 0.888      | ops/ms |
| SortBenchmarkTest.testMergeSort      | 10000 | thrpt | 5   | 2.032 ±      | 0.272      | ops/ms |
| BinarySearchTest.testIterativeSearch | 10    | avgt  | 5   | ≈ 10⁻⁵       |            | ms/op  |
| BinarySearchTest.testIterativeSearch | 100   | avgt  | 5   | ≈ 10⁻⁵       |            | ms/op  |
| BinarySearchTest.testIterativeSearch | 1000  | avgt  | 5   | ≈ 10⁻⁵       |            | ms/op  |
| BinarySearchTest.testIterativeSearch | 10000 | avgt  | 5   | ≈ 10⁻⁴       |            | ms/op  |
| BinarySearchTest.testRecursiveSearch | 10    | avgt  | 5   | ≈ 10⁻⁵       |            | ms/op  |
| BinarySearchTest.testRecursiveSearch | 100   | avgt  | 5   | ≈ 10⁻⁵       |            | ms/op  |
| BinarySearchTest.testRecursiveSearch | 1000  | avgt  | 5   | ≈ 10⁻⁵       |            | ms/op  |
| BinarySearchTest.testRecursiveSearch | 10000 | avgt  | 5   | ≈ 10⁻⁴       |            | ms/op  |
| SortBenchmarkTest.testInsertionSort  | 10    | avgt  | 5   | ≈ 10⁻⁵       |            | ms/op  |
| SortBenchmarkTest.testInsertionSort  | 100   | avgt  | 5   | ≈ 10⁻⁴       |            | ms/op  |
| SortBenchmarkTest.testInsertionSort  | 1000  | avgt  | 5   | 0.001 ±      | 0.001      | ms/op  |
| SortBenchmarkTest.testInsertionSort  | 10000 | avgt  | 5   | 0.007 ±      | 0.002      | ms/op  |
| SortBenchmarkTest.testMergeSort      | 10    | avgt  | 5   | ≈ 10⁻³       |            | ms/op  |
| SortBenchmarkTest.testMergeSort      | 100   | avgt  | 5   | 0.006 ±      | 0.003      | ms/op  |
| SortBenchmarkTest.testMergeSort      | 1000  | avgt  | 5   | 0.059 ±      | 0.005      | ms/op  |
| SortBenchmarkTest.testMergeSort      | 10000 | avgt  | 5   | 0.631 ±      | 0.452      | ms/op  |
