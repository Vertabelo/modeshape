/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.modeshape.sequencer.ddl.dialect.mysql8;

import java.util.Arrays;
import java.util.List;

import org.modeshape.sequencer.ddl.DdlConstants;

/**
 * @author blafond
 * @author Marek Berkan
 */
public interface MySql8DdlConstants extends DdlConstants {

    String[] CUSTOM_KEYWORDS = {

        // source: https://dev.mysql.com/doc/refman/8.0/en/keywords.html
        "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN",
        "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER",
        "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS",
        "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES",
        ", DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", ",DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT",
        "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE",
        "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE",
        "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GRANT", "GROUP",
        "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", ",  HOUR_MINUTE", "HOUR_SECOND", "IF", ",IGNORE", "IN",
        "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4",
        "INT8", "INTEGER INTERVAL", "INTO", "IS", ",ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING LEAVE",
        "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP  LOCK", "LONG",
        "LONGBLOB", ",  LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE",
        ",  MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", ",MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD",
        "MODIFIES", "NATURAL NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", ",  OPTION",
        "OPTIONALLY", "OR", ",ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY PROCEDURE", "PURGE",
        "RANGE", "READ", "READS", "READ_WRITE", ",REAL", "REFERENCES", "REGEXP  RELEASE", "RENAME",
        "REPEAT  REPLACE", "REQUIRE", "RESIGNAL", ",  RESTRICT", "RETURN", "REVOKE  RIGHT", "RLIKE",
        "SCHEMA  SCHEMAS", "SECOND_MICROSECOND", "SELECT  SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL",
        "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", ",SQL_BIG_RESULT",
        "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", ",  SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED",
        "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", ",  TO", "TRAILING", "TRIGGER TRUE", "UNDO", "UNION", "UNIQUE",
        "UNLOCK", "UNSIGNED", ",  UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", ",  UTC_TIMESTAMP",
        "VALUES", "VARBINARY", ", VARCHAR", "VARCHARACTER", "VARYING WHEN", "WHERE", "WHILE", "WITH", "WRITE",
        "XOR", "YEAR_MONTH", "ZEROFILL",
        // added manually:
        "SERIAL"};



    interface MySqlStatementStartPhrases {
        // source https://dev.mysql.com/doc/refman/8.0/en/data-types.html
        String[] STMT_ALTER_DATABASE 		= {"ALTER", "DATABASE"};
        String[] STMT_ALTER_SCHEMA 		= {"ALTER", "SCHEMA"};
        
        String[] STMT_ALTER_DEFINER 		= {"ALTER", "DEFINER"}; // = ALTER EVENT, ALTER VIEW
        String[] STMT_ALTER_EVENT	 		= {"ALTER", "EVENT"};
        String[] STMT_ALTER_LOGFILE_GROUP		= {"ALTER", "LOGFILE", "GROUP"};
        String[] STMT_ALTER_FUNCTION 		= {"ALTER", "FUNCTION"};
        String[] STMT_ALTER_PROCEDURE 		= {"ALTER", "PROCEDURE"};
        String[] STMT_ALTER_SERVER 		= {"ALTER", "SERVER"};
        
        // ALTER [ONLINE | OFFLINE] [IGNORE] TABLE
        String[] STMT_ALTER_IGNORE_TABLE		= {"ALTER", "IGNORE", "TABLE"};
        String[] STMT_ALTER_ONLINE_TABLE		= {"ALTER", "ONLINE", "TABLE"};
        String[] STMT_ALTER_OFFLINE_TABLE		= {"ALTER", "OFFLINE", "TABLE"};
        String[] STMT_ALTER_ONLINE_IGNORE_TABLE	= {"ALTER", "ONLINE", "IGNORE", "TABLE"};
        String[] STMT_ALTER_OFFLINE_IGNORE_TABLE	= {"ALTER", "OFFLINE", "IGNORE", "TABLE"};
        
        String[] STMT_ALTER_TABLESPACE		= {"ALTER", "TABLESPACE"};
        String[] STMT_ALTER_ALGORITHM 		= {"ALTER", "ALGORITHM"};
        String[] STMT_ALTER_SQL_SECURITY 		= {"ALTER", "SQL", "SECURITY"};
        String[] STMT_ALTER_VIEW                   = {"ALTER", "VIEW"};
        
