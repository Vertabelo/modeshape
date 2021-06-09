CREATE EXTERNAL TABLE exttable_part(
 date_part date,
 timestamp bigint,
 col2 varchar)
 PARTITION BY (date_part)
 LOCATION=@exttable_part_stage/logs/
 AUTO_REFRESH = true
 FILE_FORMAT = (TYPE = PARQUET);