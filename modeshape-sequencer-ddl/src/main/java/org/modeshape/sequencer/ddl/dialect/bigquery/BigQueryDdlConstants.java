package org.modeshape.sequencer.ddl.dialect.bigquery;

import java.util.Arrays;
import java.util.List;

import static org.modeshape.sequencer.ddl.DdlConstants.CREATE;
import static org.modeshape.sequencer.ddl.DdlConstants.TABLE;
import static org.modeshape.sequencer.ddl.DdlConstants.VIEW;

public interface BigQueryDdlConstants {

    interface BigQueryStatementStartPhrases {
        String[] CUSTOM_KEYWORDS = {
                "ALL", "AND", "ANY", "ARRAY", "AS", "ASC", "ASSERT_ROWS_MODIFIED", "AT", "BETWEEN", "BY", "CASE",
                "CAST", "COLLATE", "CONTAINS", "CREATE", "CROSS", "CUBE", "CURRENT", "DEFAULT", "DEFINE", "DESC",
                "DISTINCT", "ELSE", "END", "ENUM", "ESCAPE", "EXCEPT", "EXCLUDE", "EXISTS", "EXTRACT", "FALSE", "FETCH",
                "FOLLOWING", "FOR", "FROM", "FULL", "GROUP", "GROUPING", "GROUPS", "HASH", "HAVING", "IF", "IGNORE",
                "IN", "INNER", "INTERSECT", "INTERVAL", "INTO", "IS", "JOIN", "LATERAL", "LEFT", "LIKE", "LIMIT",
                "LOOKUP", "MERGE", "NATURAL", "NEW", "NO", "NOT", "NULL", "NULLS", "OF", "ON", "OR", "ORDER", "OUTER",
                "OVER", "PARTITION", "PRECEDING", "PROTO", "RANGE", "RECURSIVE", "RESPECT", "RIGHT", "ROLLUP", "ROWS",
                "SELECT", "SET", "SOME", "STRUCT", "TABLESAMPLE", "THEN", "TO", "TREAT", "TRUE", "UNBOUNDED", "UNION",
                "UNNEST", "USING", "WHEN", "WHERE", "WINDOW", "WITH", "WITHIN"
        };

        String[] STMT_CREATE_MATERIALIZED_VIEW = {CREATE, "MATERIALIZED", VIEW};

        String[] STMT_CREATE_OR_REPLACE_TABLE = {CREATE, "OR", "REPLACE", TABLE};
        String[] STMT_CREATE_TEMPORARY_TABLE = {CREATE, "TEMPORARY", TABLE};
        String[] STMT_CREATE_OR_REPLACE_TEMPORARY_TABLE = {CREATE, "OR", "REPLACE", "TEMPORARY", TABLE};
        //  TEMP shortcut
        String[] STMT_CREATE_TEMP_TABLE = {CREATE, "TEMP", TABLE};
        String[] STMT_CREATE_OR_REPLACE_TEMP_TABLE = {CREATE, "OR", "REPLACE", "TEMP", TABLE};

        String[][] CREATE_PHRASES = {STMT_CREATE_MATERIALIZED_VIEW,
                                     STMT_CREATE_OR_REPLACE_TABLE,
                                     STMT_CREATE_TEMPORARY_TABLE,
                                     STMT_CREATE_OR_REPLACE_TEMPORARY_TABLE,
                                     STMT_CREATE_TEMP_TABLE,
                                     STMT_CREATE_OR_REPLACE_TEMP_TABLE};
    }

    interface BigQueryDataTypes {
        String[] DTYPE_INT64 = {"INT64"};
        String[] DTYPE_NUMERIC = {"NUMERIC"};
        String[] DTYPE_FLOAT64 = {"FLOAT64"};
        String[] DTYPE_STRING = {"STRING"};
        String[] DTYPE_DATE = {"DATE"};
        String[] DTYPE_TIME = {"TIME"};
        String[] DTYPE_DATETIME = {"DATETIME"};
        String[] DTYPE_TIMESTAMP = {"TIMESTAMP"};
        String[] DTYPE_BYTES = {"BYTES"};
        String[] DTYPE_BOOL = {"BOOL"};
        String[] DTYPE_ARRAY = {"ARRAY"};
        String[] DTYPE_STRUCT = {"STRUCT"};
        String[] DTYPE_GEOGRAPHY = {"GEOGRAPHY"};

        List<String[]> CUSTOM_DATATYPE_START_PHRASES = Arrays.asList(DTYPE_INT64,
                                                                     DTYPE_NUMERIC,
                                                                     DTYPE_FLOAT64,
                                                                     DTYPE_STRING,
                                                                     DTYPE_DATE,
                                                                     DTYPE_TIME,
                                                                     DTYPE_DATETIME,
                                                                     DTYPE_TIMESTAMP,
                                                                     DTYPE_BYTES,
                                                                     DTYPE_BOOL,
                                                                     DTYPE_ARRAY,
                                                                     DTYPE_STRUCT,
                                                                     DTYPE_GEOGRAPHY);
        List<String> CUSTOM_DATATYPE_START_WORDS = Arrays.asList(DTYPE_INT64[0],
                                                                 DTYPE_NUMERIC[0],
                                                                 DTYPE_FLOAT64[0],
                                                                 DTYPE_STRING[0],
                                                                 DTYPE_DATE[0],
                                                                 DTYPE_TIME[0],
                                                                 DTYPE_DATETIME[0],
                                                                 DTYPE_TIMESTAMP[0],
                                                                 DTYPE_BYTES[0],
                                                                 DTYPE_BOOL[0],
                                                                 DTYPE_ARRAY[0],
                                                                 DTYPE_STRUCT[0],
                                                                 DTYPE_GEOGRAPHY[0]);
    }
}