        String[][] ALTER_PHRASES = {
            STMT_ALTER_DATABASE,
            STMT_ALTER_DEFINER,
            STMT_ALTER_EVENT,
            STMT_ALTER_FUNCTION,
            STMT_ALTER_LOGFILE_GROUP,
            STMT_ALTER_PROCEDURE,
            STMT_ALTER_SCHEMA,
            STMT_ALTER_SERVER,
            STMT_ALTER_TABLESPACE,
            STMT_ALTER_IGNORE_TABLE, 
            STMT_ALTER_ONLINE_TABLE,
            STMT_ALTER_OFFLINE_TABLE,
            STMT_ALTER_ONLINE_IGNORE_TABLE,
            STMT_ALTER_OFFLINE_IGNORE_TABLE,
            STMT_ALTER_ALGORITHM,
            STMT_ALTER_SQL_SECURITY,
            STMT_ALTER_VIEW,
        };

        String[] STMT_CREATE_DATABASE 		= {"CREATE", "DATABASE"};
        String[] STMT_CREATE_SCHEMA                = {"CREATE", "SCHEMA"};
        String[] STMT_CREATE_DEFINER 		= {"CREATE", "DEFINER"}; // TRIGGER, FUNCTION, PROCEDURE, EVENT
        String[] STMT_CREATE_EVENT 		= {"CREATE", "EVENT"};

        //CREATE [ONLINE|OFFLINE] [UNIQUE|FULLTEXT|SPATIAL] INDEX
        String[] STMT_CREATE_INDEX 		= {"CREATE", "INDEX"};
        String[] STMT_CREATE_ONLINE_INDEX          = {"CREATE", "ONLINE", "INDEX"};
        String[] STMT_CREATE_OFFLINE_INDEX         = {"CREATE", "OFFLINE", "INDEX"};
        String[] STMT_CREATE_UNIQUE_INDEX 		= {"CREATE", "UNIQUE", "INDEX"};
        String[] STMT_CREATE_FULLTEXT_INDEX        = {"CREATE", "FULLTEXT", "INDEX"};
        String[] STMT_CREATE_SPATIAL_INDEX         = {"CREATE", "SPATIAL", "INDEX"};
        String[] STMT_CREATE_ONLINE_UNIQUE_INDEX   = {"CREATE", "ONLINE", "UNIQUE", "INDEX"};
        String[] STMT_CREATE_ONLINE_FULLTEXT_INDEX = {"CREATE", "ONLINE", "FULLTEXT", "INDEX"};
        String[] STMT_CREATE_ONLINE_SPATIAL_INDEX  = {"CREATE", "ONLINE", "SPATIAL", "INDEX"};
        String[] STMT_CREATE_OFFLINE_UNIQUE_INDEX  = {"CREATE", "OFFLINE", "UNIQUE", "INDEX"};
        String[] STMT_CREATE_OFFLINE_FULLTEXT_INDEX = {"CREATE", "OFFLINE", "FULLTEXT", "INDEX"};
        String[] STMT_CREATE_OFFLINE_SPATIAL_INDEX  = {"CREATE", "OFFLINE", "SPATIAL", "INDEX"};
        
        String[] STMT_CREATE_LOGFILE_GROUP 	= {"CREATE", "LOGFILE", "GROUP"};
        String[] STMT_CREATE_FUNCTION 		= {"CREATE", "FUNCTION"};
        String[] STMT_CREATE_PROCEDURE 		= {"CREATE", "PROCEDURE"};
        String[] STMT_CREATE_SERVER 		= {"CREATE", "SERVER"};
        String[] STMT_CREATE_TABLESPACE 		= {"CREATE", "TABLESPACE"};
        String[] STMT_CREATE_TRIGGER 		= {"CREATE", "TRIGGER"};
     
