# edgar-analytics
My solution to the Insight Data Engineering Coding Competition (https://github.com/InsightDataScience/edgar-analytics).

## Executing the project
The solution is written in Java and the source files are compiled using the `javac` compiler from the package `openjdk-9-jdk-headless`.
The script `run.sh` compiles and executes the project; the two input data files are `input/log.csv` and `input/inactivity_period.txt`, and the result is written into `output/sessionization.txt`. Running `insight_testsuite/run_tests.sh` evaluates the solution using the tests from `insight_testsuite/tests/`.

***Note: to execute `run_tests.sh`, change you current_working_directory to `insight_testsuite/` first, and then call `./run_tests.sh`.***


## Algorithm overview
The algorithm successively reads data entries from the input file `log.csv.txt` line by line, parsing each line after it is read, adding the new data to the data structures that allow efficient computation of the required statistics, and generating the corresponding output line. The memory requirement of the algorithm is **O(N)**. In the worst case **N** is the total number of entries scanned so far, and all of them will need to be stored until the input file ends.

The data structure used ???


## Assumptions
As for the main algorithm body, I assumed that input will not be extremely large so that all the processed data may be stored in the RAM.
The rest of them are concerning the validity of different entries:

1. ???

## Project dependencies
My implementation only imports classes from standard Java packages such as `java.util` and `java.io`.
