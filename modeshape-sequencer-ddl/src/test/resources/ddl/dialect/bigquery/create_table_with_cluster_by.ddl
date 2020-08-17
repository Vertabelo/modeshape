CREATE TABLE vertabelo-bigquery.bigquery_test.person
(
    id   INT64  NOT NULL,
    name STRING NOT NULL,
    age  int64
)
    CLUSTER BY id, name, age;

CREATE TABLE vertabelo-bigquery.bigquery_test.employee
    CLUSTER BY
         id,
         name AS
SELECT *
FROM vertabelo-bigquery.bigquery_test.person;