        String[] STMT_CREATE_TEMPORARY_TABLE               = {"CREATE", "TEMPORARY", "TABLE"};
        
//        CREATE
//        [OR REPLACE]
//        [ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
//        [DEFINER = { user | CURRENT_USER }]
//        [SQL SECURITY { DEFINER | INVOKER }]
//        VIEW view_name [(column_list)
        String[] STMT_CREATE_ALGORITHM              = {"CREATE", "ALGORITHM"};
        String[] STMT_CREATE_SQL_SECURITY           = {"CREATE", "SQL", "SECURITY"};
        String[] STMT_CREATE_VIEW                   = {"CREATE", "VIEW"};
        String[] STMT_CREATE_OR_REPLACE_DEFINER              = {"CREATE", "OR", "REPLACE", "DEFINER"};
        String[] STMT_CREATE_OR_REPLACE_ALGORITHM              = {"CREATE", "OR", "REPLACE", "ALGORITHM"};
        String[] STMT_CREATE_OR_REPLACE_SQL_SECURITY           = {"CREATE", "OR", "REPLACE", "SQL", "SECURITY"};
        String[] STMT_CREATE_OR_REPLACE_VIEW                   = {"CREATE", "OR", "REPLACE", "VIEW"};

        String[][] CREATE_PHRASES = {
            STMT_CREATE_DATABASE,
            STMT_CREATE_SCHEMA,
            
            STMT_CREATE_DEFINER,
            STMT_CREATE_EVENT,
            STMT_CREATE_FUNCTION,
            STMT_CREATE_INDEX,
            STMT_CREATE_ONLINE_INDEX,
            STMT_CREATE_OFFLINE_INDEX,
            STMT_CREATE_UNIQUE_INDEX,
            STMT_CREATE_FULLTEXT_INDEX,
            STMT_CREATE_SPATIAL_INDEX,
            STMT_CREATE_ONLINE_FULLTEXT_INDEX,
            STMT_CREATE_ONLINE_SPATIAL_INDEX,
            STMT_CREATE_ONLINE_UNIQUE_INDEX,
            STMT_CREATE_OFFLINE_UNIQUE_INDEX,
            STMT_CREATE_OFFLINE_FULLTEXT_INDEX,
            STMT_CREATE_OFFLINE_SPATIAL_INDEX,
            
            STMT_CREATE_LOGFILE_GROUP,
            STMT_CREATE_PROCEDURE,
            STMT_CREATE_SERVER,
            STMT_CREATE_TABLESPACE,
            STMT_CREATE_TRIGGER,
            
            STMT_CREATE_ALGORITHM,
            STMT_CREATE_SQL_SECURITY,
            STMT_CREATE_VIEW,
            STMT_CREATE_OR_REPLACE_DEFINER,
            STMT_CREATE_OR_REPLACE_ALGORITHM,
            STMT_CREATE_OR_REPLACE_SQL_SECURITY,
            STMT_CREATE_OR_REPLACE_VIEW,
            
            STMT_CREATE_TEMPORARY_TABLE
        };

        /*
         * TODO:  WORK REQUIRED HERE
         * 
         * NOTE:  The CREATE VIEW syntax for MySQL is convoluted as shown below. Basically you have to check for multiple properties
         * BEFORE the actual "VIEW view_name (...) AS ...." is defined.
         * Will make it difficult to parse cleanly.
         * 
         * THIS ALSO affects ALTER VIEW
         * 
				CREATE
				    [OR REPLACE]
				    [ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
				    [DEFINER = { user | CURRENT_USER }]
				    [SQL SECURITY { DEFINER | INVOKER }]
				    VIEW view_name [(column_list)]
				    AS select_statement
				    [WITH [CASCADED | LOCAL] CHECK OPTION]
         * 
         * 
         */

        String[] STMT_DROP_DATABASE 	= {"DROP", "DATABASE"};
        String[] STMT_DROP_SCHEMA          = {"DROP", "SCHEMA"};
        String[] STMT_DROP_EVENT 		= {"DROP", "EVENT"};
        String[] STMT_DROP_INDEX 		= {"DROP", "INDEX"};
        String[] STMT_DROP_ONLINE_INDEX 	= {"DROP", "ONLINE", "INDEX"};
        String[] STMT_DROP_OFFLINE_INDEX 	= {"DROP", "OFFLINE", "INDEX"};
        String[] STMT_DROP_LOGFILE_GROUP 	= {"DROP", "LOGFILE", "GROUP"};
        String[] STMT_DROP_FUNCTION 	= {"DROP", "FUNCTION"};
        String[] STMT_DROP_PROCEDURE 	= {"DROP", "PROCEDURE"};
        String[] STMT_DROP_SERVER 		= {"DROP", "SERVER"};
        String[] STMT_DROP_TABLESPACE 	= {"DROP", "TABLESPACE"};
        String[] STMT_DROP_TRIGGER 	= {"DROP", "TRIGGER"};
        String[] STMT_DROP_VIEW            = {"DROP", "VIEW"};
        
