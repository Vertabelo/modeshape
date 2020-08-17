CREATE TABLE vertabelo-bigquery.bigquery_test.personx
(
    id   INT64 NOT NULL OPTIONS (description ="Person id"),
    name ARRAY <STRUCT <part STRING, pos INT64>>,
    age  INT64 OPTIONS (description ="An optional INT64 field")
);
