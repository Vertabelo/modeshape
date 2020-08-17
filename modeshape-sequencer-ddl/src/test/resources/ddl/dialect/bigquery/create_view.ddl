CREATE TABLE vertabelo-bigquery.bigquery_test.person
(
    id INT64 NOT NULL
);

CREATE VIEW vertabelo-bigquery.bigquery_test.view_1
AS SELECT * FROM vertabelo-bigquery.bigquery_test.person;

CREATE OR REPLACE VIEW vertabelo-bigquery.bigquery_test.view_2
AS SELECT * FROM vertabelo-bigquery.bigquery_test.person;

CREATE VIEW IF NOT EXISTS vertabelo-bigquery.bigquery_test.view_3
AS SELECT * FROM vertabelo-bigquery.bigquery_test.person;

CREATE MATERIALIZED VIEW vertabelo-bigquery.bigquery_test.view_4
AS SELECT * FROM vertabelo-bigquery.bigquery_test.person
GROUP BY 1;

CREATE MATERIALIZED VIEW IF NOT EXISTS vertabelo-bigquery.bigquery_test.view_5
AS SELECT * FROM vertabelo-bigquery.bigquery_test.person
GROUP BY 1;
