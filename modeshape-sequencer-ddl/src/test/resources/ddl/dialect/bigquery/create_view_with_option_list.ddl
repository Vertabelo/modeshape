CREATE TABLE vertabelo-bigquery.bigquery_test.person
(
    id INT64 NOT NULL
);

CREATE VIEW vertabelo-bigquery.bigquery_test.view_1
            OPTIONS (
                expiration_timestamp =TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR),
                friendly_name ="new_view",
                description ="a view that expires in 2 days",
                labels =[("org_unit", "development_view")]
                )
AS
SELECT *
FROM vertabelo-bigquery.bigquery_test.person;

CREATE MATERIALIZED VIEW vertabelo-bigquery.bigquery_test.view_2
            OPTIONS (
                expiration_timestamp =TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR),
                friendly_name ="new_materialized_view",
                description ="a materialized view that expires in 2 days",
                labels =[("org_unit", "development_m_view")],
                enable_refresh = true,
                refresh_interval_minutes =60
                )
AS
SELECT *
FROM vertabelo-bigquery.bigquery_test.person
GROUP BY id;
