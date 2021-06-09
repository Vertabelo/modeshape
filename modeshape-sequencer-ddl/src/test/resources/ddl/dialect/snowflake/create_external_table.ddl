CREATE OR REPLACE EXTERNAL TABLE sales_table
WITH LOCATION = @namespace.mystage/daily/
  AUTO_REFRESH = true
  FILE_FORMAT = (TYPE = PARQUET)
  PATTERN='.*sales.*[.]parquet';