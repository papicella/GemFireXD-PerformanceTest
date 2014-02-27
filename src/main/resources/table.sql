drop table person;

create table person
(id int primary key,
 name varchar(40))
PARTITION BY COLUMN (id)
REDUNDANCY 1;