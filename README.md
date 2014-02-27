GemFireXD-PerformanceTest
=========================

Run as follows

1. Create a local repo for GemFireXD client jar as shown below. 

> mvn install:install-file -Dfile=/Users/papicella/gfxd/PRTS-1.0.0-14/Pivotal_GemFireXD_10_b45971/lib/gemfirexd-client.jar -DgroupId=com.gopivotal.gemfirexd -DartifactId=gemfirexd -Dversion=1.0.14 -Dpackaging=jar

2. Edit src/main/resources/gemfirexdtest.properties to include what the insert / query tests will look like

3. package as shown below

> mvn package

4. Create table as follows

> create table person
(id int primary key,
 name varchar(40))
PARTITION BY COLUMN (id)
REDUNDANCY 1; 

5. Run as follows 

> java -jar target/gemfirexd-performance.jar

GemFireXD PerformanceTest created by Pas Apicella papicella@gopivotal.com
