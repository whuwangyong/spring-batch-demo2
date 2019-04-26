-- DROP TABLE people IF EXISTS;
--
-- CREATE TABLE people  (
--     person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
--     first_name VARCHAR(20),
--     last_name VARCHAR(20)
-- );

DROP TABLE IF EXISTS people;

CREATE TABLE people  (
  person_id int auto_increment NOT NULL PRIMARY KEY,
  first_name varchar (20),
  last_name varchar (20)
);
