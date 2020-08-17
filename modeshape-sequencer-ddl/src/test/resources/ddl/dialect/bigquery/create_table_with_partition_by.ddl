CREATE TABLE vertabelo-bigquery.bigquery_test.person
(
    id       INT64 NOT NULL,
    birthday TIMESTAMP
)
    PARTITION BY DATE(_PARTITIONTIME);

CREATE TABLE vertabelo-bigquery.bigquery_test.person2
(
    id       INT64 NOT NULL,
    birthday TIMESTAMP
)
    PARTITION BY _PARTITIONDATE;


CREATE TABLE vertabelo-bigquery.bigquery_test.person3
(
    id       INT64 NOT NULL,
    birthday TIMESTAMP
)
    PARTITION BY DATE(birthday);


CREATE TABLE vertabelo-bigquery.bigquery_test.person4
    PARTITION BY
    RANGE_BUCKET(customer_id, GENERATE_ARRAY(0, 100, 10))
 AS
SELECT 1 AS customer_id, DATE "2019-10-01" AS date1;
