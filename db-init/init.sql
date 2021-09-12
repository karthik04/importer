-- DB Startup script

CREATE DATABASE IF NOT EXISTS dev;
USE dev;
CREATE TABLE  IF NOT EXISTS Employee (
    EmployeeId varchar(255),
    FirstName varchar(255),
    LastName varchar(255),
    Email varchar(255),
    Location varchar(255),
    PRIMARY KEY (EmployeeId),
    UNIQUE (Email)
);

--USE mysql;
--CREATE USER 'root'@'%' IDENTIFIED BY 'root';
--GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
--FLUSH PRIVILEGES;
