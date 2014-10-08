<h2>GemFireXD PerformanceTest</h2>

Run as follows

- Clone as shown below.

```
git clone https://github.com/papicella/GemFireXD-PerformanceTest.git
```

- Move into the GemFireXD-PerformanceTest folder / directory

- Using gfxd CLI create a table for the demo as follows

```
create table person
(id int primary key,
 name varchar(40))
PARTITION BY COLUMN (id)
```

- Edit src/main/resources/datasource.properties to ensure you have the correct connect details for your cluster

```
portNumber=1527
serverName=localhost
user=app
password=app
```

<h2>INSERT Demo</h2>

- Edit src/main/resources/gemfirexdtest.properties to set your INSERT demo parameters as shown below.

```
records=100000
commit_point=10000
number_of_queries_per_thread=100
start_of_record_range=1
end_of_record_range=100000
nThreads=50
testType=insert
```

- Package as follows

```
$ mvn package
```

- Run as follows

```
$ java -jar target/gemfirexd-performance.jar
```

- verify 100,000 person records created by getting a count of all records

```
select count(*) from person;
100000
1 row selected
```

<h2>Query/Update Demo</h2>

- Edit src/main/resources/gemfirexdtest.properties to set your QUERY demo parameters as shown below

```
records=100000
commit_point=10000
number_of_queries_per_thread=100
start_of_record_range=1
end_of_record_range=100000
nThreads=50
testType=query
```

- Package as follows

```
$ mvn package
```

- Run as follows

```
$ java -jar target/gemfirexd-performance.jar
```

- Verify output as follows which includes queries/updates to different records

```
.....
Query with id 33372 took | 1 | milliseconds
Update with id 22106 took | 2 | milliseconds
Query with id 25178 took | 1 | milliseconds
Update with id 52176 took | 2 | milliseconds
Query with id 18122 took | 1 | milliseconds
Query with id 40371 took | 1 | milliseconds
Update with id 59235 took | 1 | milliseconds
Query with id 59780 took | 1 | milliseconds
Query with id 63849 took | 1 | milliseconds
Query with id 49228 took | 1 | milliseconds
Update with id 87876 took | 2 | milliseconds
Elapsed time in seconds 1.448000
```

GemFireXD PerformanceTest created by Pas Apicella papicella@pivotal.io