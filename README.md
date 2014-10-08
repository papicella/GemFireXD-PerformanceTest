<h2>GemFireXD PerformanceTest</h2>

Run as follows

1. Clone as shown below.

```
git clone https://github.com/papicella/GemFireXD-PerformanceTest.git
```

2. Move into the GemFireXD-PerformanceTest folder / directory

3. Using gfxd CLI create a table for the demo as follows

```
[Wed Oct 08 21:07:31 papicella@:~/gfxd/Pivotal_GemFireXD_130_b48613_Linux/pasdemos/gfxdweb ] $ gfxd
gfxd version 10.4
gfxd> connect client 'localhost:1527';
gfxd> create table person
> (id int primary key,
>  name varchar(40))
> PARTITION BY COLUMN (id)
> REDUNDANCY 1;
0 rows inserted/updated/deleted
gfxd>
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
# INSERT TEST PROPERTIES

# insert records to create
records=100000
#at what point do we call conn.commit() for each threads;
commit_point=10000

# QUERY TEST PROPERTIES

# how many queries will each thread perform
number_of_queries_per_thread=100

# record range as per insert test , required to ensure we query/update an existing record ID
start_of_record_range=1
end_of_record_range=100000

# PROPERTIES COMMON TO BOTH TESTS

#number of threads for insert or query clients.
nThreads=50

# insert or query test, either : insert or query
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
gfxd> select count(*) from person;
1
-----------
100000

1 row selected
```

<h2>Query/Update Demo<h2>

1. Edit src/main/resources/gemfirexdtest.properties to set your QUERY demo parameters as shown below

```
# INSERT TEST PROPERTIES

# insert records to create
records=100000
#at what point do we call conn.commit() for each threads;
commit_point=10000

# QUERY TEST PROPERTIES

# how many queries will each thread perform
number_of_queries_per_thread=100

# record range as per insert test , required to ensure we query/update an existing record ID
start_of_record_range=1
end_of_record_range=100000

# PROPERTIES COMMON TO BOTH TESTS

#number of threads for insert or query clients.
nThreads=50

# insert or query test, either : insert or query
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