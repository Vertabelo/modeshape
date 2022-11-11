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
 * Lesser General Public License for more details
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org
 */
package org.modeshape.sequencer.ddl.dialect.mysql8;

import java.util.ArrayList;
import java.util.List;

import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserProblem;
import org.modeshape.sequencer.ddl.DdlSequencerI18n;
import org.modeshape.sequencer.ddl.DdlTokenStream;
import org.modeshape.sequencer.ddl.DdlTokenStream.DdlTokenizer;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.StandardDdlParser;
import org.modeshape.sequencer.ddl.datatype.DataType;
import org.modeshape.sequencer.ddl.datatype.DataTypeParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.modeshape.sequencer.ddl.DdlConstants.DataTypes.DTYPE_NATIONAL_CHARACTER_VARYING;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CHECK_SEARCH_CONDITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CONSTRAINT_TYPE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_ORIGINAL_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_START_CHAR_INDEX;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_START_COLUMN_NUMBER;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_START_LINE_NUMBER;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_ID_CURRENT_USER;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_ID_DATETIME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_ID_LITERAL;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_ID_NULL;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_ID_SESSION_USER;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_ID_SYSTEM_USER;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_ID_USER;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_OPTION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_PRECISION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_VALUE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DROP_BEHAVIOR;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NEW_NAME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NULLABLE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TEMPORARY;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_REFERENCE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_FK_COLUMN_REFERENCE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_TABLE_CONSTRAINT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_TABLE_REFERENCE;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.COLUMN_FORMAT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.COLUMN_STORAGE;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.COMMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TABLE_LIKE;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_ALGORITHM_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_DATABASE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_DEFINER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_EVENT_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_LOGFILE_GROUP_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_PROCEDURE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_SCHEMA_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_SERVER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_TABLESPACE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_ALTER_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_DEFINER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_EVENT_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_INDEX_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_SERVER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_TABLESPACE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_TRIGGER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_DATABASE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_EVENT_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_INDEX_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_LOGFILE_GROUP_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_PROCEDURE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_SERVER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_TABLESPACE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_DROP_TRIGGER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_RENAME_DATABASE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_RENAME_SCHEMA_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_RENAME_TABLE_STATEMENT;

/**
 * MySql 8.x specific DDL Parser. Includes custom data types as well as custom DDL statements.
 */
