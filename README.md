# edgar-analytics
My solution to the Insight Data Engineering Coding Competition https://github.com/InsightDataScience/edgar-analytics (*accepted*).

## Executing the project
The solution is written in Java and the source files are compiled using the `javac` compiler from the package `openjdk-9-jdk-headless`.
The script `run.sh` compiles and executes the project; the two input data files are `input/log.csv` and `input/inactivity_period.txt`, and the result is written into `output/sessionization.txt`. Running `insight_testsuite/run_tests.sh` evaluates the solution using the tests from `insight_testsuite/tests/`.

***Note: to execute `run_tests.sh`, change you current_working_directory to `insight_testsuite/` first, and then call `./run_tests.sh`.***


## Algorithm overview
The algorithm successively reads data entries from the input file `log.csv` line by line, parsing each line after it is read, adding the new data to the data structures that allow efficient computation of the required statistics, and generating the corresponding output line. The memory requirement of the algorithm is **O(N)**. In the worst case **N** is the total number of users with active sessions so far, and all of them will need to be stored until their respective sessions end.

The data structures used are the `HashMap` that maps user's `ip` to the active session record that keepd the statistics, and two `TreeMap` instances that map the unique id of user's request to `ip`. The unique id is a tuple of `Calendar timestamp` and `Integer entry_num`, with the natural ordering by `timestamp` first, and then by `entry_num`. The two treemaps serve as priority queues that are being polled for the next user's ip whose session is to be deemed expired. The first is used when a session expires before the input file ends, so `timestamp` is the date and time of the last user activity during the session. The second is used when all remaining sessions expire because the input file ends, and `timestamp` corresponds to first user activity during the session.

The operations with `HashMap` take amortized **O(1)** time, and the **O(log(N))** time is required for `TreeMap` for its methods `put()`, `remove()` and `firstEntry()`. Overall, the algorithm's complexity is **O(N log(N))**, where **N** can be as large as the size of the input.


## Assumptions
As for the main algorithm body, I assumed that input will not be extremely large so that all the processed data may be stored in the RAM.
The rest of them are concerning the validity of different entries:

1. integer `inactivity_period` is valid if it is positive and less than or equal to 86400;
2. valid date format is `yyyy-MM-dd`, and valid time format is `HH:mm:ss`;
3. `ip` is valid if it is a non-empty string;
4. each webpage request increases the number of documents viewed, even if same webpage is accessed several times;
5. user's session expires if the time elapsed after the last user activity is strictly > than `inactivity_period`, or if there are no more entries in the input file


## Project dependencies
My implementation only imports classes from standard Java packages such as `java.util` and `java.io`.
