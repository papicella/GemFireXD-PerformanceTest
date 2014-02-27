GemFireXD-PerformanceTest
=========================

Run as follows

1. Create a local repo for GemFireXD client jar as shown below. 

> mvn install:install-file -Dfile=/Users/papicella/gfxd/PRTS-1.0.0-14/Pivotal_GemFireXD_10_b45971/lib/gemfirexd-client.jar -DgroupId=com.gopivotal.gemfirexd -DartifactId=gemfirexd -Dversion=1.0.14 -Dpackaging=jar

2. Edit src/main/resources/gemfirexdtest.properties to include what the insert / query tests will look like

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

3. Edit src/main/resources/datasource.properties to include correct connect details to locator
 
portNumber=1527
serverName=192.168.1.3
user=app
password=app

4. package as shown below

> mvn package

5. Create table as follows

> create table person
(id int primary key,
 name varchar(40))
PARTITION BY COLUMN (id)
REDUNDANCY 1; 

6. Run as follows 

> java -jar target/gemfirexd-performance.jar

GemFireXD PerformanceTest created by Pas Apicella papicella@gopivotal.com