public class MySql8DdlParser extends StandardDdlParser
        implements MySql8DdlConstants, MySql8DdlConstants.MySqlStatementStartPhrases {
    /**
     * The MySQL parser identifier.
     */
    public static final String ID = "MYSQL8";

    static List<String[]> mysqlDataTypeStrings = new ArrayList<>();
    /*
    * ===========================================================================================================================
    	* Data Definition Statements
    	ALTER [DATABASE | EVENT | FUNCTION | SERVER | TABLE | VIEW]
    	CREATE [DATABASE | EVENT | FUNCTION | INDEX | PROCEDURE | SERVER | TABLE | TRIGGER | VIEW]
    	DROP [DATABASE | EVENT | FUNCTION | INDEX | PROCEDURE | SERVER | TABLE | TRIGGER | VIEW]
    	RENAME TABLE
    */

    /*
    * ===========================================================================================================================
    * CREATE TABLE
    
    CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
    (create_definition,...)
    [table_options]
    [partition_options]

    Or:
    
    CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
        [(create_definition,...)]
        [table_options]
        [partition_options]
        select_statement
    
    Or:
    
    CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
        { LIKE old_tbl_name | (LIKE old_tbl_name) }
    
    create_definition:
        col_name column_definition
      | [CONSTRAINT [symbol]] PRIMARY KEY [index_type] (index_col_name,...)
          [index_option] ...
      | {INDEX|KEY} [index_name] [index_type] (index_col_name,...)
          [index_option] ...
      | [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY]
          [index_name] [index_type] (index_col_name,...)
          [index_option] ...
      | {FULLTEXT|SPATIAL} [INDEX|KEY] [index_name] (index_col_name,...)
          [index_option] ...
      | [CONSTRAINT [symbol]] FOREIGN KEY
          [index_name] (index_col_name,...) reference_definition
      | CHECK (expr)
    
    column_definition:
        data_type [NOT NULL | NULL] [DEFAULT default_value]
          [AUTO_INCREMENT] [UNIQUE [KEY] | [PRIMARY] KEY]
          [COMMENT 'string']
          [COLUMN_FORMAT {FIXED|DYNAMIC|DEFAULT}]
          [STORAGE {DISK|MEMORY|DEFAULT}]
          [reference_definition]
    
    index_col_name:
        col_name [(length)] [ASC | DESC]
    
    index_type:
        USING {BTREE | HASH | RTREE}
    
    index_option:
        KEY_BLOCK_SIZE [=] value
      | index_type
      | WITH PARSER parser_name
    
    reference_definition:
        REFERENCES tbl_name (index_col_name,...)
          [MATCH FULL | MATCH PARTIAL | MATCH SIMPLE]
          [ON DELETE reference_option]
          [ON UPDATE reference_option]
    
    reference_option:
        RESTRICT | CASCADE | SET NULL | NO ACTION
    
    table_options:
        table_option [[,] table_option] ...
    
    table_option:
        ENGINE [=] engine_name
      | AUTO_INCREMENT [=] value
      | AVG_ROW_LENGTH [=] value
      | [DEFAULT] CHARACTER SET [=] charset_name
      | CHECKSUM [=] {0 | 1}
      | [DEFAULT] COLLATE [=] collation_name
      | COMMENT [=] 'string'
      | CONNECTION [=] 'connect_string'
      | DATA DIRECTORY [=] 'absolute path to directory'
      | DELAY_KEY_WRITE [=] {0 | 1}
      | INDEX DIRECTORY [=] 'absolute path to directory'
      | INSERT_METHOD [=] { NO | FIRST | LAST }
      | KEY_BLOCK_SIZE [=] value
      | MAX_ROWS [=] value
      | MIN_ROWS [=] value
      | PACK_KEYS [=] {0 | 1 | DEFAULT}
      | PASSWORD [=] 'string'
      | ROW_FORMAT [=] {DEFAULT|DYNAMIC|FIXED|COMPRESSED|REDUNDANT|COMPACT}
      | TABLESPACE tablespace_name [STORAGE {DISK|MEMORY|DEFAULT}]
      | UNION [=] (tbl_name[,tbl_name]...)
    
    partition_options:
        PARTITION BY
            { [LINEAR] HASH(expr)
            | [LINEAR] KEY(column_list)
            | RANGE(expr)
            | LIST(expr) }
        [PARTITIONS num]
        [SUBPARTITION BY
            { [LINEAR] HASH(expr)
            | [LINEAR] KEY(column_list) }
          [SUBPARTITIONS num]
        ]
        [(partition_definition [, partition_definition] ...)]
    
    partition_definition:
        PARTITION partition_name
            [VALUES {LESS THAN {(expr) | MAXVALUE} | IN (value_list)}]
            [[STORAGE] ENGINE [=] engine_name]
            [COMMENT [=] 'comment_text' ]
            [DATA DIRECTORY [=] 'data_dir']    	
            [INDEX DIRECTORY [=] 'index_dir']
            [MAX_ROWS [=] max_number_of_rows]
            [MIN_ROWS [=] min_number_of_rows]
            [TABLESPACE [=] tablespace_name]
            [NODEGROUP [=] node_group_id]
            [(subpartition_definition [, subpartition_definition] ...)]
    
    subpartition_definition:
        SUBPARTITION logical_name
            [[STORAGE] ENGINE [=] engine_name]
            [COMMENT [=] 'comment_text' ]
            [DATA DIRECTORY [=] 'data_dir']
            [INDEX DIRECTORY [=] 'index_dir']
            [MAX_ROWS [=] max_number_of_rows]
            [MIN_ROWS [=] min_number_of_rows]
            [TABLESPACE [=] tablespace_name]
            [NODEGROUP [=] node_group_id]
    
    select_statement:
        [IGNORE | REPLACE] [AS] SELECT ...   (Some legal select statement)

    
    * ===========================================================================================================================
    */
    private static final String TERMINATOR = DEFAULT_TERMINATOR;

    public MySql8DdlParser() {
        initialize();
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#getId()
     */
    @Override
    public String getId() {
        return ID;
    }

    private void initialize() {
        setDatatypeParser(new MySqlDataTypeParser());

        setDoUseTerminator(true);

        setTerminator(TERMINATOR);

        mysqlDataTypeStrings.addAll(MySqlDataTypes.CUSTOM_DATATYPE_START_PHRASES);
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#initializeTokenStream(DdlTokenStream)
     */
    @Override
    protected void initializeTokenStream(DdlTokenStream tokens) {
        super.initializeTokenStream(tokens);
        tokens.registerKeyWords(CUSTOM_KEYWORDS);
        tokens.registerKeyWords(MySqlDataTypes.CUSTOM_DATATYPE_START_WORDS);
        tokens.registerStatementStartPhrase(ALTER_PHRASES);
        tokens.registerStatementStartPhrase(CREATE_PHRASES);
        tokens.registerStatementStartPhrase(DROP_PHRASES);
        tokens.registerStatementStartPhrase(MISC_PHRASES);
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#parseCreateStatement(DdlTokenStream,
     * AstNode)
     */
    @Override
    protected AstNode parseCreateStatement(DdlTokenStream tokens,
                                           AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_CREATE_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_ONLINE_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_OFFLINE_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_UNIQUE_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_FULLTEXT_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_SPATIAL_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_ONLINE_UNIQUE_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_ONLINE_FULLTEXT_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_ONLINE_SPATIAL_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_OFFLINE_UNIQUE_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_OFFLINE_FULLTEXT_INDEX)
                || tokens.matches(MySqlStatementStartPhrases.STMT_CREATE_OFFLINE_SPATIAL_INDEX)) {
            return parseCreateIndexStatement(tokens, parentNode);
        } else if (isCreateViewStatement(tokens)) {
            return parseCreateViewStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_FUNCTION)) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_FUNCTION,
                                  parentNode,
                                  TYPE_CREATE_FUNCTION_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_PROCEDURE)) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_PROCEDURE,
                                  parentNode,
                                  TYPE_CREATE_PROCEDURE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SERVER)) {
            return parseStatement(tokens, MySqlStatementStartPhrases.STMT_CREATE_SERVER, parentNode,
                                  TYPE_CREATE_SERVER_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_TRIGGER)) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_TRIGGER,
                                  parentNode,
                                  TYPE_CREATE_TRIGGER_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_EVENT)) {
            return parseStatement(tokens, MySqlStatementStartPhrases.STMT_CREATE_EVENT, parentNode,
                                  TYPE_CREATE_EVENT_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_TABLESPACE)) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_TABLESPACE,
                                  parentNode,
                                  TYPE_CREATE_TABLESPACE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_DEFINER) || tokens.matches(STMT_CREATE_OR_REPLACE_DEFINER)) {
            return parseCreateDefinerStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_ALGORITHM) || tokens.matches(STMT_CREATE_OR_REPLACE_ALGORITHM)) {
            return parseCreateAlgorithmStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_SQL_SECURITY) || tokens.matches(STMT_CREATE_OR_REPLACE_SQL_SECURITY)) {
            return parseCreateSqlSecurityStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_TEMPORARY_TABLE)) {
            return parseCreateTableStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_DATABASE)) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_DATABASE,
                                  parentNode,
                                  MySql8DdlLexicon.TYPE_CREATE_DATABASE_STATEMENT);
        }

        return super.parseCreateStatement(tokens, parentNode);
    }

    boolean isCreateViewStatement(DdlTokenStream tokens) {
        return tokens.matches(StatementStartPhrases.STMT_CREATE_VIEW)
                || tokens.matches(StatementStartPhrases.STMT_CREATE_OR_REPLACE_VIEW)
                || tokens.matches("CREATE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "OR", "REPLACE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "DEFINER", "=", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "OR", "REPLACE", "DEFINER", "=", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "SQL", "SECURITY ", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "OR", "REPLACE", "SQL", "SECURITY ", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "DEFINER", "=",
                                  DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "OR", "REPLACE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "DEFINER", "=",
                                  DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "SQL", "SECURITY ",
                                  DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "OR", "REPLACE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "SQL",
                                  "SECURITY ", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "DEFINER", "=", DdlTokenStream.ANY_VALUE, "SQL", "SECURITY ",
                                  DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "OR", "REPLACE", "DEFINER", "=", DdlTokenStream.ANY_VALUE, "SQL",
                                  "SECURITY ", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "DEFINER", "=",
                                  DdlTokenStream.ANY_VALUE, "SQL", "SECURITY ", DdlTokenStream.ANY_VALUE, "VIEW")
                || tokens.matches("CREATE", "OR", "REPLACE", "ALGORITHM", "=", DdlTokenStream.ANY_VALUE, "DEFINER", "=",
                                  DdlTokenStream.ANY_VALUE, "SQL", "SECURITY ", DdlTokenStream.ANY_VALUE, "VIEW");
    }

    private AstNode parseCreateIndexStatement(DdlTokenStream tokens, AstNode parentNode) {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);

        tokens.consume(CREATE); // CREATE

        String online = null;
        if (tokens.canConsume("OFFLINE")) {
            online = "OFFLINE";
        } else if (tokens.canConsume("ONLINE")) {
            online = "ONLINE";
        }

        String type = null;
        if (tokens.canConsume("UNIQUE")) {
            type = "UNIQUE";
        } else if (tokens.canConsume("FULLTEXT")) {
            type = "FULLTEXT";
        } else if (tokens.canConsume("SPATIAL")) {
            type = "SPATIAL";
        }
        tokens.consume("INDEX");

        String indexName = parseName(tokens);
        AstNode indexNode = nodeFactory().node(indexName, parentNode, TYPE_CREATE_INDEX_STATEMENT);

        if (online != null) {
            indexNode.setProperty(MySql8DdlLexicon.ONLINE, online);
        }
        if (type != null) {
            indexNode.setProperty(MySql8DdlLexicon.TYPE, type);
        }

        parseIndexType(tokens, indexNode);
        tokens.consume("ON");

        String tableName = parseName(tokens);
        indexNode.setProperty(MySql8DdlLexicon.TABLE_NAME, tableName);

        parseIndexColumnNameList(tokens, indexNode, TYPE_COLUMN_REFERENCE);

        parseIndexOption(tokens, indexNode);

        markEndOfStatement(tokens, indexNode);
        return indexNode;
    }

    @Override
    protected AstNode parseCreateTableStatement(DdlTokenStream tokens,
                                                AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);

        tokens.consume(CREATE); // CREATE
        String temporaryValue = null;

        if (tokens.canConsume("TEMPORARY")) {
            temporaryValue = "TEMPORARY";
        }
        tokens.consume(TABLE);
        tokens.canConsume("IF", "NOT", "EXISTS");

        String tableName = parseName(tokens);
        AstNode tableNode = nodeFactory().node(tableName, parentNode, TYPE_CREATE_TABLE_STATEMENT);

        if (temporaryValue != null) {
            tableNode.setProperty(TEMPORARY, temporaryValue);
        }

        String likeValue = null;
        if (tokens.matches("LIKE")) {
            likeValue = parseLikeStatement(tokens);
        } else if (tokens.matches(DdlConstants.L_PAREN, "LIKE")) {
            String likeStatement = parseContentBetweenParens(tokens);

            DdlTokenStream likeTokens = new DdlTokenStream(likeStatement, DdlTokenStream.ddlTokenizer(true), false);
            likeTokens.start();
            likeValue = parseLikeStatement(likeTokens);
        }

        if (likeValue != null) {
            tableNode.setProperty(TABLE_LIKE, likeValue);
        } else {
            // System.out.println("  >> PARSING CREATE TABLE >>  Name = " + tableName);

            parseColumnsAndConstraints(tokens, tableNode);

            parseCreateTableOptions(tokens, tableNode);
        }

        markEndOfStatement(tokens, tableNode);

        return tableNode;
    }

    private String parseLikeStatement(DdlTokenStream tokens) {
        String likeValue;
        tokens.consume("LIKE");
        likeValue = parseName(tokens);
        return likeValue;
    }

    @Override
    protected void parseCreateTableOptions(DdlTokenStream tokens, AstNode tableNode) {
        //        table_options:
        //            table_option [[,] table_option] ...
        //
        //        table_option:
        //            ENGINE [=] engine_name
        //          | AUTO_INCREMENT [=] value
        //          | AVG_ROW_LENGTH [=] value
        //          | [DEFAULT] CHARACTER SET [=] charset_name
        //          | CHECKSUM [=] {0 | 1}
        //          | [DEFAULT] COLLATE [=] collation_name
        //          | COMMENT [=] 'string'
        //          | CONNECTION [=] 'connect_string'
        //          | DATA DIRECTORY [=] 'absolute path to directory'
        //          | DELAY_KEY_WRITE [=] {0 | 1}
        //          | INDEX DIRECTORY [=] 'absolute path to directory'
        //          | INSERT_METHOD [=] { NO | FIRST | LAST }
        //          | KEY_BLOCK_SIZE [=] value
        //          | MAX_ROWS [=] value
        //          | MIN_ROWS [=] value
        //          | PACK_KEYS [=] {0 | 1 | DEFAULT}
        //          | PASSWORD [=] 'string'
        //          | ROW_FORMAT [=] {DEFAULT|DYNAMIC|FIXED|COMPRESSED|REDUNDANT|COMPACT}
        //          | TABLESPACE tablespace_name [STORAGE {DISK|MEMORY|DEFAULT}]
        //          | UNION [=] (tbl_name[,tbl_name]...)

        //        140116;140101;3;'Engine';null;1
        //        140117;140101;1;'Character set';null;2
        //        140118;140101;1;'Collate';null;3
        //        140119;140101;1;'Comment';null;4
        //        140120;140101;1;'Tablespace';null;5

        boolean lastMatched = true;
        while (tokens.hasNext() && !isTerminator(tokens) && lastMatched) {
            lastMatched = parseTableOption(tokens, tableNode);
            tokens.canConsume(DdlConstants.COMMA);
        }
        parseUntilTerminator(tokens);
    }

    private boolean parseTableOption(DdlTokenStream tokens, AstNode tableNode) {
        boolean lastMatched = true;

        if (tokens.canConsume("ENGINE")) {
            tokens.canConsume("=");
            String engineName = parseName(tokens);

            tableNode.setProperty(MySql8DdlLexicon.TABLE_ENGINE, engineName);
        } else if (tokens.canConsume("CHARACTER", "SET")
                || tokens.canConsume("DEFAULT", "CHARACTER", "SET")
                || tokens.canConsume("CHARSET")
                || tokens.canConsume("DEFAULT", "CHARSET")) {
            tokens.canConsume("=");
            String charsetName = parseName(tokens);
            tableNode.setProperty(MySql8DdlLexicon.TABLE_CHARACTER_SET, charsetName);

        } else if (tokens.canConsume("DEFAULT", "COLLATE")) {
            tokens.canConsume("=");
            String collationName = parseName(tokens);

            tableNode.setProperty(MySql8DdlLexicon.TABLE_COLLATE, collationName);
        } else if (tokens.canConsume("COLLATE")) {
            tokens.canConsume("=");
            String collationName = parseName(tokens);

            tableNode.setProperty(MySql8DdlLexicon.TABLE_COLLATE, collationName);
        } else if (tokens.canConsume("COMMENT")) {
            tokens.canConsume("=");
            String comment = tokens.consume();

            tableNode.setProperty(MySql8DdlLexicon.COMMENT, comment);
        } else if (tokens.canConsume("TABLESPACE")) {
            tokens.canConsume("=");
            String tablespaceName = parseName(tokens);

            tableNode.setProperty(MySql8DdlLexicon.TABLE_TABLESPACE, tablespaceName);
        } else if (tokens.canConsume("AUTO_INCREMENT")) {
            tokens.canConsume("=");
            String autoIncrementExpression = parseName(tokens);
            tableNode.setProperty(MySql8DdlLexicon.TABLE_AUTO_INCREMENT, autoIncrementExpression);
        } else if (tokens.canConsume("PARTITION", "BY")) {
            String partitionByExpression = parseUntilTerminator(tokens);
            tableNode.setProperty(MySql8DdlLexicon.TABLE_PARTITION_BY, partitionByExpression);
        } else {
            String rest = parseUntilCommaOrTerminator(tokens);
            if (rest == null || "".equals(rest)) {
                lastMatched = false;
            }
        }
        return lastMatched;
    }

    //    | [CONSTRAINT [symbol]] PRIMARY KEY [index_type] (index_col_name,...)
    //      [index_option] ...
    //    | {INDEX|KEY} [index_name] [index_type] (index_col_name,...)
    //      [index_option] ...
    //    | [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY]
    //      [index_name] [index_type] (index_col_name,...)
    //      [index_option] ...
    //    | {FULLTEXT|SPATIAL} [INDEX|KEY] [index_name] (index_col_name,...)
    //      [index_option] ...
    //    | [CONSTRAINT [symbol]] FOREIGN KEY
    //     [index_name] (index_col_name,...) reference_definition
    //    | CHECK (expr)
    @Override
    protected boolean isTableConstraint(DdlTokenStream tokens) throws ParsingException {

        if (tokens.matches("FULLTEXT")
                || tokens.matches("SPATIAL")
                || tokens.matches("INDEX")
                || tokens.matches("KEY")) {
            return true;
        }

        if (tokens.matches("CONSTRAINT", "UNIQUE")
                || tokens.matches("CONSTRAINT", "PRIMARY", "KEY")
                || tokens.matches("CONSTRAINT", "FOREIGN", "KEY")
                || tokens.matches("CONSTRAINT", "CHECK")) {
            return true;
        }

        return super.isTableConstraint(tokens);
    }

    @Override
    protected void parseTableConstraint(DdlTokenStream tokens,
                                        AstNode tableNode,
                                        boolean isAlterTable) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        String mixinType = isAlterTable ? TYPE_ADD_TABLE_CONSTRAINT_DEFINITION : TYPE_TABLE_CONSTRAINT;

        if (tokens.matches("FULLTEXT")
                || tokens.matches("SPATIAL")
                || tokens.matches("INDEX")
                || tokens.matches("KEY")) {
            parseIndex(tokens, tableNode);
        }

        consumeComment(tokens);

        if (tokens.matches("PRIMARY", "KEY")
                || tokens.matches("CONSTRAINT", "PRIMARY", "KEY")
                || tokens.matches("FOREIGN", "KEY")
                || tokens.matches("CONSTRAINT", "FOREIGN", "KEY")
                || tokens.matches("UNIQUE")
                || tokens.matches("CONSTRAINT", "UNIQUE")
                || tokens.matches("CHECK")) {

            // This is the case where the PK/FK/UK is NOT NAMED
            if (tokens.matches("UNIQUE") || (tokens.matches("CONSTRAINT", "UNIQUE"))) {
                tokens.canConsume("CONSTRAINT");
                String uc_name = "UC_1"; // UNIQUE CONSTRAINT NAME
                tokens.consume("UNIQUE"); // UNIQUE
                tokens.canConsume("KEY");
                tokens.canConsume("INDEX");

                if (!(tokens.matches(L_PAREN) || tokens.matches("USING"))) {
                    uc_name = parseName(tokens);
                }
                AstNode constraintNode = nodeFactory().node(uc_name, tableNode, mixinType);
                constraintNode.setProperty(CONSTRAINT_TYPE, UNIQUE);

                parseIndexType(tokens, constraintNode);
                parseIndexColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

                parseConstraintAttributes(tokens, constraintNode);
                parseIndexOption(tokens, constraintNode);

                consumeComment(tokens);
            } else if (tokens.matches("PRIMARY", "KEY") || (tokens.matches("CONSTRAINT", "PRIMARY", "KEY"))) {
                tokens.canConsume("CONSTRAINT");
                String pk_name = "PK_1"; // PRIMARY KEY NAME
                tokens.consume("PRIMARY", "KEY"); // PRIMARY KEY

                AstNode constraintNode = nodeFactory().node(pk_name, tableNode, mixinType);
                constraintNode.setProperty(CONSTRAINT_TYPE, PRIMARY_KEY);

                parseIndexType(tokens, constraintNode);

                // CONSUME COLUMNS
                parseIndexColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

                parseConstraintAttributes(tokens, constraintNode);
                parseIndexOption(tokens, constraintNode);

                consumeComment(tokens);
            } else if (tokens.matches("FOREIGN", "KEY") || (tokens.matches("CONSTRAINT", "FOREIGN", "KEY"))) {
                tokens.canConsume("CONSTRAINT");

                String fk_name = "FK_1"; // FOREIGN KEY NAME
                tokens.consume("FOREIGN", "KEY"); // FOREIGN KEY

                if (!tokens.matches(L_PAREN)) {
                    // Assume the FK is Named here
                    fk_name = tokens.consume();
                }

                AstNode constraintNode = nodeFactory().node(fk_name, tableNode, mixinType);
                constraintNode.setProperty(CONSTRAINT_TYPE, FOREIGN_KEY);

                parseIndexColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

                // Parse the references to table and columns
                tokens.consume("REFERENCES");
                parseReferences(tokens, constraintNode);

                parseConstraintAttributes(tokens, constraintNode);

                consumeComment(tokens);
            } else if (tokens.matches("CHECK")) {
                // CHECK (char_length(zipcode) = 5);
                String ck_name = "CHECK_1";
                tokens.consume("CHECK");

                AstNode constraintNode = nodeFactory().node(ck_name, tableNode, mixinType);
                constraintNode.setProperty(CONSTRAINT_TYPE, CHECK);

                String clause = consumeParenBoundedTokens(tokens, true);
                constraintNode.setProperty(CHECK_SEARCH_CONDITION, clause);

                parseConstraintAttributes(tokens, constraintNode);
            }
        } else if (tokens.matches("CONSTRAINT", DdlTokenStream.ANY_VALUE, "UNIQUE")) {
            // CONSTRAINT P_KEY_2a UNIQUE (PERMISSIONUID)
            tokens.consume(); // CONSTRAINT
            String uc_name = parseName(tokens); // UNIQUE CONSTRAINT NAME
            tokens.consume("UNIQUE"); // UNIQUE

            tokens.canConsume("INDEX");
            tokens.canConsume("KEY");

            if (!(tokens.matches(L_PAREN) || tokens.matches("USING"))) {
                uc_name = tokens.consume();
            }

            AstNode constraintNode = nodeFactory().node(uc_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, UNIQUE);

            parseIndexType(tokens, constraintNode);

            // CONSUME COLUMNS
            parseIndexColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

            parseConstraintAttributes(tokens, constraintNode);
            parseIndexOption(tokens, constraintNode);

            consumeComment(tokens);
        } else if (tokens.matches("CONSTRAINT", DdlTokenStream.ANY_VALUE, "PRIMARY", "KEY")) {
            // CONSTRAINT U_KEY_2a PRIMARY KEY (PERMISSIONUID)
            tokens.consume("CONSTRAINT"); // CONSTRAINT
            String pk_name = parseName(tokens); // PRIMARY KEY NAME
            tokens.consume("PRIMARY", "KEY"); // PRIMARY KEY

            AstNode constraintNode = nodeFactory().node(pk_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, PRIMARY_KEY);

            parseIndexType(tokens, constraintNode);

            parseIndexColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

            parseConstraintAttributes(tokens, constraintNode);
            parseIndexOption(tokens, constraintNode);

            consumeComment(tokens);

        } else if (tokens.matches("CONSTRAINT", DdlTokenStream.ANY_VALUE, "FOREIGN", "KEY")) {
            // CONSTRAINT F_KEY_2a FOREIGN KEY (PERMISSIONUID)
            tokens.consume("CONSTRAINT"); // CONSTRAINT
            String fk_name = parseName(tokens); // FOREIGN KEY NAME
            tokens.consume("FOREIGN", "KEY"); // FOREIGN KEY

            AstNode constraintNode = nodeFactory().node(fk_name, tableNode, mixinType);

            constraintNode.setProperty(CONSTRAINT_TYPE, FOREIGN_KEY);

            if (!tokens.matches(L_PAREN)) {
                String indexName = parseName(tokens);
            }

            // CONSUME COLUMNS
            parseIndexColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

            // Parse the references to table and columns
            tokens.consume("REFERENCES");
            parseReferences(tokens, constraintNode);

            parseConstraintAttributes(tokens, constraintNode);

            consumeComment(tokens);

        } else if (tokens.matches("CONSTRAINT", DdlTokenStream.ANY_VALUE, "CHECK")) {
            // CONSTRAINT zipchk CHECK (char_length(zipcode) = 5);
            tokens.consume("CONSTRAINT"); // CONSTRAINT
            String ck_name = parseName(tokens); // NAME
            tokens.consume("CHECK"); // CHECK

            AstNode constraintNode = nodeFactory().node(ck_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, CHECK);

            String clause = consumeParenBoundedTokens(tokens, true);
            constraintNode.setProperty(CHECK_SEARCH_CONDITION, clause);

            parseConstraintAttributes(tokens, constraintNode);
        }

        super.parseTableConstraint(tokens, tableNode, isAlterTable);
    }

    protected void parseIndexColumnNameList(DdlTokenStream tokens,
                                            AstNode parentNode,
                                            String referenceType) {
        if (tokens.matches(L_PAREN)) {
            tokens.consume(L_PAREN);

            while (true) {
                parseIndexColumn(tokens, parentNode, referenceType);

                if (!tokens.canConsume(COMMA)) {
                    break;
                }
            }

            tokens.consume(R_PAREN);
        }
    }

    private void parseIndexColumn(DdlTokenStream tokens, AstNode parentNode, String referenceType) {
        String columnName = parseName(tokens);
        AstNode indexColumnNode = nodeFactory().node(columnName, parentNode, referenceType);

        if (tokens.matches(L_PAREN)) {
            parseContentBetweenParens(tokens);
        }
        if (tokens.canConsume("ASC")) {
            indexColumnNode.setProperty(MySql8DdlLexicon.INDEX_ORDER, "ASC");
        } else if (tokens.canConsume("DESC")) {
            indexColumnNode.setProperty(MySql8DdlLexicon.INDEX_ORDER, "DESC");
        }
    }

    private void parseIndex(DdlTokenStream tokens, AstNode tableNode) {
        //    | [CONSTRAINT [symbol]] PRIMARY KEY [index_type] (index_col_name,...)
        //        [index_option] ...
        //    | {INDEX|KEY} [index_name] [index_type] (index_col_name,...)
        //        [index_option] ...
        //    | [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY]
        //        [index_name] [index_type] (index_col_name,...)
        //        [index_option] ...
        //    | {FULLTEXT|SPATIAL} [INDEX|KEY] [index_name] (index_col_name,...)
        //        [index_option] ...
        //    | [CONSTRAINT [symbol]] FOREIGN KEY
        //        [index_name] (index_col_name,...) reference_definition
        //    | CHECK (expr)

        if (tokens.matches("FULLTEXT", "INDEX")
                || tokens.matches("SPATIAL", "INDEX")
                || tokens.matches("FULLTEXT", "KEY")
                || tokens.matches("SPATIAL", "KEY")) {

            String fullText;
            if (tokens.canConsume("FULLTEXT")) {
                fullText = "FULLTEXT";
            } else {
                tokens.consume("SPATIAL");
                fullText = "SPATIAL";
            }

            tokens.canConsume("INDEX");
            tokens.canConsume("KEY");

            AstNode indexNode;
            if (!tokens.matches(L_PAREN)) {
                String indexName = parseName(tokens);
                indexNode = nodeFactory().node(indexName, tableNode.getParent(), TYPE_CREATE_INDEX_STATEMENT);
            } else {
                indexNode = nodeFactory().node("IDX", tableNode.getParent(), TYPE_CREATE_INDEX_STATEMENT);
            }

            indexNode.setProperty(MySql8DdlLexicon.TABLE_NAME, tableNode.getName());
            indexNode.setProperty(MySql8DdlLexicon.TYPE, fullText);

            parseIndexColumnNameList(tokens, indexNode, TYPE_COLUMN_REFERENCE);
            parseIndexOption(tokens, indexNode);

        } else {
            tokens.canConsume("INDEX");
            tokens.canConsume("KEY");

            AstNode indexNode;
            if (!(tokens.matches(L_PAREN) || tokens.matches("USING"))) {
                String indexName = parseName(tokens);
                indexNode = nodeFactory().node(indexName, tableNode.getParent(), TYPE_CREATE_INDEX_STATEMENT);
            } else {
                indexNode = nodeFactory().node("IDX", tableNode.getParent(), TYPE_CREATE_INDEX_STATEMENT);
            }

            indexNode.setProperty(MySql8DdlLexicon.TABLE_NAME, tableNode.getName());

            parseIndexType(tokens, indexNode);

            parseIndexColumnNameList(tokens, indexNode, TYPE_COLUMN_REFERENCE);
            parseIndexOption(tokens, indexNode);
        }

        consumeComment(tokens);
    }

    private void parseIndexType(DdlTokenStream tokens, AstNode indexNode) {
        // USING {BTREE | HASH}
        if (tokens.canConsume("USING")) {
            if (tokens.canConsume("BTREE")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_TYPE, "BTREE");
            } else {
                tokens.consume("HASH");
                indexNode.setProperty(MySql8DdlLexicon.INDEX_TYPE, "HASH");
            }
        }
    }

    private void parseIndexOption(DdlTokenStream tokens, AstNode indexNode) {
        //        index_option:
        //            KEY_BLOCK_SIZE [=] value
        //          | index_type
        //          | WITH PARSER parser_name
        //          | COMMENT 'string'
        if (tokens.canConsume("KEY_BLOCK_SIZE")) {
            tokens.canConsume("=");
            String keyBlockSize = tokens.consume();
            indexNode.setProperty(MySql8DdlLexicon.KEY_BLOCK_SIZE, keyBlockSize);
        } else if (tokens.matches("USING")) {
            parseIndexType(tokens, indexNode);
        } else if (tokens.canConsume("WITH", "PARSER")) {
            String parserName = tokens.consume();
            indexNode.setProperty(MySql8DdlLexicon.WITH_PARSER, parserName);
        } else if (tokens.canConsume("COMMENT")) {
            String comment = tokens.consume();
            indexNode.setProperty(MySql8DdlLexicon.COMMENT, comment);
        }

        parseIndexAlgorithm(tokens, indexNode);
        parseIndexLock(tokens, indexNode);
    }

    private void parseIndexAlgorithm(DdlTokenStream tokens, AstNode indexNode) {
        // ALGORITHM [=] {DEFAULT | INPLACE | COPY}
        if (tokens.canConsume("ALGORITHM")) {
            tokens.canConsume("=");
            if (tokens.canConsume("DEFAULT")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_ALGORITHM, "DEFAULT");
            } else if (tokens.canConsume("INPLACE")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_ALGORITHM, "INPLACE");
            } else if (tokens.canConsume("COPY")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_ALGORITHM, "COPY");
            } else {
                String value = tokens.consume();
                throw new ParsingException(tokens.nextPosition(), "Unexpected ALGORITHM type: " + value);
            }
        }
    }

    private void parseIndexLock(DdlTokenStream tokens, AstNode indexNode) {
        // LOCK [=] {DEFAULT | NONE | SHARED | EXCLUSIVE}
        if (tokens.canConsume("LOCK")) {
            tokens.canConsume("=");
            if (tokens.canConsume("DEFAULT")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_LOCK, "DEFAULT");
            } else if (tokens.canConsume("NONE")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_LOCK, "NONE");
            } else if (tokens.canConsume("SHARED")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_LOCK, "SHARED");
            } else if (tokens.canConsume("EXCLUSIVE")) {
                indexNode.setProperty(MySql8DdlLexicon.INDEX_LOCK, "EXCLUSIVE");
            } else {
                String value = tokens.consume();
                throw new ParsingException(tokens.nextPosition(), "Unexpected LOCK type: " + value);
            }
        }
    }

    @Override
    protected void parseColumnDefinition(DdlTokenStream tokens,
                                         AstNode tableNode,
                                         boolean isAlterTable) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        String columnName = parseName(tokens);
        DataType datatype = getDatatypeParser().parse(tokens);

        AstNode columnNode = nodeFactory().node(columnName, tableNode, TYPE_COLUMN_DEFINITION);

        getDatatypeParser().setPropertiesOnNode(columnNode, datatype);

        // Now clauses and constraints can be defined in any order, so we need to keep parsing until we get to a comma
        StringBuffer unusedTokensSB = new StringBuffer();

        //        column_definition:
        //            data_type [NOT NULL | NULL] [DEFAULT default_value]
        //              [AUTO_INCREMENT] [UNIQUE [KEY] | [PRIMARY] KEY]
        //              [COMMENT 'string']
        //              [COLUMN_FORMAT {FIXED|DYNAMIC|DEFAULT}]
        //              [STORAGE {DISK|MEMORY|DEFAULT}]
        //              [reference_definition]

        while (tokens.hasNext() && !tokens.matches(COMMA) && !tokens.matches(SEMICOLON) &&
                !tokens.matches("FIRST") && !tokens.matches("AFTER")) {

            if (parseGeneratedAsClause(tokens, columnNode)) {
                // GENERATED COLUMN
                boolean parsedUniqueKeyClause = parseUniqueKeyClause(tokens, columnNode);
                boolean parsedCommentClause = parseCommentClause(tokens, columnNode);
                boolean parsedNullClause = parseNullClause(tokens, columnNode);
                boolean parsedPrimaryKeyClause = parsePrimaryKeyClause(tokens, columnNode);

                if (!parsedUniqueKeyClause
                        && !parsedCommentClause
                        && !parsedNullClause
                        && !parsedPrimaryKeyClause) {
                    // THIS IS AN ERROR. NOTHING FOUND.
                    // NEED TO absorb tokens
                    unusedTokensSB.append(SPACE).append(tokens.consume());
                }

                tokens.canConsume(DdlTokenizer.COMMENT);
            } else {
                // NORMAL COLUMN
                boolean parsedNullClause = parseNullClause(tokens, columnNode);
                boolean parsedDefaultClause = parseDefaultClause(tokens, columnNode);
                boolean parsedAutoIncrementClause = parseAutoIncrementClause(tokens, columnNode);
                boolean parsedKeyClause = parseKeyClause(tokens, columnNode);
                boolean parsedCommentClause = parseCommentClause(tokens, columnNode);
                boolean parsedColumnFormatClause = parseColumnFormatClause(tokens, columnNode, unusedTokensSB);
                boolean parsedStorageClause = parseStorageClause(tokens, columnNode, unusedTokensSB);
                boolean parsedReferenceClause = parseReferenceClause(tokens, columnNode);
                boolean parsedZerofillClause = parseZerofillClause(tokens, columnNode);
                boolean parsedUnsignedClause = parseUnsignedClause(tokens, columnNode);

                if (!parsedNullClause
                        && !parsedDefaultClause
                        && !parsedAutoIncrementClause
                        && !parsedKeyClause
                        && !parsedCommentClause
                        && !parsedColumnFormatClause
                        && !parsedStorageClause
                        && !parsedReferenceClause
                        && !parsedZerofillClause
                        && !parsedUnsignedClause) {
                    // THIS IS AN ERROR. NOTHING FOUND.
                    // NEED TO absorb tokens
                    unusedTokensSB.append(SPACE).append(tokens.consume());
                }

                tokens.canConsume(DdlTokenizer.COMMENT);
            }
        }

        if (unusedTokensSB.length() > 0) {
            String msg = DdlSequencerI18n.unusedTokensParsingColumnDefinition.text(tableNode.getName());
            DdlParserProblem problem = new DdlParserProblem(Problems.WARNING, Position.EMPTY_CONTENT_POSITION, msg);
            problem.setUnusedSource(unusedTokensSB.toString());
            addProblem(problem, tableNode);
        }
    }

    private boolean parseGeneratedAsClause(DdlTokenStream tokens, AstNode columnNode) {
        if (!tokens.canConsume("GENERATED", "ALWAYS", "AS") && !tokens.canConsume("AS")) {
            return false;
        }

        tokens.consume("(");
        String generatedAs = parseUntilCustomTokenOrTerminator(tokens, ")");
        columnNode.setProperty(MySql8DdlLexicon.GENERATED_AS, generatedAs);
        tokens.consume(")");

        if (tokens.canConsume("STORED")) {
            columnNode.setProperty(MySql8DdlLexicon.GENERATED_VALUE_STORAGE, "STORED");
        } else {
            tokens.canConsume("VIRTUAL");
            columnNode.setProperty(MySql8DdlLexicon.GENERATED_VALUE_STORAGE, "VIRTUAL");
        }

        return true;
    }

    private boolean parseReferenceClause(DdlTokenStream tokens, AstNode columnNode) {
        //        REFERENCES tbl_name (index_col_name,...)
        //        [MATCH FULL | MATCH PARTIAL | MATCH SIMPLE]
        //        [ON DELETE reference_option]
        //        [ON UPDATE reference_option]

        if (tokens.canConsume("REFERENCES")) {
            AstNode constraintNode = nodeFactory().node("FK_1", columnNode.getParent(), TYPE_TABLE_CONSTRAINT);
            constraintNode.setProperty(CONSTRAINT_TYPE, FOREIGN_KEY);
            nodeFactory().node(columnNode.getName(), constraintNode, TYPE_COLUMN_REFERENCE);
            parseReferences(tokens, constraintNode);

            return true;
        }

        return false;
    }

    @Override
    protected void parseReferences(DdlTokenStream tokens,
                                   AstNode constraintNode) throws ParsingException {
        assert tokens != null;
        assert constraintNode != null;

        String tableName = parseName(tokens);

        nodeFactory().node(tableName, constraintNode, TYPE_TABLE_REFERENCE);

        parseIndexColumnNameList(tokens, constraintNode, TYPE_FK_COLUMN_REFERENCE);

        if (tokens.canConsume("MATCH", "FULL")) {
            constraintNode.setProperty(MySql8DdlLexicon.MATCH, "FULL");
        } else if (tokens.canConsume("MATCH", "PARTIAL")) {
            constraintNode.setProperty(MySql8DdlLexicon.MATCH, "PARTIAL");
        } else if (tokens.canConsume("MATCH", "SIMPLE")) {
            constraintNode.setProperty(MySql8DdlLexicon.MATCH, "SIMPLE");
        }

        parseReferentialActions(tokens, constraintNode);
    }

    @Override
    protected String getReferentialActionType(DdlTokenStream tokens) {
        if (tokens.canConsume("SET NULL")) {
            return MySql8DdlLexicon.SET_NULL_ACTION;
        }
        if (tokens.canConsume("NO", "ACTION")) {
            return MySql8DdlLexicon.NO_ACTION;
        }
        return super.getReferentialActionType(tokens);
    }

    private boolean parseStorageClause(DdlTokenStream tokens, AstNode columnNode, StringBuffer unusedTokensSB) {
        if (tokens.canConsume("STORAGE")) {
            if (tokens.canConsume("DISK")) {
                columnNode.setProperty(COLUMN_STORAGE, "DISK");
            } else if (tokens.canConsume("MEMORY")) {
                columnNode.setProperty(COLUMN_STORAGE, "MEMORY");
            } else if (tokens.canConsume("DEFAULT")) {
                columnNode.setProperty(COLUMN_STORAGE, "DEFAULT");
            } else {
                unusedTokensSB.append(SPACE).append(tokens.consume());
            }
            return true;
        }
        return false;
    }

    private boolean parseColumnFormatClause(DdlTokenStream tokens, AstNode columnNode, StringBuffer unusedTokensSB) {
        if (tokens.canConsume("COLUMN_FORMAT")) {
            if (tokens.canConsume("FIXED")) {
                columnNode.setProperty(COLUMN_FORMAT, "FIXED");
            } else if (tokens.canConsume("DYNAMIC")) {
                columnNode.setProperty(COLUMN_FORMAT, "DYNAMIC");
            } else if (tokens.canConsume("DEFAULT")) {
                columnNode.setProperty(COLUMN_FORMAT, "DEFAULT");
            } else {
                unusedTokensSB.append(SPACE).append(tokens.consume());
            }

            return true;
        }
        return false;
    }

    private boolean parseCommentClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("COMMENT")) {
            String comment = tokens.consume();

            columnNode.setProperty(COMMENT, comment);
            return true;
        }
        return false;
    }

    private boolean parseKeyClause(DdlTokenStream tokens, AstNode columnNode) {
        return parseUniqueKeyClause(tokens, columnNode) || parsePrimaryKeyClause(tokens, columnNode);
    }

    private boolean parseUniqueKeyClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("UNIQUE")) {
            tokens.canConsume("KEY");

            // Unique constraint for this particular column
            String uc_name = "UC_1"; // UNIQUE CONSTRAINT NAME

            AstNode constraintNode = nodeFactory().node(uc_name, columnNode.getParent(), TYPE_TABLE_CONSTRAINT);
            constraintNode.setProperty(CONSTRAINT_TYPE, UNIQUE);

            nodeFactory().node(columnNode.getName(), constraintNode, TYPE_COLUMN_REFERENCE);

            return true;
        }
        return false;
    }

    private boolean parsePrimaryKeyClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("PRIMARY", "KEY") || tokens.canConsume("KEY")) {
            String pk_name = "PK_1"; // PRIMARY KEY NAME

            AstNode constraintNode = nodeFactory().node(pk_name, columnNode.getParent(), TYPE_TABLE_CONSTRAINT);
            constraintNode.setProperty(CONSTRAINT_TYPE, PRIMARY_KEY);

            nodeFactory().node(columnNode.getName(), constraintNode, TYPE_COLUMN_REFERENCE);
            return true;
        }
        return false;
    }

    private boolean parseAutoIncrementClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("AUTO_INCREMENT")) {
            columnNode.setProperty(MySql8DdlLexicon.COLUMN_AUTO_INCREMENT, true);
            return true;
        }
        return false;
    }

    private boolean parseZerofillClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("ZEROFILL")) {
            columnNode.setProperty(MySql8DdlLexicon.COLUMN_ZEROFILL, true);
            return true;
        }
        return false;
    }

    private boolean parseUnsignedClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("UNSIGNED")) {
            columnNode.setProperty(MySql8DdlLexicon.COLUMN_UNSIGNED, true);
            return true;
        }
        return false;
    }

    private boolean parseNullClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("NULL")) {
            columnNode.setProperty(NULLABLE, "NULL");
            return true;
        } else if (tokens.canConsume("NOT", "NULL")) {
            columnNode.setProperty(NULLABLE, "NOT NULL");
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#parseAlterStatement(DdlTokenStream,
     * AstNode)
     */
    @Override
    protected AstNode parseAlterStatement(DdlTokenStream tokens,
                                          AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_ALTER_ALGORITHM)) {
            return parseStatement(tokens, STMT_ALTER_ALGORITHM, parentNode, TYPE_ALTER_ALGORITHM_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_DATABASE)) {
            return parseStatement(tokens, STMT_ALTER_DATABASE, parentNode, TYPE_ALTER_DATABASE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_DEFINER)) {
            return parseStatement(tokens, STMT_ALTER_DEFINER, parentNode, TYPE_ALTER_DEFINER_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_EVENT)) {
            return parseStatement(tokens, STMT_ALTER_EVENT, parentNode, TYPE_ALTER_EVENT_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_FUNCTION)) {
            return parseStatement(tokens, STMT_ALTER_FUNCTION, parentNode, TYPE_ALTER_FUNCTION_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_LOGFILE_GROUP)) {
            return parseStatement(tokens, STMT_ALTER_LOGFILE_GROUP, parentNode, TYPE_ALTER_LOGFILE_GROUP_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_PROCEDURE)) {
            return parseStatement(tokens, STMT_ALTER_PROCEDURE, parentNode, TYPE_ALTER_PROCEDURE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SCHEMA)) {
            return parseStatement(tokens, STMT_ALTER_SCHEMA, parentNode, TYPE_ALTER_SCHEMA_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SERVER)) {
            return parseStatement(tokens, STMT_ALTER_SERVER, parentNode, TYPE_ALTER_SERVER_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_TABLESPACE)) {
            return parseStatement(tokens, STMT_ALTER_TABLESPACE, parentNode, TYPE_ALTER_TABLESPACE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SQL_SECURITY)) {
            return parseStatement(tokens, STMT_ALTER_SQL_SECURITY, parentNode, TYPE_ALTER_VIEW_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_IGNORE_TABLE) || tokens.matches(STMT_ALTER_ONLINE_TABLE)
                || tokens.matches(STMT_ALTER_ONLINE_IGNORE_TABLE) || tokens.matches(STMT_ALTER_OFFLINE_TABLE)
                || tokens.matches(STMT_ALTER_OFFLINE_IGNORE_TABLE)) {
            return parseAlterTableStatement(tokens, parentNode);
        }

        return super.parseAlterStatement(tokens, parentNode);
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#parseAlterTableStatement(DdlTokenStream,
     * AstNode)
     */
    @Override
    protected AstNode parseAlterTableStatement(DdlTokenStream tokens,
                                               AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;
        /*
         * 
        	alter_specification:
        	    table_options
         */

        markStartOfStatement(tokens);

        tokens.consume(ALTER);
        tokens.canConsume("ONLINE");
        tokens.canConsume("OFFLINE");
        tokens.canConsume("IGNORE");
        tokens.consume(TABLE);

        String tableName = parseName(tokens);

        AstNode alterTableNode = nodeFactory().node(tableName, parentNode, TYPE_ALTER_TABLE_STATEMENT);

        while (tokens.hasNext() && !isTerminator(tokens)) {
            if (tokens.canConsume("ADD")) {

                if (isTableConstraint(tokens)) {
                    parseTableConstraint(tokens, alterTableNode, true);
                } else if (tokens.canConsume("PARTITION")) {
                    parseUntilCommaOrTerminator(tokens);
                } else {
                    tokens.canConsume("COLUMN");

                    if (tokens.matches(L_PAREN)) {
                        String content = parseContentBetweenParens(tokens);

                        DdlTokenStream columnDefinitionsTokens = new DdlTokenStream(content,
                                                                                    DdlTokenStream.ddlTokenizer(true),
                                                                                    false);
                        columnDefinitionsTokens.start();

                        while (columnDefinitionsTokens.hasNext()) {
                            parseColumnDefinition(columnDefinitionsTokens, alterTableNode, true);
                            columnDefinitionsTokens.canConsume(COMMA);
                        }

                    } else {
                        parseColumnDefinition(tokens, alterTableNode, true);

                        List<AstNode> columnNodes = nodeFactory().getChildrenForType(alterTableNode,
                                                                                     TYPE_COLUMN_DEFINITION);

                        if (columnNodes.size() > 0) {
                            AstNode columnNode = columnNodes.get(columnNodes.size() - 1);
                            parseAlterColumnFirstOrAfterClause(tokens, columnNode);
                        }
                    }
                }
            } else if (tokens.canConsume("DROP")) {
                if (tokens.canConsume("PRIMARY", "KEY")) {
                    nodeFactory().node("DROP_PRIMARY_KEY", alterTableNode, MySql8DdlLexicon.TYPE_DROP_PRIMARY_KEY);
                } else if (tokens.canConsume("INDEX") || tokens.canConsume("KEY")) {
                    String indexName = parseName(tokens);
                    nodeFactory().node(indexName, alterTableNode, MySql8DdlLexicon.TYPE_DROP_INDEX);
                } else if (tokens.canConsume("FOREIGN", "KEY")) {
                    String fkName = parseName(tokens);
                    nodeFactory().node(fkName, alterTableNode, MySql8DdlLexicon.TYPE_DROP_FOREIGN_KEY);
                } else if (tokens.canConsume("PARTITION")) {
                    parseUntilCommaOrTerminator(tokens);
                } else {
                    tokens.canConsume("COLUMN");
                    String columnName = parseName(tokens);
                    nodeFactory().node(columnName, alterTableNode, TYPE_DROP_COLUMN_DEFINITION);
                }
            } else if (tokens.canConsume("ALTER")) {
                tokens.canConsume("COLUMN");
                String alterColumnName = parseName(tokens);
                AstNode columnNode = nodeFactory().node(alterColumnName, alterTableNode, TYPE_ALTER_COLUMN_DEFINITION);
                if (tokens.canConsume("SET")) {
                    parseDefaultClause(tokens, columnNode);
                } else if (tokens.canConsume("DROP", "DEFAULT")) {
                    columnNode.setProperty(DROP_BEHAVIOR, "DROP DEFAULT");
                }
            } else if (tokens.canConsume("CHANGE")) {
                tokens.canConsume("COLUMN");

                String oldName = parseName(tokens);
                parseColumnDefinition(tokens, alterTableNode, true);

                List<AstNode> columnNodes = nodeFactory().getChildrenForType(alterTableNode, TYPE_COLUMN_DEFINITION);
                if (columnNodes.size() > 0) {
                    AstNode columnNode = columnNodes.get(columnNodes.size() - 1);

                    nodeFactory().setType(columnNode, MySql8DdlLexicon.TYPE_CHANGE_COLUMN_DEFINITION);
                    columnNode.setProperty(MySql8DdlLexicon.OLD_NAME, oldName);
                    parseAlterColumnFirstOrAfterClause(tokens, columnNode);
                }

            } else if (tokens.canConsume("MODIFY")) {
                tokens.canConsume("COLUMN");

                parseColumnDefinition(tokens, alterTableNode, true);

                List<AstNode> columnNodes = nodeFactory().getChildrenForType(alterTableNode, TYPE_COLUMN_DEFINITION);
                if (columnNodes.size() > 0) {
                    AstNode columnNode = columnNodes.get(columnNodes.size() - 1);

                    nodeFactory().setType(columnNode, MySql8DdlLexicon.TYPE_MODIFY_COLUMN_DEFINITION);
                    parseAlterColumnFirstOrAfterClause(tokens, columnNode);
                }
            }
            if (tokens.canConsume("RENAME")) {
                tokens.canConsume("AS");
                tokens.canConsume("TO");

                String newTableName = parseName(tokens);
                nodeFactory().node(newTableName, alterTableNode, MySql8DdlLexicon.TYPE_RENAME_TABLE_STATEMENT);
            } else {
                parseTableOption(tokens, alterTableNode);
                parseUntilCommaOrTerminator(tokens); // COULD BE "NESTED TABLE xxxxxxxx" option clause
            }

            tokens.canConsume(COMMA);
        }

        markEndOfStatement(tokens, alterTableNode);
        return alterTableNode;
    }

    private void parseAlterColumnFirstOrAfterClause(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("FIRST")) {
            columnNode.setProperty(MySql8DdlLexicon.FIRST, true);
        } else if (tokens.canConsume("AFTER")) {
            String colName = parseName(tokens);
            columnNode.setProperty(MySql8DdlLexicon.AFTER, colName);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#parseCustomStatement(DdlTokenStream,
     * AstNode)
     */
    @Override
    protected AstNode parseCustomStatement(DdlTokenStream tokens,
                                           AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_RENAME_DATABASE)) {
            markStartOfStatement(tokens);

            // RENAME DATABASE db_name TO new_db_name;
            tokens.consume(STMT_RENAME_DATABASE);
            String oldName = parseName(tokens);
            tokens.consume("TO");
            AstNode node = nodeFactory().node(oldName, parentNode, TYPE_RENAME_DATABASE_STATEMENT);
            String newName = parseName(tokens);
            node.setProperty(NEW_NAME, newName);

            markEndOfStatement(tokens, node);
            return node;
        } else if (tokens.matches(STMT_RENAME_SCHEMA)) {
            markStartOfStatement(tokens);

            // RENAME SCHEMA schema_name TO new_schema_name;
            tokens.consume(STMT_RENAME_SCHEMA);
            String oldName = parseName(tokens);
            tokens.consume("TO");
            AstNode node = nodeFactory().node(oldName, parentNode, TYPE_RENAME_SCHEMA_STATEMENT);
            String newName = parseName(tokens);
            node.setProperty(NEW_NAME, newName);

            markEndOfStatement(tokens, node);
            return node;
        } else if (tokens.matches(STMT_RENAME_TABLE)) {
            markStartOfStatement(tokens);

            // RENAME TABLE old_table TO tmp_table,
            // new_table TO old_table,
            // tmp_table TO new_table;
            tokens.consume(STMT_RENAME_TABLE);

            String oldName = parseName(tokens);
            tokens.consume("TO");
            String newName = parseName(tokens);

            AstNode node = nodeFactory().node(oldName, parentNode, TYPE_RENAME_TABLE_STATEMENT);
            node.setProperty(NEW_NAME, newName);

            // IF NOT MULTIPLE RENAMES, FINISH AND RETURN
            if (!tokens.matches(COMMA)) {
                markEndOfStatement(tokens, node);
                return node;
            }

            // Assume multiple renames

            // Create list of nodes so we can re-set the expression of each to reflect ONE rename.
            List<AstNode> nodes = new ArrayList<AstNode>();
            nodes.add(node);

            while (tokens.matches(COMMA)) {
                tokens.consume(COMMA);
                oldName = parseName(tokens);
                tokens.consume("TO");
                newName = parseName(tokens);
                node = nodeFactory().node(oldName, parentNode, TYPE_RENAME_TABLE_STATEMENT);
                node.setProperty(NEW_NAME, newName);
                nodes.add(node);
            }

            markEndOfStatement(tokens, nodes.get(0));

            String originalExpression = (String) nodes.get(0).getProperty(DDL_EXPRESSION);
            Object startLineNumber = nodes.get(0).getProperty(DDL_START_LINE_NUMBER);
            Object startColumnNumber = nodes.get(0).getProperty(DDL_START_COLUMN_NUMBER);
            Object startCharIndex = nodes.get(0).getProperty(DDL_START_CHAR_INDEX);

            for (AstNode nextNode : nodes) {
                oldName = nextNode.getName();
                newName = (String) nextNode.getProperty(NEW_NAME);
                String express = "RENAME TABLE" + SPACE + oldName + SPACE + "TO" + SPACE + newName + SEMICOLON;
                nextNode.setProperty(DDL_EXPRESSION, express);
                nextNode.setProperty(DDL_ORIGINAL_EXPRESSION, originalExpression);
                nextNode.setProperty(DDL_START_LINE_NUMBER, startLineNumber);
                nextNode.setProperty(DDL_START_COLUMN_NUMBER, startColumnNumber);
                nextNode.setProperty(DDL_START_CHAR_INDEX, startCharIndex);
            }

            return nodes.get(0);
        }

        return super.parseCustomStatement(tokens, parentNode);
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#parseDropStatement(DdlTokenStream,
     * AstNode)
     */
    @Override
    protected AstNode parseDropStatement(DdlTokenStream tokens,
                                         AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_DROP_DATABASE)) {
            return parseStatement(tokens, STMT_DROP_DATABASE, parentNode, TYPE_DROP_DATABASE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_EVENT)) {
            return parseStatement(tokens, STMT_DROP_EVENT, parentNode, TYPE_DROP_EVENT_STATEMENT);
        } else if (tokens.matches(STMT_DROP_FUNCTION)) {
            return parseStatement(tokens, STMT_DROP_FUNCTION, parentNode, TYPE_DROP_FUNCTION_STATEMENT);
        } else if (tokens.matches(STMT_DROP_INDEX)) {
            return parseStatement(tokens, STMT_DROP_INDEX, parentNode, TYPE_DROP_INDEX_STATEMENT);
        } else if (tokens.matches(STMT_DROP_OFFLINE_INDEX)) {
            return parseStatement(tokens, STMT_DROP_OFFLINE_INDEX, parentNode, TYPE_DROP_INDEX_STATEMENT);
        } else if (tokens.matches(STMT_DROP_ONLINE_INDEX)) {
            return parseStatement(tokens, STMT_DROP_ONLINE_INDEX, parentNode, TYPE_DROP_INDEX_STATEMENT);
        } else if (tokens.matches(STMT_DROP_LOGFILE_GROUP)) {
            return parseStatement(tokens, STMT_DROP_LOGFILE_GROUP, parentNode, TYPE_DROP_LOGFILE_GROUP_STATEMENT);
        } else if (tokens.matches(STMT_DROP_PROCEDURE)) {
            return parseStatement(tokens, STMT_DROP_PROCEDURE, parentNode, TYPE_DROP_PROCEDURE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SERVER)) {
            return parseStatement(tokens, STMT_DROP_SERVER, parentNode, TYPE_DROP_SERVER_STATEMENT);
        } else if (tokens.matches(STMT_DROP_TABLESPACE)) {
            return parseStatement(tokens, STMT_DROP_TABLESPACE, parentNode, TYPE_DROP_TABLESPACE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_TRIGGER)) {
            return parseStatement(tokens, STMT_DROP_TRIGGER, parentNode, TYPE_DROP_TRIGGER_STATEMENT);
        } else if (tokens.matches(STMT_DROP_TABLE_IF_EXISTS)) {
            return parseStatement(tokens, STMT_DROP_TABLE_IF_EXISTS, parentNode, TYPE_DROP_TABLE_STATEMENT);
        }

        return super.parseDropStatement(tokens, parentNode);
    }

    private AstNode parseCreateSqlSecurityStatement(DdlTokenStream tokens, AstNode parentNode) {
        assert tokens != null;
        assert parentNode != null;
        //        CREATE
        //        [OR REPLACE]
        //        [ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
        //        [DEFINER = { user | CURRENT_USER }]
        //        [SQL SECURITY { DEFINER | INVOKER }]
        //        VIEW view_name [(column_list)]
        //        AS select_statement
        //        [WITH [CASCADED | LOCAL] CHECK OPTION]

        markStartOfStatement(tokens);

        tokens.consume("CREATE");

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }
        if (tokens.canConsume("SQL", "SECURITY")) {
            tokens.consume();
        }

        if (!tokens.canConsume("VIEW")) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_SQL_SECURITY,
                                  parentNode,
                                  MySql8DdlLexicon.TYPE_CREATE_SQL_SECURITY_STATEMENT);
        }

        return parseCreateViewHelper(tokens, orReplaceClause, parentNode);
    }

    private AstNode parseCreateAlgorithmStatement(DdlTokenStream tokens, AstNode parentNode) {
        assert tokens != null;
        assert parentNode != null;
        //        CREATE
        //        [OR REPLACE]
        //        [ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
        //        [DEFINER = { user | CURRENT_USER }]
        //        [SQL SECURITY { DEFINER | INVOKER }]
        //        VIEW view_name [(column_list)]
        //        AS select_statement
        //        [WITH [CASCADED | LOCAL] CHECK OPTION]

        markStartOfStatement(tokens);

        tokens.consume("CREATE");

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }
        if (tokens.canConsume("ALGORITHM")) {
            tokens.consume("=");
            tokens.consume();
        }
        if (tokens.canConsume("DEFINER")) {
            tokens.consume("=");
            tokens.consume();
        }
        if (tokens.canConsume("SQL", "SECURITY")) {
            tokens.consume();
        }

        if (!tokens.canConsume("VIEW")) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_ALGORITHM,
                                  parentNode,
                                  MySql8DdlLexicon.TYPE_CREATE_ALGORITHM_STATEMENT);
        }

        return parseCreateViewHelper(tokens, orReplaceClause, parentNode);
    }

    private AstNode parseCreateDefinerStatement(DdlTokenStream tokens, AstNode parentNode) {
        assert tokens != null;
        assert parentNode != null;
        //        CREATE
        //        [OR REPLACE]
        //        [ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
        //        [DEFINER = { user | CURRENT_USER }]
        //        [SQL SECURITY { DEFINER | INVOKER }]
        //        VIEW view_name [(column_list)]
        //        AS select_statement
        //        [WITH [CASCADED | LOCAL] CHECK OPTION]

        markStartOfStatement(tokens);

        tokens.consume("CREATE");

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }
        if (tokens.canConsume("DEFINER")) {
            tokens.consume("=");
            tokens.consume();
        }
        if (tokens.canConsume("SQL", "SECURITY")) {
            tokens.consume();
        }

        if (!tokens.canConsume("VIEW")) {
            return parseStatement(tokens,
                                  MySqlStatementStartPhrases.STMT_CREATE_DEFINER,
                                  parentNode,
                                  TYPE_CREATE_DEFINER_STATEMENT);
        }

        return parseCreateViewHelper(tokens, orReplaceClause, parentNode);
    }

    private AstNode parseCreateViewHelper(DdlTokenStream tokens, boolean orReplaceClause, AstNode parentNode) {
        String name = parseName(tokens);

        AstNode createViewNode = nodeFactory().node(name, parentNode, TYPE_CREATE_VIEW_STATEMENT);
        createViewNode.setProperty(StandardDdlLexicon.OR_REPLACE_CLAUSE, orReplaceClause);

        // CONSUME COLUMNS
        parseColumnNameList(tokens, createViewNode, TYPE_COLUMN_REFERENCE);

        tokens.consume("AS");

        String queryExpression = parseUntilTerminatorOrWithCheck(tokens);

        createViewNode.setProperty(CREATE_VIEW_QUERY_EXPRESSION, queryExpression);

        if (tokens.canConsume("WITH")) {
            tokens.canConsume("CASCADED");
            tokens.canConsume("LOCAL");
            tokens.consume("CHECK");
            tokens.consume("OPTION");
        }

        markEndOfStatement(tokens, createViewNode);

        return createViewNode;
    }

    @Override
    protected AstNode parseCreateViewStatement(DdlTokenStream tokens,
                                               AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;
        //        CREATE
        //        [OR REPLACE]
        //        [ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
        //        [DEFINER = { user | CURRENT_USER }]
        //        [SQL SECURITY { DEFINER | INVOKER }]
        //        VIEW view_name [(column_list)]
        //        AS select_statement
        //        [WITH [CASCADED | LOCAL] CHECK OPTION]

        markStartOfStatement(tokens);

        tokens.consume("CREATE");

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }

        if (tokens.canConsume("ALGORITHM")) {
            tokens.consume("=");
            tokens.consume();
        }
        if (tokens.canConsume("DEFINER")) {
            tokens.consume("=");
            tokens.consume();
        }
        if (tokens.canConsume("SQL", "SECURITY")) {
            tokens.consume();
        }

        tokens.consume("VIEW");

        return parseCreateViewHelper(tokens, orReplaceClause, parentNode);
    }

    protected String parseUntilTerminatorOrWithCheck(DdlTokenStream tokens) throws ParsingException {
        final StringBuffer sb = new StringBuffer();
        boolean lastTokenWasPeriod = false;
        Position prevPosition = (tokens.hasNext() ? tokens.nextPosition() : Position.EMPTY_CONTENT_POSITION);
        String prevToken = "";

        while (tokens.hasNext() && !tokens.matches(DdlTokenizer.STATEMENT_KEY) && (
                (doUseTerminator() && !isTerminator(tokens)) || !doUseTerminator())
                && !(tokens.matches("WITH", "CHECK", "OPTION") || tokens.matches("WITH", DdlTokenStream.ANY_VALUE,
                                                                                 "CHECK", "OPTION"))) {
            final Position currPosition = tokens.nextPosition();
            final String thisToken = tokens.consume();
            final boolean thisTokenIsPeriod = thisToken.equals(PERIOD);
            final boolean thisTokenIsComma = thisToken.equals(COMMA);

            if (lastTokenWasPeriod || thisTokenIsPeriod || thisTokenIsComma) {
                sb.append(thisToken);
            } else if ((currPosition.getIndexInContent() - prevPosition.getIndexInContent() - prevToken.length()) > 0) {
                sb.append(SPACE).append(thisToken);
            } else {
                sb.append(thisToken);
            }

            if (thisTokenIsPeriod) {
                lastTokenWasPeriod = true;
            } else {
                lastTokenWasPeriod = false;
            }

            prevToken = thisToken;
            prevPosition = currPosition;
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#getDataTypeStartWords()
     */
    @Override
    protected List<String> getCustomDataTypeStartWords() {
        return MySqlDataTypes.CUSTOM_DATATYPE_START_WORDS;
    }

    // ===========================================================================================================================
    // ===========================================================================================================================
    class MySqlDataTypeParser extends DataTypeParser implements MySqlDataTypes {

        // NOTE THAT MYSQL allows "UNSIGNED" and "ZEROFILL" as options AFTER the datatype definition
        // Need to override and do a CHECK for and CONSUME them.

        /**
         * {@inheritDoc}
         *
         * @see DataTypeParser#isCustomDataType(DdlTokenStream)
         */
        @Override
        protected boolean isCustomDataType(DdlTokenStream tokens) throws ParsingException {
            // Loop through the registered statement start string arrays and look for exact matches.

            for (String[] stmts : mysqlDataTypeStrings) {
                if (tokens.matches(stmts))
                    return true;
            }
            return super.isCustomDataType(tokens);
        }

        @Override
        public DataType parse(DdlTokenStream tokens) throws ParsingException {
            DataType dataType = super.parse(tokens);
            if (dataType != null) {
                return dataType;
            }
            if (tokens.matches("SERIAL")) {
                dataType = new DataType();
                dataType.setName(tokens.consume());
                return dataType;
            }
            return null;
        }

        @Override
        protected DataType parseApproxNumericType(DdlTokenStream tokens) throws ParsingException {
            MySql8DataType dataType = null;
            String typeName;

            if (tokens.matches(DataTypes.DTYPE_REAL)) {
                dataType = new MySql8DataType();
                typeName = consume(tokens, dataType, false, DataTypes.DTYPE_REAL);
                dataType.setName(typeName);

                parsePrecisionScale(tokens, dataType);

            } else if (tokens.matches(DataTypes.DTYPE_DOUBLE_PRECISION)) {
                dataType = new MySql8DataType();
                typeName = consume(tokens, dataType, false, DataTypes.DTYPE_DOUBLE_PRECISION);
                dataType.setName(typeName);
            } else if (tokens.matches(DataTypes.DTYPE_FLOAT)) {
                dataType = new MySql8DataType();
                typeName = consume(tokens, dataType, false, DataTypes.DTYPE_FLOAT);
                dataType.setName(typeName);

                parsePrecisionScale(tokens, dataType);
            }

            return dataType;
        }

        private void parsePrecisionScale(DdlTokenStream tokens, DataType dataType) {
            if (tokens.matches(L_PAREN)) {
                consume(tokens, dataType, false, L_PAREN);

                int precision = (int) parseLong(tokens, dataType);
                dataType.setPrecision(precision);

                if (canConsume(tokens, dataType, false, COMMA)) {
                    int scale = (int) parseLong(tokens, dataType);
                    dataType.setScale(scale);
                }
                consume(tokens, dataType, false, R_PAREN);
            }
        }

        @Override
        protected DataType parseBitStringType(DdlTokenStream tokens) throws ParsingException {
            return super.parseBitStringType(tokens);
        }

        @Override
        protected DataType parseCharStringType(DdlTokenStream tokens) throws ParsingException {
            DataType superResult = super.parseCharStringType(tokens);
            MySql8DataType result = new MySql8DataType(superResult.getName());
            result.setLength(superResult.getLength());

            if (tokens.canConsume("CHARACTER", "SET")) {
                String charset = tokens.consume();
                result.setCharacterSet(charset);
            }
            if (tokens.canConsume("COLLATE")) {
                String collate = tokens.consume();
                result.setCollate(collate);
            }

            return result;
        }

        @Override
        protected DataType parseCustomType(DdlTokenStream tokens) throws ParsingException {
            DataType dataType = null;

            if (tokens.matches(DTYPE_FIXED) || tokens.matches(DTYPE_DOUBLE)) {
                dataType = new DataType();
                String typeName = tokens.consume();
                dataType.setName(typeName);

                if (tokens.matches(L_PAREN)) {
                    consume(tokens, dataType, false, L_PAREN);

                    int precision = (int) parseLong(tokens, dataType);
                    dataType.setPrecision(precision);

                    if (tokens.canConsume(COMMA)) {
                        int scale = (int) parseLong(tokens, dataType);
                        dataType.setScale(scale);
                    }
                    tokens.consume(R_PAREN);
                }
            } else if (tokens.matches(DTYPE_DATETIME)) {
                String typeName = tokens.consume();
                dataType = new DataType(typeName);

                if (tokens.matches(L_PAREN)) {
                    int precision = (int) parseBracketedLong(tokens, dataType);
                    dataType.setPrecision(precision);
                }
            } else if (tokens.matches(DTYPE_MEDIUMBLOB) || tokens.matches(DTYPE_LONGBLOB)
                    || tokens.matches(DTYPE_TINYBLOB) || tokens.matches(DTYPE_YEAR)
                    || tokens.matches(DTYPE_BOOLEAN) || tokens.matches(DTYPE_BOOL) || tokens.matches(DTYPE_JSON)
                    // dane przestrzenne
                    || tokens.matches(DTYPE_GEOMETRY) || tokens.matches(DTYPE_POINT)
                    || tokens.matches(DTYPE_LINESTRING) || tokens.matches(DTYPE_POLYGON)
                    || tokens.matches(DTYPE_GEOMETRYCOLLECTION) || tokens.matches(DTYPE_MULTIPOINT)
                    || tokens.matches(DTYPE_MULTILINESTRING) || tokens.matches(DTYPE_MULTIPOLYGON)) {
                String typeName = tokens.consume();
                dataType = new DataType(typeName);
            } else if (tokens.matches(DTYPE_MEDIUMINT) || tokens.matches(DTYPE_TINYINT) || tokens.matches(
                    DTYPE_VARBINARY)
                    || tokens.matches(DTYPE_BINARY) || tokens.matches(DTYPE_BIGINT)
                    || tokens.matches(DTYPE_BLOB)) {
                String typeName = tokens.consume();
                dataType = new DataType(typeName);
                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }
            } else if (tokens.matches(DTYPE_NVARCHAR)) {
                String typeName = getStatementTypeName(DTYPE_NVARCHAR);
                dataType = new DataType(typeName);
                tokens.consume(DTYPE_NVARCHAR);
                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }
            } else if (tokens.matches(DTYPE_NATIONAL_VARCHAR)) {
                String typeName = getStatementTypeName(DTYPE_NVARCHAR);
                dataType = new DataType(typeName);
                tokens.consume(DTYPE_NATIONAL_VARCHAR);
                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }
            } else if (tokens.matches(DTYPE_NATIONAL_CHARACTER_VARYING)) {
                String typeName = getStatementTypeName(DTYPE_NVARCHAR);
                dataType = new DataType(typeName);
                tokens.consume(DTYPE_NATIONAL_CHARACTER_VARYING);
                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }
            } else if (tokens.matches(DTYPE_MEDIUMTEXT) || tokens.matches(DTYPE_TEXT) || tokens.matches(DTYPE_LONGTEXT)
                    || tokens.matches(DTYPE_TINYTEXT)) {
                String typeName = tokens.consume();
                tokens.canConsume("BINARY");

                dataType = new MySql8DataType(typeName);

                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }

                if (tokens.canConsume("COLLATE")) {
                    String collate = tokens.consume();
                    ((MySql8DataType) dataType).setCollate(collate);
                }
                if (tokens.canConsume("CHARACTER", "SET")) {
                    String charset = tokens.consume();
                    ((MySql8DataType) dataType).setCharacterSet(charset);
                }
                if (tokens.canConsume("COLLATE")) {
                    String collate = tokens.consume();
                    ((MySql8DataType) dataType).setCollate(collate);
                }
            } else if (tokens.matches(DTYPE_SET)) {
                // SET(value1,value2,value3,...) [CHARACTER SET charset_name] [COLLATE collation_name]
                String typeName = tokens.consume();

                StringBuffer values = new StringBuffer();
                boolean comma;

                tokens.consume(L_PAREN);
                values.append(L_PAREN);
                do {
                    values.append(tokens.consume());
                    comma = tokens.canConsume(COMMA);
                    if (comma) {
                        values.append(COMMA);
                    }
                } while (comma);
                tokens.consume(R_PAREN);
                values.append(R_PAREN);

                dataType = new MySql8DataType(typeName + values);
                if (tokens.canConsume("COLLATE")) {
                    String collate = tokens.consume();
                    ((MySql8DataType) dataType).setCollate(collate);
                }
                if (tokens.canConsume("CHARACTER", "SET")) {
                    String charset = tokens.consume();
                    ((MySql8DataType) dataType).setCharacterSet(charset);
                }
                if (tokens.canConsume("COLLATE")) {
                    String collate = tokens.consume();
                    ((MySql8DataType) dataType).setCollate(collate);
                }

            } else if (tokens.matches(DTYPE_ENUM)) {
                // ENUM(value1,value2,value3,...) [CHARACTER SET charset_name] [COLLATE collation_name]
                String typeName = tokens.consume();

                StringBuffer values = new StringBuffer();

                tokens.consume(L_PAREN);
                values.append(L_PAREN);

                boolean comma;
                do {
                    values.append(tokens.consume());
                    comma = tokens.canConsume(COMMA);
                    if (comma) {
                        values.append(COMMA);
                    }
                } while (comma);
                tokens.consume(R_PAREN);
                values.append(R_PAREN);

                dataType = new MySql8DataType(typeName + values);
                if (tokens.canConsume("COLLATE")) {
                    String collate = tokens.consume();
                    ((MySql8DataType) dataType).setCollate(collate);
                }
                if (tokens.canConsume("CHARACTER", "SET")) {
                    String charset = tokens.consume();
                    ((MySql8DataType) dataType).setCharacterSet(charset);
                }
                if (tokens.canConsume("COLLATE")) {
                    String collate = tokens.consume();
                    ((MySql8DataType) dataType).setCollate(collate);
                }
            }

            if (dataType == null) {
                dataType = super.parseCustomType(tokens);
            }

            return dataType;
        }

        @Override
        protected DataType parseNationalCharStringType(DdlTokenStream tokens) throws ParsingException {
            DataType dataType;

            if (tokens.matches(DTYPE_NATIONAL_CHARACTER_VARYING)) {
                String typeName = getStatementTypeName(DTYPE_NVARCHAR);
                dataType = new DataType(typeName);
                tokens.consume(DTYPE_NATIONAL_CHARACTER_VARYING);
                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }
                return dataType;
            }

            return super.parseNationalCharStringType(tokens);
        }

        @Override
        protected DataType parseDateTimeType(DdlTokenStream tokens) throws ParsingException {
            return super.parseDateTimeType(tokens);
        }

        @Override
        protected DataType parseExactNumericType(DdlTokenStream tokens) throws ParsingException {
            MySql8DataType dataType = null;
            String typeName;

            if (tokens.matchesAnyOf("INTEGER", "INT", "SMALLINT")) {
                dataType = new MySql8DataType();
                typeName = consume(tokens, dataType, false);
                dataType.setName(typeName);

                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }

            } else if (tokens.matchesAnyOf("NUMERIC", "DECIMAL", "DEC")) {
                dataType = new MySql8DataType();
                typeName = consume(tokens, dataType, false);
                dataType.setName(typeName);

                parsePrecisionScale(tokens, dataType);
            } else if (tokens.matches("SERIAL")) {
                dataType = new MySql8DataType();
                typeName = consume(tokens, dataType, false);
                dataType.setName(typeName);
            }

            if (dataType == null) {
                return null;
            }

            return dataType;
        }

        @Override
        public void setPropertiesOnNode(AstNode columnNode, DataType datatype) {
            assert columnNode != null;
            assert datatype != null;
            super.setPropertiesOnNode(columnNode, datatype);

            if (datatype instanceof MySql8DataType) {
                String characterSet = ((MySql8DataType) datatype).getCharacterSet();
                if (characterSet != null) {
                    columnNode.setProperty(MySql8DdlLexicon.TABLE_CHARACTER_SET, characterSet);
                }
                String collate = ((MySql8DataType) datatype).getCollate();
                if (collate != null) {
                    columnNode.setProperty(MySql8DdlLexicon.TABLE_COLLATE, collate);
                }

                boolean onUpdateCurrentTimestamp = ((MySql8DataType) datatype).isOnUpdateCurrentTimestamp();
                if (onUpdateCurrentTimestamp) {
                    columnNode.setProperty(MySql8DdlLexicon.ON_UPDATE_CURRENT_TIMESTAMP, true);
                }
            }
        }

    }

    @Override
    protected DdlTokenizer ddlTokenizer(boolean includeComments) {
        return new MySql8DdlTokenizer(includeComments);
    }

    @Override
    protected boolean parseDefaultClause(DdlTokenStream tokens,
                                         AstNode columnNode) throws ParsingException {

        assert tokens != null;
        assert columnNode != null;

        // defaultClause
        // : defaultOption
        // ;
        // defaultOption : <literal> | datetimeValueFunction
        // | USER | CURRENT_USER | SESSION_USER | SYSTEM_USER | NULL;
        //
        // <datetime value function> ::=
        // <current date value function>
        // | <current time value function>
        // | <current timestamp value function>
        //
        // <current date value function> ::= CURRENT_DATE
        //
        // <current time value function> ::=
        // CURRENT_TIME [ <left paren> <time precision> <right paren> ]
        //
        // <current timestamp value function> ::=
        // CURRENT_TIMESTAMP [ <left paren> <timestamp precision> <right paren> ]

        boolean parsedDefaultClause = false;

        if (tokens.canConsume("ON", "UPDATE", "CURRENT_TIMESTAMP")) {
            columnNode.setProperty(MySql8DdlLexicon.ON_UPDATE_CURRENT_TIMESTAMP, true);
            parsedDefaultClause = true;
        }

        String defaultValue = "";

        if (tokens.canConsume("DEFAULT")) {

            String optionID;
            int precision = -1;

            if (tokens.canConsume("CURRENT_DATE") || tokens.canConsume("'CURRENT_DATE'")) {
                optionID = DEFAULT_ID_DATETIME;
                defaultValue = "CURRENT_DATE";
            } else if (tokens.canConsume("CURRENT_TIME") || tokens.canConsume("'CURRENT_TIME'")) {
                optionID = DEFAULT_ID_DATETIME;
                defaultValue = "CURRENT_TIME";
                if (tokens.canConsume(L_PAREN)) {
                    // EXPECT INTEGER
                    precision = integer(tokens.consume());
                    tokens.canConsume(R_PAREN);
                }
            } else if (tokens.canConsume("CURRENT_TIMESTAMP") || tokens.canConsume("'CURRENT_TIMESTAMP'")) {
                optionID = DEFAULT_ID_DATETIME;
                defaultValue = "CURRENT_TIMESTAMP";
                if (tokens.canConsume(L_PAREN)) {
                    // EXPECT INTEGER
                    precision = integer(tokens.consume());
                    tokens.canConsume(R_PAREN);
                }
            } else if (tokens.canConsume("USER") || tokens.canConsume("'USER'")) {
                optionID = DEFAULT_ID_USER;
                defaultValue = "USER";
            } else if (tokens.canConsume("CURRENT_USER") || tokens.canConsume("'CURRENT_USER'")) {
                optionID = DEFAULT_ID_CURRENT_USER;
                defaultValue = "CURRENT_USER";
            } else if (tokens.canConsume("SESSION_USER") || tokens.canConsume("'SESSION_USER'")) {
                optionID = DEFAULT_ID_SESSION_USER;
                defaultValue = "SESSION_USER";
            } else if (tokens.canConsume("SYSTEM_USER") || tokens.canConsume("'SYSTEM_USER'")) {
                optionID = DEFAULT_ID_SYSTEM_USER;
                defaultValue = "SYSTEM_USER";
            } else if (tokens.canConsume("NULL") || tokens.canConsume("NULL")) {
                optionID = DEFAULT_ID_NULL;
                defaultValue = "NULL";
            } else if (tokens.canConsume(L_PAREN)) {
                optionID = DEFAULT_ID_LITERAL;
                while (!tokens.canConsume(R_PAREN)) {
                    defaultValue = defaultValue + tokens.consume();
                }
            } else {
                optionID = DEFAULT_ID_LITERAL;
                // Assume default was EMPTY or ''
                defaultValue = tokens.consume();

                // NOTE: default value could be a Real number as well as an integer, so
                // 1000.00 is valid
                if (tokens.canConsume(".")) {
                    defaultValue = defaultValue + '.' + tokens.consume();
                }
            }

            columnNode.setProperty(DEFAULT_OPTION, optionID);
            columnNode.setProperty(DEFAULT_VALUE, defaultValue);
            if (precision > -1) {
                columnNode.setProperty(DEFAULT_PRECISION, precision);
            }
            parsedDefaultClause = true;
        }

        if (tokens.canConsume("ON", "UPDATE", "CURRENT_TIMESTAMP")) {
            columnNode.setProperty(MySql8DdlLexicon.ON_UPDATE_CURRENT_TIMESTAMP, true);
            parsedDefaultClause = true;
        }

        return parsedDefaultClause;
    }

    @Override
    protected String consumeIdentifier(DdlTokenStream tokens) throws ParsingException {
        String value = tokens.consume();
        // This may surrounded by quotes, so remove them ...
        if (value.charAt(0) == '"') {
            int length = value.length();
            // Check for the end quote ...
            value = value.substring(1, length - 1); // not complete!!
        } else if (value.startsWith("`") && value.endsWith("`")) {
            int length = value.length();
            value = value.substring(1, length - 1);
        }
        // TODO: Handle warnings elegantly
        // else {
        // // Not quoted, so check for reserved words ...
        // if (tokens.isKeyWord(value)) {
        // // Record warning ...
        // System.out.println("  WARNING:  Identifier [" + value + "] is a SQL 92 Reserved Word");
        // }
        // }
        return value;
    }

}
