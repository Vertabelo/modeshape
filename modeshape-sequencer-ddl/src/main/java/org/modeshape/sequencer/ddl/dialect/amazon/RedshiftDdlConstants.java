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
package org.modeshape.sequencer.ddl.dialect.amazon;

import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlTokenStream;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public interface RedshiftDdlConstants extends DdlConstants {
	public static final String[] CUSTOM_KEYWORDS = {
			"ES128", "AES256", "ALLOWOVERWRITE", "ANALYSE", "ANALYZE", "ARRAY", "BACKUP", "BINARY", "BLANKSASNULL",
			"BYTEDICT", "BZIP2", "CREDENTIALS", "CURRENT_USER_ID", "DEFLATE", "DEFRAG", "DELTA", "DELTA32K", "DISABLE",
			"DO", "EMPTYASNULL", "ENABLE", "ENCODE", "ENCRYPT", "ENCRYPTION", "EXPLICIT", "FREEZE", "GLOBALDICT256",
			"GLOBALDICT64K", "GZIP", "IGNORE", "ILIKE", "ISNULL", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "LUN", "LUNS",
			"LZO", "LZOP", "MINUS", "MOSTLY13", "MOSTLY32", "MOSTLY8", "NEW", "NOTNULL", "NULLS", "OFF", "OFFLINE",
			"OFFSET", "OID", "OLD", "PARALLEL", "PARTITION", "PERCENT", "PERMISSIONS", "PLACING", "RAW", "READRATIO",
			"RECOVER", "RESPECT", "REJECTLOG", "RESORT", "RESTORE", "SIMILAR", "SNAPSHOT ", "SYSDATE", "SYSTEM", "TAG",
			"TDES", "TEXT255", "TEXT32K", "TOP", "TRUNCATECOLUMNS", "VERBOSE", "WALLET", "WITHOUT"
	};
	
	public static final String[] IF_NOT_EXISTS = {"IF", "NOT", "EXISTS"};

	interface RedshiftStatementStartPhrases {
		static final String[] STMT_ALTER_DATABASE = {ALTER, "DATABASE"};
		static final String[] STMT_ALTER_GROUP = {ALTER, "GROUP", DdlTokenStream.ANY_VALUE, DdlTokenStream.ANY_VALUE, DdlTokenStream.ANY_VALUE };
		static final String[] STMT_ALTER_LIBRARY = {ALTER, "LIBRARY"};
		static final String[] STMT_ALTER_PROCEDURE = {ALTER, "PROCEDURE"};
		static final String[] STMT_ALTER_SCHEMA = {ALTER, "SCHEMA"};
		static final String[] STMT_ALTER_USER = {ALTER, "USER"};
		static final String[] STMT_ALTER_VIEW = {ALTER, "VIEW"};
		
	    static final String[][] ALTER_PHRASES = { 
	    	STMT_ALTER_DATABASE, STMT_ALTER_GROUP, STMT_ALTER_PROCEDURE, STMT_ALTER_LIBRARY,
			STMT_ALTER_SCHEMA, STMT_ALTER_USER,
	    	STMT_ALTER_VIEW
		};
	    

	    static final String[] STMT_CREATE_DATABASE = {CREATE, "DATABASE"};
	    static final String[] STMT_CREATE_EXTERNAL_SCHEMA = {CREATE, "EXTERNAL", "SCHEMA"};
		static final String[] STMT_CREATE_EXTERNAL_TABLE = {CREATE, "EXTERNAL", "TABLE"};
	    static final String[] STMT_CREATE_FUNCTION = {CREATE, "FUNCTION"};
	    static final String[] STMT_CREATE_OR_REPLACE_FUNCTION = {CREATE, "OR", "REPLACE", "FUNCTION"};
	    static final String[] STMT_CREATE_GROUP = {CREATE, "GROUP"};
	    static final String[] STMT_CREATE_LIBRARY = {CREATE, "LIBRARY"};
	    static final String[] STMT_CREATE_PROCEDURE = {CREATE, "PROCEDURE"};
		static final String[] STMT_CREATE_OR_REPLACE_PROCEDURE = {CREATE, "OR", "REPLACE", "PROCEDURE"};
	    static final String[] STMT_CREATE_OR_REPLACE_RULE = {CREATE, "OR", "REPLACE", "RULE"};
	    static final String[] STMT_CREATE_TEMP_TABLE = {CREATE, "TEMP", TABLE};
		static final String[] STMT_CREATE_TEMPORARY_TABLE = {CREATE, "TEMPORARY", TABLE};
		static final String[] STMT_CREATE_LOCAL_TEMP_TABLE = {CREATE, "LOCAL", "TEMP", TABLE};
		static final String[] STMT_CREATE_USER = {CREATE, "USER"};
		static final String[] STMT_CREATE_TABLE_IF_NOT_EXISTS = {CREATE, TABLE, "IF", "NOT", "EXISTS"};

	    
	    static final String[][] CREATE_PHRASES = { 
			STMT_CREATE_DATABASE, STMT_CREATE_EXTERNAL_SCHEMA, STMT_CREATE_EXTERNAL_TABLE, STMT_CREATE_FUNCTION, STMT_CREATE_OR_REPLACE_FUNCTION,
	    	STMT_CREATE_GROUP, STMT_CREATE_PROCEDURE, STMT_CREATE_OR_REPLACE_PROCEDURE,
			STMT_CREATE_LIBRARY, STMT_CREATE_OR_REPLACE_RULE,
	    	STMT_CREATE_TEMP_TABLE, STMT_CREATE_TEMPORARY_TABLE, STMT_CREATE_LOCAL_TEMP_TABLE,
			STMT_CREATE_USER, STMT_CREATE_TABLE_IF_NOT_EXISTS
		};
	    
	    static final String[] STMT_DROP_DATABASE = {DROP, "DATABASE"};
	    static final String[] STMT_DROP_FUNCTION = {DROP, "FUNCTION"};
	    static final String[] STMT_DROP_GROUP = {DROP, "GROUP"};
	    static final String[] STMT_DROP_LIBRARY = {DROP, "LIBRARY"};
	    static final String[] STMT_DROP_PROCEDURE = {DROP, "PROCEDURE"};
	    static final String[] STMT_DROP_USER = {DROP, "USER"};

	    
	    static final String[][] DROP_PHRASES = { 
	    	STMT_DROP_DATABASE, STMT_DROP_FUNCTION,
	    	STMT_DROP_GROUP, STMT_DROP_PROCEDURE,
			STMT_DROP_LIBRARY, STMT_DROP_USER
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
	    static final String[] STMT_COMMENT_ON = {"COMMENT", "ON"};
	    static final String[] STMT_COMMIT = {"COMMIT"};
		static final String[] STMT_CLOSE = {"CLOSE"};
	    static final String[] STMT_COPY = {"COPY"};
	    static final String[] STMT_DEALLOCATE_PREPARE = {"DEALLOCATE", "PREPARE"};
	    static final String[] STMT_DEALLOCATE = {"DEALLOCATE"};
	    static final String[] STMT_DECLARE = {"DECLARE"};
	    static final String[] STMT_EXPLAIN_ANALYZE = {"EXPLAIN", "ANALYZE"};
	    static final String[] STMT_EXPLAIN = {"EXPLAIN"};
	    static final String[] STMT_FETCH = {"FETCH"};
	    static final String[] STMT_LOCK_TABLE = {"LOCK", "TABLE"};
	    static final String[] STMT_PREPARE = {"PREPARE"};
	    static final String[] STMT_PREPARE_TRANSATION = {"PREPARE", "TRANSATION"};
	    static final String[] STMT_REVOKE = {"REVOKE"};
	    static final String[] STMT_ROLLBACK = {"ROLLBACK"};
	    static final String[] STMT_ROLLBACK_PREPARED = {"ROLLBACK", "PREPARED"};
	    static final String[] STMT_ROLLBACK_TO_SAVEPOINT = {"ROLLBACK", "TO", "SAVEPOINT"};
	    static final String[] STMT_SELECT_INTO = {"SELECT", "INTO"};

	    static final String[] STMT_SHOW = {"SHOW"};
	    static final String[] STMT_TRUNCATE = {"TRUNCATE"};
	    static final String[] STMT_UNLISTEN = {"UNLISTEN"};
	    static final String[] STMT_VACUUM = {"VACUUM"};

	    static final String[][] MISC_PHRASES = {
	    	STMT_ABORT, STMT_ANALYZE, STMT_CLOSE, STMT_COMMENT_ON, STMT_COMMIT, STMT_COPY, STMT_DEALLOCATE_PREPARE, STMT_DEALLOCATE,
	    	STMT_DECLARE,  STMT_EXPLAIN_ANALYZE, STMT_EXPLAIN, STMT_FETCH,  STMT_LOCK_TABLE,
	    	STMT_PREPARE, STMT_PREPARE_TRANSATION,
	    	STMT_REVOKE, STMT_ROLLBACK_TO_SAVEPOINT, STMT_ROLLBACK_PREPARED,
	    	STMT_ROLLBACK, STMT_SELECT_INTO, STMT_SHOW, STMT_TRUNCATE, STMT_UNLISTEN, STMT_VACUUM   //, STMT_VALUES
		};
	    
	    // CREATE TABLE, CREATE VIEW, CREATE INDEX, CREATE SEQUENCE, CREATE TRIGGER and GRANT
	    public final static String[] VALID_SCHEMA_CHILD_STMTS = {
	    	StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT, 
	    	StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT,
	    	StandardDdlLexicon.TYPE_GRANT_ON_TABLE_STATEMENT,
	    	RedshiftDdlLexicon.TYPE_GRANT_ON_SCHEMA_STATEMENT
	  	};
	    
	    public final static String[] COMPLEX_STMT_TYPES = {
	    	RedshiftDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT
	    };
	}

	/**
	 *
	 * Data Type	Aliases	Description
	 * SMALLINT	INT2	Signed two-byte integer
	 * INTEGER	INT, INT4	Signed four-byte integer
	 * BIGINT	INT8	Signed eight-byte integer
	 * DECIMAL	NUMERIC	Exact numeric of selectable precision
	 * REAL	FLOAT4	Single precision floating-point number
	 * DOUBLE PRECISION	FLOAT8, FLOAT	Double precision floating-point number
	 * BOOLEAN	BOOL	Logical Boolean (true/false)
	 * CHAR	CHARACTER, NCHAR, BPCHAR	Fixed-length character string
	 * VARCHAR	CHARACTER VARYING, NVARCHAR, TEXT	Variable-length character string with a user-defined limit
	 * DATE		Calendar date (year, month, day)
	 * TIMESTAMP	TIMESTAMP WITHOUT TIME ZONE	Date and time (without time zone)
	 * TIMESTAMPTZ	TIMESTAMP WITH TIME ZONE	Date and time (with time zone)
	 */

	interface RedshiftDataTypes {
	    static final String[] DTYPE_BIGINT = {"BIGINT"};
		static final String[] DTYPE_TEXT = {"TEXT"};
		static final String[] DTYPE_BOOLEAN = {"BOOLEAN"};
		static final String[] DTYPE_BOOL = {"BOOL"}; 
		static final String[] DTYPE_FLOAT4 = {"FLOAT4"}; 
		static final String[] DTYPE_FLOAT8 = {"FLOAT8"}; 
		static final String[] DTYPE_INT2 = {"INT2"}; 
		static final String[] DTYPE_INT4 = {"INT4"}; 
		static final String[] DTYPE_INT8 = {"INT8"}; 
		static final String[] DTYPE_TIMESTAMPZ = {"TIMESTAMPZ"};
		static final String[] DTYPE_TIMESTAMPTZ = {"TIMESTAMPTZ" };

		
		static final List<String[]> CUSTOM_DATATYPE_START_PHRASES = 
	  		Arrays.asList(DTYPE_BIGINT, DTYPE_BOOL, DTYPE_BOOLEAN,
                            DTYPE_FLOAT4, DTYPE_FLOAT8,  DTYPE_INT2, DTYPE_INT4, DTYPE_INT8,
							DTYPE_TEXT, DTYPE_TIMESTAMPZ,
                            DTYPE_TIMESTAMPTZ);
		
	  	static final List<String> CUSTOM_DATATYPE_START_WORDS = 
	  		Arrays.asList("BIGINT", "BOOLEAN", "BOOL",
                            "FLOAT4", "FLOAT8", "INT2", "INT4", "INT8",
                            "TEXT", "TIMESTAMPZ",
                            "TIMESTAMPTZ");
	}
}
