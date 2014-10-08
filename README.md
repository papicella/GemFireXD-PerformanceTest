<h2>GemFireXD PerformanceTest</h2>

Run as follows

1. Clone as shown below.

```
git clone https://github.com/papicella/GemFireXD-PerformanceTest.git
```

2. Move into the GemFireXD-PerformanceTest folder / directory

3. Using gfxd CLI create a table for the demo as follows

```
create table person
(id int primary key,
 name varchar(40))
PARTITION BY COLUMN (id)
```

4. Edit src/main/resources/datasource.properties to ensure you have the correct connect details for your cluster

```
portNumber=1527
serverName=localhost
user=app
password=app
```

<h2>INSERT Demo<h2>

1. Edit src/main/resources/gemfirexdtest.properties to set your INSERT demo parameters as shown below.

```
records=100000
commit_point=10000
number_of_queries_per_thread=100
start_of_record_range=1
end_of_record_range=100000
nThreads=50
testType=insert
```

2. Package as follows

```
$ mvn package
```

3. Run as follows

```
$ java -jar target/gemfirexd-performance.jar
```

4. verify 100,000 person records created

```
select count(*) from person;

100000

1 row selected
```

<h2>Query/Update Demo</h2>

1. Edit src/main/resources/gemfirexdtest.properties to set your QUERY demo parameters as shown below

```
records=100000
commit_point=10000
number_of_queries_per_thread=100
start_of_record_range=1
end_of_record_range=100000
nThreads=50
testType=query
```

2. Package as follows

```
$ mvn package
```

3. Run as follows

```
$ java -jar target/gemfirexd-performance.jar
```

GemFireXD PerformanceTest created by Pas Apicella papicella@pivotal.io