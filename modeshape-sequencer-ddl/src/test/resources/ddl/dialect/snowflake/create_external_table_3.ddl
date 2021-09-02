CREATE EXTERNAL TABLE exttable_part(
 date_part date as (value:"b"."c"::varchar) constraint constr,
 timestamp bigint as (value:c1::varchar),
 col2 varchar constraint lalala)
 PARTITION BY (date_part)
 LOCATION=@exttable_part_stage/logs/
 AUTO_REFRESH = true
 FILE_FORMAT = (TYPE = PARQUET);