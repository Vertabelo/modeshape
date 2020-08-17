CREATE TABLE `vertabelo-bigquery.bigquery_test.person_0`
(
    id   INT64 NOT NULL,
    name ARRAY <STRUCT <part STRING, pos INT64>>,
    age  INT64
);

CREATE OR REPLACE TABLE `vertabelo-bigquery.bigquery_test.person_1`
(
    id INT64 NOT NULL
);

CREATE TEMPORARY TABLE `temp_table_2`
(
    id INT64 NOT NULL
)
    OPTIONS (
        expiration_timestamp =TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 DAY)
        );

CREATE TEMP TABLE `temp_table_3`
(
    id INT64 NOT NULL
);


CREATE OR REPLACE TEMPORARY TABLE `temp_table_4`
(
    id INT64 NOT NULL
);

CREATE TABLE IF NOT EXISTS `vertabelo-bigquery.bigquery_test.person_5`
(
    id INT64 NOT NULL
);

CREATE TEMPORARY TABLE IF NOT EXISTS `temp_table_6`
(
    id INT64 NOT NULL
);
