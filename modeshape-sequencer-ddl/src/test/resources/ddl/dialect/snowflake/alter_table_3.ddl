CREATE OR REPLACE TABLE t1(a1 number);
-- Add a new column to table T1
ALTER TABLE t1 ADD COLUMN a2 number;
-- Add another column with NOT NULL constraint
ALTER TABLE t1 ADD COLUMN a3 number NOT NULL;
-- Add another column with a default value and a NOT NULL constraint
ALTER TABLE t1 ADD COLUMN a4 number DEFAULT 0 NOT NULL;
-- Rename a column in table T1
ALTER TABLE t1 RENAME COLUMN a1 TO b1;
-- Drop a column from table T1
ALTER TABLE t1 DROP COLUMN a2;