        String[] STMT_DROP_TABLE_IF_EXISTS            = {"DROP", "TABLE", "IF", "EXISTS"};

        String[][] DROP_PHRASES = {
            STMT_DROP_DATABASE,
            STMT_DROP_SCHEMA,
            STMT_DROP_EVENT,
            STMT_DROP_FUNCTION,
            STMT_DROP_INDEX,
            STMT_DROP_ONLINE_INDEX,
            STMT_DROP_OFFLINE_INDEX,
            STMT_DROP_LOGFILE_GROUP,
            STMT_DROP_PROCEDURE,
            STMT_DROP_SERVER,
            STMT_DROP_TABLESPACE,
            STMT_DROP_TRIGGER,
            STMT_DROP_VIEW,
            STMT_DROP_TABLE_IF_EXISTS
        };
        String[][] SET_PHRASES = {

        };

        // RENAME {DATABASE | SCHEMA} db_name TO new_db_name;
        String[] STMT_RENAME_DATABASE 	= {"RENAME", "DATABASE"};
        String[] STMT_RENAME_SCHEMA 	= {"RENAME", "SCHEMA"};
        // RENAME TABLE tbl_name TO new_tbl_name [, tbl_name2 TO new_tbl_name2] ...
        String[] STMT_RENAME_TABLE 	= {"RENAME", "TABLE"};
        String[] STMT_TRUNCATE         = {"RENAME", "TRUNCATE"};

        String[][] MISC_PHRASES = {
            STMT_RENAME_DATABASE, STMT_RENAME_SCHEMA, STMT_RENAME_TABLE, STMT_TRUNCATE
        };
    }

    String[] DTYPE_NATIONAL_VARCHAR	= {"NATIONAL", "VARCHAR"};// NATIONAL VARCHAR
    String[] DTYPE_NVARCHAR	= {"NVARCHAR"};// NATIONAL VARCHAR

    String[] DTYPE_TINYINT 		= {"TINYINT"}; // TINYINT[(M)] [UNSIGNED] [ZEROFILL]
    String[] DTYPE_MEDIUMINT 		= {"MEDIUMINT"}; // MEDIUMINT[(M)] [UNSIGNED] [ZEROFILL]
    String[] DTYPE_BIGINT 			= {"BIGINT"}; // BIGINT[(M)] [UNSIGNED] [ZEROFILL]
    String[] DTYPE_FIXED 			= {"FIXED"};// FIXED[(M[,D])] [UNSIGNED] [ZEROFILL]
    String[] DTYPE_BOOL 			= {"BOOL"};// BOOL
    String[] DTYPE_BOOLEAN 		= {"BOOLEAN"};// BOOLEAN

    String[] DTYPE_DOUBLE 			= {"DOUBLE"}; // DOUBLE[(M,D)] [UNSIGNED] [ZEROFILL]

    String[] DTYPE_BINARY 			= {"BINARY"};
    String[] DTYPE_VARBINARY 		= {"VARBINARY"};
    String[] DTYPE_BLOB 			= {"BLOB"};
    String[] DTYPE_TINYBLOB 		= {"TINYBLOB"};
    String[] DTYPE_TINYTEXT 		= {"TINYTEXT"};
    String[] DTYPE_TEXT 			= {"TEXT"};
    String[] DTYPE_MEDIUMBLOB 		= {"MEDIUMBLOB"};
    String[] DTYPE_MEDIUMTEXT 		= {"MEDIUMTEXT"};
    String[] DTYPE_LONGBLOB 		= {"LONGBLOB"};
    String[] DTYPE_LONGTEXT 		= {"LONGTEXT"};
    String[] DTYPE_ENUM 			= {"ENUM"};
    String[] DTYPE_SET 			= {"SET"};
    String[] DTYPE_SERIAL			= {"SERIAL"};

    String[] DTYPE_DATETIME 		= {"DATETIME"};
    String[] DTYPE_YEAR 			= {"YEAR"};

    String[] DTYPE_JSON            = {"JSON"};

