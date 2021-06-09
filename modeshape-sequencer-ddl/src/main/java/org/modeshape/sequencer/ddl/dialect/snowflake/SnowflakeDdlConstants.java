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
package org.modeshape.sequencer.ddl.dialect.snowflake;

import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public interface SnowflakeDdlConstants extends DdlConstants {
	// https://docs.snowflake.com/en/sql-reference/reserved-keywords.html
	public static final String[] CUSTOM_KEYWORDS = {
		"ACCOUNT",
		"CASE",
		"CAST",
		"CONNECTION",
		"CONSTRAINT",
		"CROSS",
		"CURRENT_DATE",
		"DATABASE",
		"FALSE",
		"FULL",
		"GSCLUSTER",
		"ILIKE",
		"INCREMENT",
		"INNER",
		"ISSUE",
		"JOIN",
		"LATERAL",
		"LEFT",
		"LOCALTIME",
		"MINUS",
		"NATURAL",
		"ORGANIZATION",
		"QUALIFY",
		"REGEXP",
		"RIGHT",
		"RLIKE",
		"SCHEMA",
		"SOME",
		"TRUE",
		"TRY_CAST",
		"USING",
		"VIEW",
		"WHEN"
	};
	
	public static final String[] IF_NOT_EXISTS = {"IF", "NOT", "EXISTS"};

	interface SnowflakeStatementStartPhrases {
		static final String[] STMT_ALTER_AGGREGATE = {ALTER, "AGGREGATE"};
		static final String[] STMT_ALTER_CONVERSION = {ALTER, "CONVERSION"};
		static final String[] STMT_ALTER_DATABASE = {ALTER, "DATABASE"};
		static final String[] STMT_ALTER_FOREIGN_DATA_WRAPPER = {ALTER, "FOREIGN", "DATA", "WRAPPER"};
		static final String[] STMT_ALTER_FUNCTION = {ALTER, "FUNCTION"};
		static final String[] STMT_ALTER_EXTERNAL_FUNCTION = {ALTER, "EXTERNAL", "FUNCTION"};
		static final String[] STMT_ALTER_ROLE = {ALTER, "ROLE"};
		static final String[] STMT_ALTER_SCHEMA = {ALTER, "SCHEMA"};
		static final String[] STMT_ALTER_SEQUENCE = {ALTER, "SEQUENCE"};
		static final String[] STMT_ALTER_SERVER = {ALTER, "SERVER"};
		static final String[] STMT_ALTER_USER = {ALTER, "USER"};
		static final String[] STMT_ALTER_VIEW = {ALTER, "VIEW"};
		static final String[] STMT_MATERIALIZED_ALTER_VIEW = {ALTER, "MATERIALIZED", "VIEW"};
		static final String[] STMT_ALTER_FILE_FORMAT = {ALTER, "FILE", "FORMAT"};
		static final String[] STMT_ALTER_STAGE = {ALTER, "STAGE"};
		static final String[] STMT_ALTER_PIPE = {ALTER, "PIPE"};
		static final String[] STMT_ALTER_STREAM = {ALTER, "STREAM"};
		static final String[] STMT_ALTER_TASK = {ALTER, "TASK"};
		static final String[] STMT_ALTER_PROCEDURE = {ALTER, "PROCEDURE"};
		
	    static final String[][] ALTER_PHRASES = {
			STMT_ALTER_AGGREGATE,
			STMT_ALTER_CONVERSION,
			STMT_ALTER_DATABASE,
			STMT_ALTER_FOREIGN_DATA_WRAPPER,
			STMT_ALTER_FUNCTION,
			STMT_ALTER_EXTERNAL_FUNCTION,
			STMT_ALTER_ROLE,
			STMT_ALTER_SCHEMA,
			STMT_ALTER_SEQUENCE,
			STMT_ALTER_SERVER,
			STMT_ALTER_USER,
			STMT_ALTER_VIEW,
			STMT_MATERIALIZED_ALTER_VIEW,
			STMT_ALTER_FILE_FORMAT,
			STMT_ALTER_STAGE,
			STMT_ALTER_PIPE,
			STMT_ALTER_STREAM,
			STMT_ALTER_TASK,
			STMT_ALTER_PROCEDURE,
		};
	    

	    static final String[] STMT_CREATE_DATABASE = {CREATE, "DATABASE"};
	    static final String[] STMT_CREATE_FUNCTION = {CREATE, "FUNCTION"};
		static final String[] STMT_CREATE_EXTERNAL_FUNCTION = {CREATE, "EXTERNAL", "FUNCTION"};
	    static final String[] STMT_CREATE_OR_REPLACE_FUNCTION = {CREATE, "OR", "REPLACE", "FUNCTION"};
	    static final String[] STMT_CREATE_ROLE = {CREATE, "ROLE"};
	    static final String[] STMT_CREATE_EXTERNAL_TABLE = {CREATE, "EXTERNAL", TABLE};
		static final String[] STMT_CREATE_OR_REPLACE_EXTERNAL_TABLE = {CREATE, "OR", "REPLACE", "EXTERNAL", TABLE};
		static final String[] STMT_CREATE_SEQUENCE = {CREATE, "SEQUENCE"};
		static final String[] STMT_CREATE_OR_REPLACE_SEQUENCE = {CREATE,  "OR", "REPLACE", "SEQUENCE"};
	    static final String[] STMT_CREATE_TEMP_SEQUENCE = {CREATE, "TEMP", "SEQUENCE"};
	    static final String[] STMT_CREATE_TEMPORARY_SEQUENCE = {CREATE, "TEMPORARY", "SEQUENCE"};
	    static final String[] STMT_CREATE_USER = {CREATE, "USER"};
	    static final String[] STMT_CREATE_SECURE_VIEW = {CREATE, "SECURE", VIEW};
		static final String[] STMT_CREATE_OR_REPLACE_SECURE_VIEW = {CREATE, "OR", "REPLACE", "SECURE", VIEW};
		static final String[] STMT_CREATE_RECURSIVE_VIEW = {CREATE, "RECURSIVE", VIEW};
		static final String[] STMT_CREATE_OR_REPLACE_RECURSIVE_VIEW = {CREATE, "OR", "REPLACE", "RECURSIVE", VIEW};
		static final String[] STMT_CREATE_SECURE_RECURSIVE_VIEW = {CREATE, "SECURE", "RECURSIVE", VIEW};
		static final String[] STMT_CREATE_OR_REPLACE_SECURE_RECURSIVE_VIEW = {CREATE, "OR", "REPLACE", "SECURE", "RECURSIVE", VIEW};
		static final String[] STMT_CREATE_MATERIALIZED_VIEW = {CREATE, "MATERIALIZED", VIEW};
		static final String[] STMT_CREATE_OR_REPLACE_MATERIALIZED_VIEW = {CREATE, "OR", "REPLACE", "MATERIALIZED", VIEW};
		static final String[] STMT_CREATE_SECURE_MATERIALIZED_VIEW = {CREATE, "SECURE", "MATERIALIZED", VIEW};
		static final String[] STMT_CREATE_OR_REPLACE_SECURE_MATERIALIZED_VIEW = {CREATE, "OR", "REPLACE", "SECURE", "MATERIALIZED", VIEW};
		static final String[] STMT_CREATE_OR_REPLACE_TABLE = {CREATE,  "OR", "REPLACE", TABLE};
		static final String[] STMT_CREATE_TABLE_TRANSIENT_TABLE =  {CREATE, "TRANSIENT", TABLE};
		static final String[] STMT_CREATE_OR_REPLACE_TABLE_TRANSIENT_TABLE =  {CREATE, "OR", "REPLACE", "TRANSIENT", TABLE};
		static final String[] STMT_CREATE_TABLE_VOLATILE_TABLE = {CREATE, "VOLATILE", TABLE};
		static final String[] STMT_CREATE_OR_REPLACE_TABLE_VOLATILE_TABLE = {CREATE, "OR", "REPLACE", "VOLATILE", TABLE};
		static final String[] STMT_CREATE_MASKING_POLICY = {CREATE,  "MASKING", "POLICY"};
		static final String[] STMT_CREATE_STAGE = {CREATE, "STAGE"};
		static final String[] STMT_CREATE_PIPE = {CREATE, "PIPE"};
		static final String[] STMT_CREATE_STREAM = {CREATE, "STREAM"};
		static final String[] STMT_CREATE_TASK = {CREATE, "TASK"};
		static final String[] STMT_CREATE_PROCEDURE = {CREATE, "PROCEDURE"};
		static final String[] STMT_CREATE_NETWORK_POLICY = {CREATE, "NETWORK", "POLICY"};
		static final String[] STMT_CREATE_RESOURCE_MONITOR = {CREATE, "RESOURCE", "MONITOR"};
		static final String[] STMT_CREATE_SHARE = {CREATE, "SHARE"};
		static final String[] STMT_CREATE_WAREHOUSE = {CREATE, "WAREHOUSE"};
		static final String[] STMT_CREATE_NOTIFICATION_INTEGRATION = {CREATE, "NOTIFICATION", "INTEGRATION"};
		static final String[] STMT_CREATE_SECURITY_INTEGRATION = {CREATE, "SECURITY", "INTEGRATION"};
		static final String[] STMT_CREATE_STORAGE_INTEGRATION = {CREATE, "STORAGE", "INTEGRATION"};

	    
	    static final String[][] CREATE_PHRASES = {
			STMT_CREATE_DATABASE,
			STMT_CREATE_FUNCTION,
			STMT_CREATE_EXTERNAL_FUNCTION,
			STMT_CREATE_OR_REPLACE_FUNCTION,
			STMT_CREATE_ROLE,
			STMT_CREATE_EXTERNAL_TABLE,
			STMT_CREATE_OR_REPLACE_EXTERNAL_TABLE,
			STMT_CREATE_SEQUENCE,
			STMT_CREATE_OR_REPLACE_SEQUENCE,
			STMT_CREATE_TEMP_SEQUENCE,
			STMT_CREATE_TEMPORARY_SEQUENCE,
			STMT_CREATE_USER,
			STMT_CREATE_SECURE_VIEW,
			STMT_CREATE_OR_REPLACE_SECURE_VIEW,
			STMT_CREATE_RECURSIVE_VIEW,
			STMT_CREATE_OR_REPLACE_RECURSIVE_VIEW,
			STMT_CREATE_SECURE_RECURSIVE_VIEW,
			STMT_CREATE_OR_REPLACE_SECURE_RECURSIVE_VIEW,
			STMT_CREATE_MATERIALIZED_VIEW,
			STMT_CREATE_OR_REPLACE_MATERIALIZED_VIEW,
			STMT_CREATE_SECURE_MATERIALIZED_VIEW,
			STMT_CREATE_OR_REPLACE_SECURE_MATERIALIZED_VIEW,
				STMT_CREATE_OR_REPLACE_TABLE,
			STMT_CREATE_TABLE_TRANSIENT_TABLE,
			STMT_CREATE_OR_REPLACE_TABLE_TRANSIENT_TABLE,
			STMT_CREATE_TABLE_VOLATILE_TABLE,
			STMT_CREATE_OR_REPLACE_TABLE_VOLATILE_TABLE,
			STMT_CREATE_MASKING_POLICY,
			STMT_CREATE_STAGE,
			STMT_CREATE_PIPE,
			STMT_CREATE_STREAM,
			STMT_CREATE_TASK,
			STMT_CREATE_PROCEDURE,
			STMT_CREATE_NETWORK_POLICY,
			STMT_CREATE_RESOURCE_MONITOR,
			STMT_CREATE_SHARE,
			STMT_CREATE_WAREHOUSE,
			STMT_CREATE_NOTIFICATION_INTEGRATION,
			STMT_CREATE_SECURITY_INTEGRATION,
			STMT_CREATE_STORAGE_INTEGRATION,
		};

		static final String[] STMT_DROP_DATABASE = {DROP, "DATABASE"};
		static final String[] STMT_DROP_FUNCTION = {DROP, "FUNCTION"};
		static final String[] STMT_DROP_EXTERNAL_FUNCTION = {DROP, "EXTERNAL", "FUNCTION"};
		static final String[] STMT_DROP_OR_REPLACE_FUNCTION = {DROP, "OR", "REPLACE", "FUNCTION"};
		static final String[] STMT_DROP_ROLE = {DROP, "ROLE"};
		static final String[] STMT_DROP_EXTERNAL_TABLE = {DROP, "EXTERNAL", TABLE};
		static final String[] STMT_DROP_SEQUENCE = {DROP, "SEQUENCE"};
		static final String[] STMT_DROP_TEMP_SEQUENCE = {DROP, "TEMP", "SEQUENCE"};
		static final String[] STMT_DROP_TEMPORARY_SEQUENCE = {DROP, "TEMPORARY", "SEQUENCE"};
		static final String[] STMT_DROP_USER = {DROP, "USER"};
		static final String[] STMT_DROP_MATERIALIZED_VIEW = {DROP, "MATERIALIZED", VIEW}; // EDWM-2505
		static final String[] STMT_DROP_TABLE_IF_NOT_EXISTS = {DROP, TABLE, "IF", "NOT", "EXISTS"};
		static final String[] STMT_DROP_MASKING_POLICY = {DROP, TABLE, "IF", "NOT", "EXISTS"};
		static final String[] STMT_DROP_STAGE = {DROP, "STAGE", "IF", "NOT", "EXISTS"};
		static final String[] STMT_DROP_PIPE = {DROP, "PIPE"};
		static final String[] STMT_DROP_STREAM = {DROP, "STREAM"};
		static final String[] STMT_DROP_TASK = {DROP, "TASK"};
		static final String[] STMT_DROP_PROCEDURE = {DROP, "PROCEDURE"};
		static final String[] STMT_DROP_NETWORK_POLICY = {DROP, "NETWORK", "POLICY"};
		static final String[] STMT_DROP_RESOURCE_MONITOR = {DROP, "RESOURCE", "MONITOR"};
		static final String[] STMT_DROP_SHARE = {DROP, "SHARE"};
		static final String[] STMT_DROP_WAREHOUSE = {DROP, "WAREHOUSE"};
		static final String[] STMT_DROP_NOTIFICATION_INTEGRATION = {DROP, "NOTIFICATION", "INTEGRATIO"};
		static final String[] STMT_DROP_SECURITY_INTEGRATION = {DROP, "SECURITY", "INTEGRATION"};
		static final String[] STMT_DROP_STORAGE_INTEGRATION = {DROP, "STORAGE", "INTEGRATION"};



		static final String[][] DROP_PHRASES = {
			STMT_DROP_DATABASE,
			STMT_DROP_FUNCTION,
			STMT_DROP_EXTERNAL_FUNCTION,
			STMT_DROP_OR_REPLACE_FUNCTION,
			STMT_DROP_ROLE,
			STMT_DROP_EXTERNAL_TABLE,
			STMT_DROP_SEQUENCE,
			STMT_DROP_TEMP_SEQUENCE,
			STMT_DROP_TEMPORARY_SEQUENCE,
			STMT_DROP_USER,
			STMT_DROP_MATERIALIZED_VIEW,
			STMT_DROP_TABLE_IF_NOT_EXISTS,
			STMT_DROP_MASKING_POLICY,
			STMT_DROP_STAGE,
			STMT_DROP_PIPE,
			STMT_DROP_STREAM,
			STMT_DROP_TASK,
			STMT_DROP_PROCEDURE,
			STMT_DROP_NETWORK_POLICY,
			STMT_DROP_RESOURCE_MONITOR,
			STMT_DROP_SHARE,
			STMT_DROP_WAREHOUSE,
			STMT_DROP_NOTIFICATION_INTEGRATION,
			STMT_DROP_SECURITY_INTEGRATION,
			STMT_DROP_STORAGE_INTEGRATION,
		};
	    
	    static final String[] STMT_SET_CONSTRAINTS = {"SET", "CONSTRAINTS"};
	    static final String[] STMT_SET_ROLE = {"SET", "ROLE"};
	    static final String[] STMT_SET_SESSION_AUTHORIZATION= {"SET", "SESSION", "AUTHORIZATION"};
	    static final String[] STMT_SET_TRANSACTION = {"SET", "TRANSACTION"};
	    
	    static final String[][] SET_PHRASES = { 
	    	STMT_SET_CONSTRAINTS, STMT_SET_ROLE, STMT_SET_SESSION_AUTHORIZATION, STMT_SET_TRANSACTION
		};
	    
	    static final String[] STMT_ABORT = {"ABORT"};
	    static final String[] STMT_ANALYZE = {"ANALYZE"};
	    static final String[] STMT_CLUSTER = {"CLUSTER"};
	    static final String[] STMT_COMMENT_ON = {"COMMENT", "ON"};
	    static final String[] STMT_COMMIT = {"COMMIT"};
	    static final String[] STMT_COPY = {"COPY"};
	    static final String[] STMT_DEALLOCATE_PREPARE = {"DEALLOCATE", "PREPARE"};
	    static final String[] STMT_DEALLOCATE = {"DEALLOCATE"};
	    static final String[] STMT_DECLARE = {"DECLARE"};
	    static final String[] STMT_DISCARD = {"DISCARD"};
	    static final String[] STMT_EXPLAIN_ANALYZE = {"EXPLAIN", "ANALYZE"};
	    static final String[] STMT_EXPLAIN = {"EXPLAIN"};
	    static final String[] STMT_FETCH = {"FETCH"};
	    static final String[] STMT_LISTEN = {"LISTEN"};
	    static final String[] STMT_LOAD = {"LOAD"};
		static final String[] STMT_LOCK_TABLE = {"LOCK", "TABLE"};
	    static final String[] STMT_MOVE = {"MOVE"};
	    static final String[] STMT_NOTIFY = {"NOTIFY"};
	    static final String[] STMT_PREPARE = {"PREPARE"};
	    static final String[] STMT_PREPARE_TRANSATION = {"PREPARE", "TRANSATION"};
	    static final String[] STMT_REASSIGN_OWNED = {"REASSIGN", "OWNED"};
	    static final String[] STMT_REINDEX = {"REINDEX"};
	    static final String[] STMT_RELEASE_SAVEPOINT = {"RELEASE", "SAVEPOINT"};
	    static final String[] STMT_REVOKE = {"REVOKE"};
	    static final String[] STMT_ROLLBACK = {"ROLLBACK"};
	    static final String[] STMT_ROLLBACK_PREPARED = {"ROLLBACK", "PREPARED"};
	    static final String[] STMT_ROLLBACK_TO_SAVEPOINT = {"ROLLBACK", "TO", "SAVEPOINT"};
	    static final String[] STMT_SELECT_INTO = {"SELECT", "INTO"};

	    static final String[] STMT_SHOW = {"SHOW"};
	    static final String[] STMT_TRUNCATE = {"TRUNCATE"};
	    static final String[] STMT_UNLISTEN = {"UNLISTEN"};
	    static final String[] STMT_VACUUM = {"VACUUM"};
	    //static final String[] STMT_VALUES = {"VALUES"};
	    
	    static final String[][] MISC_PHRASES = {
	    	STMT_ABORT, STMT_ANALYZE, STMT_CLUSTER, STMT_COMMENT_ON, STMT_COMMIT, STMT_COPY, STMT_DEALLOCATE_PREPARE, STMT_DEALLOCATE,
	    	STMT_DECLARE, STMT_DISCARD, STMT_EXPLAIN_ANALYZE, STMT_EXPLAIN, STMT_FETCH, STMT_LISTEN, STMT_LOAD, STMT_LOCK_TABLE,
	    	STMT_MOVE, STMT_NOTIFY, STMT_PREPARE, STMT_PREPARE_TRANSATION, STMT_REASSIGN_OWNED, STMT_REINDEX, 
	    	STMT_RELEASE_SAVEPOINT, STMT_REVOKE, STMT_ROLLBACK_TO_SAVEPOINT, STMT_ROLLBACK_PREPARED, 
	    	STMT_ROLLBACK, STMT_SELECT_INTO, STMT_SHOW, STMT_TRUNCATE, STMT_UNLISTEN, STMT_VACUUM   //, STMT_VALUES
		};
	    
	    // CREATE TABLE, CREATE VIEW, CREATE INDEX, CREATE SEQUENCE, CREATE TRIGGER and GRANT
	    public final static String[] VALID_SCHEMA_CHILD_STMTS = {
	    	StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT, 
	    	StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT,
	    	StandardDdlLexicon.TYPE_GRANT_ON_TABLE_STATEMENT,
	    	SnowflakeDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT,
	    	SnowflakeDdlLexicon.TYPE_GRANT_ON_SEQUENCE_STATEMENT,
	    	SnowflakeDdlLexicon.TYPE_GRANT_ON_SCHEMA_STATEMENT
	  	};
	    
	    public final static String[] COMPLEX_STMT_TYPES = {
	    	SnowflakeDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT
	    };
	}
	
		//SPEC  Name				Aliases		Description
		//
		//	X	bigint					int8		signed eight-byte integer
		//		bigserial				serial8		autoincrementing eight-byte integer
		//	X	bit [ (n) ]	 						fixed-length bit string
		//	X	bit varying [ (n) ]		varbit		variable-length bit string
		//		boolean					bool		logical Boolean (true/false)
		//		box	 								rectangular box on a plane
		//		bytea	 							binary data ("byte array")
		//	X	character varying [ (n) ]	varchar [ (n) ]	variable-length character string
		//	X	character [ (n) ]		char [ (n) ]	fixed-length character string
		//		cidr	 							IPv4 or IPv6 network address
		//		circle	 							circle on a plane
		//	X	date	 							calendar date (year, month, day)
		//	X	double precision		float8		double precision floating-point number (8 bytes)
		//		inet	 							IPv4 or IPv6 host address
		//	X	integer					int, int4	signed four-byte integer
		//	X	interval [ fields ] [ (p) ]	 		time span
		//		line	 							infinite line on a plane
		//		lseg	 							line segment on a plane
		//		macaddr	 							MAC (Media Access Control) address
		//		money	 							currency amount
		//	X	numeric [ (p, s) ]		decimal [ (p, s) ]	exact numeric of selectable precision
		//		path	 							geometric path on a plane
		//		point	 							geometric point on a plane
		//		polygon	 							closed geometric path on a plane
		//	X	real					float4		single precision floating-point number (4 bytes)
		//	X	smallint				int2		signed two-byte integer
		//		serial					serial4		auto incrementing four-byte integer
		//		text	 							variable-length character string
		//		time [ (p) ] [ without time zone ]	 	time of day (no time zone)
		//	X	time [ (p) ] with time zone	timetz		time of day, including time zone
		//		timestamp [ (p) ] [ without time zone ]	 	date and time (no time zone)
		//	X	timestamp [ (p) ] with time zone	timestamptz	date and time, including time zone
		//		tsquery	 							text search query
		//		tsvector	 						text search document
		//		txid_snapshot	 					user-level transaction ID snapshot
		//		uuid	 							universally unique identifier
		//	X	xml	 								XML data
		//      interval hour to minute


	interface SnowflakeDataTypes {
		static final String[] DTYPE_ARRAY = {"ARRAY"};
		static final String[] DTYPE_BIGINT = {"BIGINT"};
		static final String[] DTYPE_BINARY = {"BINARY"};
		static final String[] DTYPE_BOOLEAN = {"BOOLEAN"};
		static final String[] DTYPE_CHAR = {"CHAR"};
		static final String[] DTYPE_CHARACTER = {"CHARACTER"};
		static final String[] DTYPE_DATE = {"DATE"};
		static final String[] DTYPE_DATETIME = {"DATETIME"};
		static final String[] DTYPE_DECIMAL = {"DECIMAL"};
		static final String[] DTYPE_DOUBLE = {"DOUBLE"};
		static final String[] DTYPE_DOUBLE_PRECISION = {"DOUBLE PRECISION"};
		static final String[] DTYPE_FLOAT = {"FLOAT"};
		static final String[] DTYPE_FLOAT4 = {"FLOAT4"};
		static final String[] DTYPE_FLOAT8 = {"FLOAT8"};
		static final String[] DTYPE_GEOGRAPHY = {"GEOGRAPHY"};
		static final String[] DTYPE_INT = {"INT"};
		static final String[] DTYPE_INTEGER = {"INTEGER"};
		static final String[] DTYPE_NUMBER = {"NUMBER"};
		static final String[] DTYPE_NUMERIC = {"NUMERIC"};
		static final String[] DTYPE_OBJECT = {"OBJECT"};
		static final String[] DTYPE_REAL = {"REAL"};
		static final String[] DTYPE_SMALLINT = {"SMALLINT"};
		static final String[] DTYPE_STRING = {"STRING"};
		static final String[] DTYPE_TEXT = {"TEXT"};
		static final String[] DTYPE_TIME = {"TIME"};
		static final String[] DTYPE_TIMESTAMP = {"TIMESTAMP"};
		static final String[] DTYPE_TIMESTAMP_LTZ = {"TIMESTAMP_LTZ"};
		static final String[] DTYPE_TIMESTAMP_NTZ = {"TIMESTAMP_NTZ"};
		static final String[] DTYPE_TIMESTAMP_TZ = {"TIMESTAMP_TZ"};
		static final String[] DTYPE_VARBINARY = {"VARBINARY"};
		static final String[] DTYPE_VARCHAR = {"VARCHAR"};
		static final String[] DTYPE_VARIANT = {"VARIANT"};
		
		static final List<String[]> CUSTOM_DATATYPE_START_PHRASES = 
	  		Arrays.asList(
				DTYPE_ARRAY,
				DTYPE_BIGINT,
				DTYPE_BINARY,
				DTYPE_BOOLEAN,
				DTYPE_CHAR,
				DTYPE_CHARACTER,
				DTYPE_DATE,
				DTYPE_DATETIME,
				DTYPE_DECIMAL,
				DTYPE_DOUBLE,
				DTYPE_DOUBLE_PRECISION,
				DTYPE_FLOAT,
				DTYPE_FLOAT4,
				DTYPE_FLOAT8,
				DTYPE_GEOGRAPHY,
				DTYPE_INT,
				DTYPE_INTEGER,
				DTYPE_NUMBER,
				DTYPE_NUMERIC,
				DTYPE_OBJECT,
				DTYPE_REAL,
				DTYPE_SMALLINT,
				DTYPE_STRING,
				DTYPE_TEXT,
				DTYPE_TIME,
				DTYPE_TIMESTAMP,
				DTYPE_TIMESTAMP_LTZ,
				DTYPE_TIMESTAMP_NTZ,
				DTYPE_TIMESTAMP_TZ,
				DTYPE_VARBINARY,
				DTYPE_VARCHAR,
				DTYPE_VARIANT
			);
		
	  	static final List<String> CUSTOM_DATATYPE_START_WORDS = Arrays.asList(
			"ARRAY",
			"BIGINT",
			"BINARY",
			"BOOLEAN",
			"CHAR",
			"CHARACTER",
			"DATE",
			"DATETIME",
			"DECIMAL",
			"DOUBLE",
			"FLOAT",
			"FLOAT4",
			"FLOAT8",
			"GEOGRAPHY",
			"INT",
			"INTEGER",
			"NUMBER",
			"NUMERIC",
			"OBJECT",
			"REAL",
			"SMALLINT",
			"STRING",
			"TEXT",
			"TIME",
			"TIMESTAMP",
			"TIMESTAMP_LTZ",
			"TIMESTAMP_NTZ",
			"TIMESTAMP_TZ",
			"VARBINARY",
			"VARCHAR",
			"VARIANT"
		);
	}
}
