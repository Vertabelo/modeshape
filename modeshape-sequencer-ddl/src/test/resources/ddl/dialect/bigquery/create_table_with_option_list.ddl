CREATE TABLE vertabelo-bigquery.bigquery_test.person
(
    id   INT64 NOT NULL,
    name ARRAY <STRUCT <part STRING, pos INT64>>,
    age  INT64
)
    PARTITION BY RANGE_BUCKET(age, GENERATE_ARRAY(1, 120, 2))
    OPTIONS (
        expiration_timestamp =TIMESTAMP "2025-01-01 00:00:00 UTC",
        partition_expiration_days =12,
        require_partition_filter = true,
        kms_key_name ="projects/project_id/key",
        description ="any description",
        labels =[("org_unit", "development")],
        friendly_name ="my_table"
        );

CREATE TABLE vertabelo-bigquery.bigquery_test.person2
(
    id INT64
)
    OPTIONS (
        expiration_timestamp =TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR));