    // spatial data
    String[] DTYPE_GEOMETRY = {"GEOMETRY"};
    String[] DTYPE_POINT = {"POINT"};
    String[] DTYPE_LINESTRING = {"LINESTRING"};
    String[] DTYPE_POLYGON = {"POLYGON"};
    String[] DTYPE_GEOMETRYCOLLECTION = {"GEOMETRYCOLLECTION"};
    String[] DTYPE_MULTIPOINT = {"MULTIPOINT"};
    String[] DTYPE_MULTILINESTRING = {"MULTILINESTRING"};
    String[] DTYPE_MULTIPOLYGON = {"MULTIPOLYGON"};


    interface MySqlDataTypes {
        List<String[]> CUSTOM_DATATYPE_START_PHRASES =
                Arrays.asList(DTYPE_BIGINT, DTYPE_BOOL, DTYPE_BOOLEAN, DTYPE_FIXED, DTYPE_DOUBLE, DTYPE_MEDIUMINT,
                              DTYPE_TINYINT,
                              DTYPE_NATIONAL_VARCHAR, DTYPE_NVARCHAR,
                              DTYPE_BINARY, DTYPE_VARBINARY, DTYPE_TINYBLOB, DTYPE_TINYTEXT,
                              DTYPE_TEXT,
                              DTYPE_MEDIUMBLOB, DTYPE_MEDIUMTEXT, DTYPE_LONGBLOB, DTYPE_LONGTEXT, DTYPE_ENUM, DTYPE_SET,
                              DTYPE_DATETIME, DTYPE_YEAR, DTYPE_BLOB, DTYPE_JSON, DTYPE_SERIAL,
                              DTYPE_GEOMETRY, DTYPE_POINT, DTYPE_LINESTRING, DTYPE_POLYGON,
                              DTYPE_GEOMETRYCOLLECTION, DTYPE_MULTIPOINT, DTYPE_MULTILINESTRING, DTYPE_MULTIPOLYGON);

        List<String> CUSTOM_DATATYPE_START_WORDS =
                Arrays.asList("TINYINT", "MEDIUMINT", "BIGINT", "FIXED", "BOOL", "BOOLEAN", "BINARY", "VARBINARY",
                              "NVARCHAR",
                              "TINYBLOB",
                              "TINYTEXT", "MEDIUMBLOB", "MEDIUMTEXT", "LONGBLOB", "LONGTEXT", "TEXT", "ENUM", "SET",
                              "DATETIME", "YEAR", "BLOB", "JSON", "SERIAL",
                              "GEOMETRY", "POINT", "LINESTRING", "POLYGON",
                              "GEOMETRYCOLLECTION", "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON");
        /* 
         * ===========================================================================================================================
         * DATATYPES

				data_type:
				    BIT[(length)]
				  | TINYINT[(length)] [UNSIGNED] [ZEROFILL]
				  | SMALLINT[(length)] [UNSIGNED] [ZEROFILL]
				  | MEDIUMINT[(length)] [UNSIGNED] [ZEROFILL]
				  | INT[(length)] [UNSIGNED] [ZEROFILL]
				  | INTEGER[(length)] [UNSIGNED] [ZEROFILL]
				  | BIGINT[(length)] [UNSIGNED] [ZEROFILL]
				  | REAL[(length,decimals)] [UNSIGNED] [ZEROFILL]
				  | DOUBLE[(length,decimals)] [UNSIGNED] [ZEROFILL]
				  | FLOAT[(length,decimals)] [UNSIGNED] [ZEROFILL]
				  | DECIMAL[(length[,decimals])] [UNSIGNED] [ZEROFILL]
				  | NUMERIC[(length[,decimals])] [UNSIGNED] [ZEROFILL]
				  | DATE
				  | TIME[(length)]
				  | TIMESTAMP[(length)]
				  | DATETIME[(length)]
				  | YEAR
				  | CHAR[(length)]
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | VARCHAR(length)
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | BINARY[(length)]
				  | VARBINARY(length)
				  | TINYBLOB
				  | BLOB
				  | MEDIUMBLOB
				  | LONGBLOB
				  | TINYTEXT [BINARY]
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | TEXT [BINARY]
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | MEDIUMTEXT [BINARY]
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | LONGTEXT [BINARY]
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | ENUM(value1,value2,value3,...)
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | SET(value1,value2,value3,...)
				      [CHARACTER SET charset_name] [COLLATE collation_name]
				  | spatial_type


         * 
         */
    }
}
