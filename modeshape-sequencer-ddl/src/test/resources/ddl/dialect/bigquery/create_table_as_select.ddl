CREATE TABLE vertabelo-bigquery.bigquery_test.person
(
    id INT64 NOT NULL
);

CREATE TABLE `vertabelo-bigquery.bigquery_test.clustered`
(
    timestamp          TIMESTAMP,
    customer_id        STRING,
    transaction_amount NUMERIC
)
    PARTITION BY DATE (timestamp)
    CLUSTER BY
        customer_id
    OPTIONS (
        partition_expiration_days =3,
        description ="a table clustered by customer_id"
        )
AS
SELECT *
FROM vertabelo-bigquery.bigquery_test.person;

