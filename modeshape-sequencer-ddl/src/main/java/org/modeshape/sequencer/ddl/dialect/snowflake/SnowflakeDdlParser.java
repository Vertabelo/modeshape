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
package org.modeshape.sequencer.ddl.dialect.snowflake;

import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.common.text.TokenStream;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.modeshape.sequencer.ddl.StandardDdlLexicon.ALL_PRIVILEGES;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_LENGTH;
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
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.GRANTEE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.GRANT_PRIVILEGE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NEW_NAME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.OR_REPLACE_CLAUSE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_REFERENCE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_DOMAIN_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_SCHEMA_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_TABLE_CONSTRAINT_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_GRANT_ON_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_MISSING_TERMINATOR;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_REVOKE_ON_CHARACTER_SET_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_REVOKE_ON_COLLATION_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_REVOKE_ON_DOMAIN_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_REVOKE_ON_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_REVOKE_ON_TRANSLATION_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_STATEMENT_OPTION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_UNKNOWN_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.VALUE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.WITH_GRANT_OPTION;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.AS_CLAUSE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.COMMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.FUNCTION_PARAMETER_MODE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.PROPERTY_VALUE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.ROLE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.SCHEMA_NAME;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.SEQ_INCREMENT_BY;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.SEQ_START_WITH;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TRANSIENT_TABLE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ABORT_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ALTER_DATABASE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ALTER_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ALTER_ROLE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ALTER_SCHEMA_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ALTER_SEQUENCE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ALTER_USER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ALTER_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ANALYZE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CLUSTER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_COMMENT_ON_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_COMMIT_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CONSTRAINT_ATTRIBUTE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_COPY_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_DATABASE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_EXTERNAL_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_MASKING_POLICY;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_NETWORK_POLICY;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_NOTIFICATION_INTEGRATION;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_PIPE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_PROCEDURE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_RESOURCE_MONITOR;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_ROLE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_SECURITY_INTEGRATION;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_SHARE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_STAGE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_STORAGE_INTEGRATION;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_STREAM;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_TASK;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_USER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_CREATE_WAREHOUSE;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_DEALLOCATE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_DECLARE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_DROP_DATABASE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_DROP_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_DROP_ROLE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_DROP_SEQUENCE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_DROP_USER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_EXPLAIN_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_FETCH_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_FUNCTION_PARAMETER;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_DATABASE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_FOREIGN_DATA_WRAPPER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_FOREIGN_SERVER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_LANGUAGE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_SCHEMA_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_SEQUENCE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ON_TABLESPACE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_GRANT_ROLES_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_LISTEN_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_LOAD_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_LOCK_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_MOVE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_NOTIFY_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_PREPARE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_REASSIGN_OWNED_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_RELEASE_SAVEPOINT_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_REVOKE_ON_SCHEMA_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_ROLLBACK_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_SELECT_INTO_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_SHOW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_TRUNCATE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_UNLISTEN_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.snowflake.SnowflakeDdlLexicon.TYPE_VACUUM_STATEMENT;

/**
 * Snowflake-specific DDL Parser. Includes custom data types as well as custom DDL statements.
 */
public class SnowflakeDdlParser extends StandardDdlParser
    implements SnowflakeDdlConstants, SnowflakeDdlConstants.SnowflakeStatementStartPhrases {

    /**
     * The Snowflake parser identifier.
     */
    public static final String ID = "SNOWFLAKE";
    public static final int TRIES_OF_PARSE = 1000;

    static List<String[]> snowflakeDataTypeStrings = new ArrayList<String[]>();

    private static final String TERMINATOR = ";";

    public SnowflakeDdlParser() {
        setDatatypeParser(new SnowflakeDataTypeParser());
        initialize();
    }

    private void initialize() {

        setDoUseTerminator(true);

        setTerminator(TERMINATOR);

        snowflakeDataTypeStrings.addAll(SnowflakeDataTypes.CUSTOM_DATATYPE_START_PHRASES);
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

    /**
     * {@inheritDoc}
     * 
     * @see StandardDdlParser#initializeTokenStream(DdlTokenStream)
     */
    @Override
    protected void initializeTokenStream( DdlTokenStream tokens ) {
        super.initializeTokenStream(tokens);
        tokens.registerKeyWords(CUSTOM_KEYWORDS);
        tokens.registerKeyWords(SnowflakeDataTypes.CUSTOM_DATATYPE_START_WORDS);
        tokens.registerStatementStartPhrase(ALTER_PHRASES);
        tokens.registerStatementStartPhrase(CREATE_PHRASES);
        tokens.registerStatementStartPhrase(DROP_PHRASES);
        tokens.registerStatementStartPhrase(SET_PHRASES);
        tokens.registerStatementStartPhrase(MISC_PHRASES);
    }

    @Override
    protected void rewrite( DdlTokenStream tokens,
                            AstNode rootNode ) {
        assert tokens != null;
        assert rootNode != null;

        // We may hava a prepare statement that is followed by a missing terminator node

        List<AstNode> copyOfNodes = new ArrayList<AstNode>(rootNode.getChildren());
        AstNode prepareNode = null;
        boolean mergeNextStatement = false;
        for (AstNode child : copyOfNodes) {
            if (prepareNode != null && mergeNextStatement) {
                mergeNodes(tokens, prepareNode, child);
                rootNode.removeChild(child);
                prepareNode = null;
            }
            if (prepareNode != null && nodeFactory().hasMixinType(child, TYPE_MISSING_TERMINATOR)) {
                mergeNextStatement = true;
            } else {
                mergeNextStatement = false;
            }
            if (nodeFactory().hasMixinType(child, TYPE_PREPARE_STATEMENT)) {
                prepareNode = child;
            }
        }

        super.rewrite(tokens, rootNode); // Removes all extra "missing terminator" nodes

        // Now we need to walk the tree again looking for unknown nodes under the root
        // and attach them to the previous node, assuming the node can contain multiple nested statements.
        // CREATE FUNCTION is one of those types

        copyOfNodes = new ArrayList<AstNode>(rootNode.getChildren());
        boolean foundComplexNode = false;
        AstNode complexNode = null;
        for (AstNode child : copyOfNodes) {
            if (matchesComplexNode(child)) {
                foundComplexNode = true;
                complexNode = child;
            } else if (foundComplexNode) {
                if (complexNode != null && nodeFactory().hasMixinType(child, TYPE_UNKNOWN_STATEMENT)) {
                    mergeNodes(tokens, complexNode, child);
                    rootNode.removeChild(child);
                } else {
                    foundComplexNode = false;
                    complexNode = null;
                }
            }
        }
    }

    private boolean matchesComplexNode( AstNode node ) {
        for (String mixin : COMPLEX_STMT_TYPES) {
            if (nodeFactory().hasMixinType(node, mixin)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected AstNode parseAlterStatement( DdlTokenStream tokens,
                                           AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_ALTER_DATABASE)) {
            return parseStatement(tokens, STMT_ALTER_DATABASE, parentNode, TYPE_ALTER_DATABASE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_FUNCTION)) {
            return parseStatement(tokens, STMT_ALTER_FUNCTION, parentNode, TYPE_ALTER_FUNCTION_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_ROLE)) {
            return parseStatement(tokens, STMT_ALTER_ROLE, parentNode, TYPE_ALTER_ROLE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SCHEMA)) {
            return parseStatement(tokens, STMT_ALTER_SCHEMA, parentNode, TYPE_ALTER_SCHEMA_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SEQUENCE)) {
            return parseStatement(tokens, STMT_ALTER_SEQUENCE, parentNode, TYPE_ALTER_SEQUENCE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_USER)) {
            return parseStatement(tokens, STMT_ALTER_USER, parentNode, TYPE_ALTER_USER_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_VIEW)) {
            return parseStatement(tokens, STMT_ALTER_VIEW, parentNode, TYPE_ALTER_VIEW_STATEMENT);
        }

        return super.parseAlterStatement(tokens, parentNode);

    }

    @Override
    protected AstNode parseAlterTableStatement( DdlTokenStream tokens,
                                                AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);
         /*
            ALTER TABLE [ IF EXISTS ] <name> RENAME TO <new_table_name>

            ALTER TABLE [ IF EXISTS ] <name> SWAP WITH <target_table_name>

            ALTER TABLE [ IF EXISTS ] <name> { clusteringAction | tableColumnAction | constraintAction  }

            ALTER TABLE [ IF EXISTS ] <name> extTableColumnAction

            ALTER TABLE [ IF EXISTS ] <name> searchOptimizationAction

            ALTER TABLE [ IF EXISTS ] <name> SET
              [ STAGE_FILE_FORMAT = ( { FORMAT_NAME = '<file_format_name>' | TYPE = { CSV | JSON | AVRO | ORC | PARQUET | XML } [ formatTypeOptions ] } ) ]
              [ STAGE_COPY_OPTIONS = ( copyOptions ) ]
              [ DATA_RETENTION_TIME_IN_DAYS = <integer> ]
              [ MAX_DATA_EXTENSION_TIME_IN_DAYS = <integer> ]
              [ CHANGE_TRACKING = { TRUE | FALSE  } ]
              [ DEFAULT_DDL_COLLATION = '<collation_specification>' ]
              [ COMMENT = '<string_literal>' ]

            ALTER TABLE [ IF EXISTS ] <name> UNSET {
                                                   DATA_RETENTION_TIME_IN_DAYS     |
                                                   MAX_DATA_EXTENSION_TIME_IN_DAYS |
                                                   CHANGE_TRACKING                 |
                                                   DEFAULT_DDL_COLLATION           |
                                                   COMMENT
                                                   }
                                                   [ , ... ]
            ALTER TABLE [ IF EXISTS ] <name>
              ADD ROW ACCESS POLICY <policy_name> ON (<col_name> [ , ... ])

            ALTER TABLE [ IF EXISTS ] <name>
              DROP ROW ACCESS POLICY <policy_name>

            ALTER TABLE [ IF EXISTS ] <name>
              ADD ROW ACCESS POLICY <policy_name> ON (<col_name> [ , ... ])
              , DROP ROW ACCESS POLICY <policy_name>

            ALTER TABLE [ IF EXISTS ] <name> DROP ALL ROW ACCESS POLICIES

            tableColumnAction ::=
            {
               ADD COLUMN <col_name> <col_type>
                  [ { DEFAULT <expr> | { AUTOINCREMENT | IDENTITY } [ { ( <start_num> , <step_num> ) | START <num> INCREMENT <num> } ] } ]
                                       AUTOINCREMENT (or IDENTITY) supported only for columns with numeric data types (NUMBER, INT, FLOAT, etc.).
                   Also, if the table is not empty (i.e. rows exist in the table), only DEFAULT can be altered.
                  [ inlineConstraint ]
             | RENAME COLUMN <col_name> TO <new_col_name>

            | ALTER | MODIFY [ ( ]
            [ COLUMN ] <col1_name> DROP DEFAULT
                        , [ COLUMN ] <col1_name> SET DEFAULT <seq_name>.NEXTVAL
                        , [ COLUMN ] <col1_name> { [ SET ] NOT NULL | DROP NOT NULL }
                        , [ COLUMN ] <col1_name> [ [ SET DATA ] TYPE ] <type>
                        , [ COLUMN ] <col1_name> COMMENT '<string>'
            , COLUMN <col1_name> SET MASKING POLICY <policy_name>
                      [ , [ COLUMN ] <col2_name> ... ]
                      [ , ... ]
                  [ ) ]
             | DROP [ COLUMN ] <col1_name> [, <col2_name> ... ]
            }

         */
        tokens.consume(ALTER, TABLE); // consumes 'ALTER'
        tokens.canConsume("IF", "EXISTS");

        String tableName = parseName(tokens);
        tokens.canConsume("*");

        // System.out.println("  >> PARSING ALTER STATEMENT >>  TABLE Name = " + tableName);
        AstNode alterTableNode = nodeFactory().node(tableName, parentNode, TYPE_ALTER_TABLE_STATEMENT);

        do {
            parseAlterTableAction(tokens, alterTableNode);
        } while (tokens.canConsume(COMMA));

        markEndOfStatement(tokens, alterTableNode);

        return alterTableNode;
    }

    private void parseAlterTableAction( DdlTokenStream tokens,
                                        AstNode alterTableNode ) throws ParsingException {
        assert tokens != null;
        assert alterTableNode != null;

        if (tokens.canConsume("ADD")) { // ADD COLUMN
            if (tokens.matches("CONSTRAINT")) {
                parseTableConstraint(tokens, alterTableNode, true);
            } else if (tokens.matches("COLUMN")) {
                parseSingleCommaTerminatedColumnDefinition(tokens, alterTableNode, true);
            } else if (tokens.matches("SEARCH")) {
                tokens.consume("SEARCH", "OPTIMIZATION");
            } else if (tokens.matches("ROW")) {
                tokens.consume("ROW", "ACCESS", "POLICY");
                tokens.consume();
                tokens.consume("ON");
                parseContentBetweenParens(tokens);
            }

        } else if (tokens.canConsume("DROP")) { // DROP CONSTRAINT & DROP COLUMN
            if (tokens.canConsume("CONSTRAINT")) {
                String constraintName = parseName(tokens); // constraint name

                AstNode constraintNode = nodeFactory().node(constraintName, alterTableNode, TYPE_DROP_TABLE_CONSTRAINT_DEFINITION);

                if (tokens.canConsume(DropBehavior.CASCADE)) {
                    constraintNode.setProperty(DROP_BEHAVIOR, DropBehavior.CASCADE);
                } else if (tokens.canConsume(DropBehavior.RESTRICT)) {
                    constraintNode.setProperty(DROP_BEHAVIOR, DropBehavior.RESTRICT);
                }
            } else if (tokens.canConsume("COLUMN")) {
                // ALTER TABLE supplier
                // DROP COLUMN supplier_name;
                do {
                    String columnName = parseName(tokens);
                    AstNode columnNode = nodeFactory().node(columnName, alterTableNode, TYPE_DROP_COLUMN_DEFINITION);
                } while (tokens.canConsume(COMMA));

            } else if (tokens.canConsume("CLUSTERING", "KEY")) {

            } else if (tokens.canConsume("ALL", "ROW", "ACCESS", "POLICIES")) {

            } else if (tokens.canConsume("ROW", "ACCESS", "POLICY")) {
                String policyName = tokens.consume();
            }
        } else if (tokens.canConsume("RENAME", "COLUMN")) {
            String oldColumnName = parseName(tokens); // OLD COLUMN NAME
            tokens.consume("TO");
            String newColumnName = parseName(tokens); // NEW COLUMN NAME
            AstNode renameColumnNode = nodeFactory().node(oldColumnName, alterTableNode, SnowflakeDdlLexicon.TYPE_RENAME_COLUMN);
            renameColumnNode.setProperty(NEW_NAME, newColumnName);

        } else if (tokens.matches("ALTER") || tokens.matches("MODIFY")) {
            // -- ALTER [ COLUMN ] column SET STORAGE { PLAIN | EXTERNAL | EXTENDED | MAIN } +
            // -- ALTER [ COLUMN ] column new_tablespace SET STATISTICS integer
            // -- ALTER [ COLUMN ] column DROP DEFAULT +
            // -- ALTER [ COLUMN ] column [ SET DATA ] TYPE type [ USING expression ] +
            // -- ALTER [ COLUMN ] column SET DEFAULT expression +
            // -- ALTER [ COLUMN ] column { SET | DROP } NOT NULL
            tokens.canConsumeAnyOf("ALTER", "MODIFY");
            tokens.canConsume("COLUMN");
            String column = tokens.consume();
            AstNode alterColumnNode = nodeFactory().node(column, alterTableNode, StandardDdlLexicon.TYPE_ALTER_COLUMN_DEFINITION);
            if (tokens.canConsume("SET", "STORAGE")) {
                String storage = tokens.consume();
                alterColumnNode.setProperty(SnowflakeDdlLexicon.STORAGE, storage);
            } else if (tokens.canConsume("DROP", "DEFAULT")) {
                alterColumnNode.setProperty(SnowflakeDdlLexicon.DROP_DEFAULT);
            } else if (tokens.canConsume("SET", "DEFAULT")) {
                String expression = tokens.consume();
                alterColumnNode.setProperty(SnowflakeDdlLexicon.SET_DEFAULT, expression);
            } else if (tokens.canConsume("SET", "NOT", "NULL")) {
                alterColumnNode.setProperty(SnowflakeDdlLexicon.SET_NOT_NULL);
            } else if (tokens.canConsume("DROP", "NOT", "NULL")) {
                alterColumnNode.setProperty(SnowflakeDdlLexicon.DROP_NOT_NULL);
            } else if (tokens.canConsume("SET", "DATA", "TYPE")
                    || tokens.canConsume("TYPE")) {
                String type = tokens.consume();
                if (tokens.canConsume("USING")) {
                    String expression = tokens.consume();
                    alterColumnNode.setProperty(SnowflakeDdlLexicon.USING_EXPRESSION, expression);
                }
                alterColumnNode.setProperty(SnowflakeDdlLexicon.TARGET_OBJECT_TYPE, type);
                
            } else if (tokens.matches(TokenStream.ANY_VALUE, "SET", "STATISTICS")) {
                String newTablespace = tokens.consume();
                tokens.consume("SET", "STATISTICS");
                String value = tokens.consume();
                alterColumnNode.setProperty(SnowflakeDdlLexicon.STATISTICS_TABLESPACE, newTablespace);
                alterColumnNode.setProperty(SnowflakeDdlLexicon.STATISTICS_VALUE, value);
            }


        } else if (tokens.canConsume("CLUSTER", "BY")) {
            /**
            *
             *
             * clusteringAction ::=
             *     {
             *        CLUSTER BY ( <expr> [ , <expr> , ... ] )
             *      | RECLUSTER [ MAX_SIZE = <budget_in_bytes> ] [ WHERE <condition> ]
             *      |{
             *   SUSPEND | RESUME
             * } RECLUSTER
             * | DROP CLUSTERING KEY
             *}
             *
            * */
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            // -- CLUSTER ON index_name
            String indexName = parseName(tokens); // index_name
            optionNode.setProperty(VALUE, "CLUSTER ON" + SPACE + indexName);
        } else if (tokens.canConsume("RENAME", "TO")) {
            // --ALTER TABLE name
            // -- RENAME TO new_name
            String newTableName = parseName(tokens);
            alterTableNode.setProperty(SnowflakeDdlLexicon.RENAME_TO, newTableName);

        } else if (tokens.canConsume("SWAP", "WITH")) {
            // ALTER TABLE myschema.distributors SET SCHEMA your schema;
            String schemaName = parseName(tokens);
            alterTableNode.setProperty(SnowflakeDdlLexicon.SWAP_WITH, schemaName);
        } else {
            System.out.println("  WARNING:  Option not found for ALTER TABLE. Check your DDL for incomplete statement.");
        }
    }


    protected void parseConstraintAttributes( DdlTokenStream tokens, AstNode constraintNode ) throws ParsingException {
        assert tokens != null;
        assert constraintNode != null;

        super.parseConstraintAttributes(tokens, constraintNode);

        if (tokens.canConsume("NOT", "VALID")) {
            AstNode attrNode = nodeFactory().node("CONSTRAINT_ATTRIBUTE", constraintNode, TYPE_CONSTRAINT_ATTRIBUTE);
            attrNode.setProperty(PROPERTY_VALUE, "NOT VALID");
        }
    }
    
    private void parseSingleCommaTerminatedColumnDefinition( DdlTokenStream tokens,
                                                             AstNode tableNode,
                                                             boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        tokens.canConsume("COLUMN");
        String columnName = parseName(tokens);
        DataType datatype = getDatatypeParser().parse(tokens);

        AstNode columnNode = nodeFactory().node(columnName, tableNode, TYPE_COLUMN_DEFINITION);

        getDatatypeParser().setPropertiesOnNode(columnNode, datatype);
        // Now clauses and constraints can be defined in any order, so we need to keep parsing until we get to a comma, a
        // terminator
        // or a new statement

        // w niektórych przypadkach poniższa pętla sie nie kończy
        // zabezpiecznie na ten przypadek
        int triesGuard = TRIES_OF_PARSE;
        while (tokens.hasNext() && !tokens.matches(getTerminator()) && !tokens.matches(DdlTokenizer.STATEMENT_KEY)) {
            if (triesGuard == 0) {
                throw new IllegalStateException("Parsing is taking too much time");
            }
            boolean parsedDefaultClause = parseDefaultClause(tokens, columnNode);
            if (!parsedDefaultClause) {
                parseCollateClause(tokens, columnNode);
                parseColumnConstraint(tokens, columnNode, isAlterTable);
            }
            consumeComment(tokens);
            if (tokens.matches(COMMA)) {
                break;
            }
            triesGuard--;
        }
    }

    /**
     * Currently, only CREATE TABLE, CREATE VIEW, CREATE INDEX, CREATE SEQUENCE, CREATE TRIGGER and GRANT are accepted as clauses
     * within CREATE SCHEMA. {@inheritDoc}
     * 
     * @see StandardDdlParser#parseCreateSchemaStatement(DdlTokenStream,
     *      AstNode)
     */
    @Override
    protected AstNode parseCreateSchemaStatement( DdlTokenStream tokens,
                                                  AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        return super.parseCreateSchemaStatement(tokens, parentNode);
    }

    @Override
    protected AstNode parseCreateStatement( DdlTokenStream tokens,
                                            AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (matchesCreateTableStatement(tokens)) {
            return parseCreateTableStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_EXTERNAL_TABLE) || tokens.matches(STMT_CREATE_OR_REPLACE_EXTERNAL_TABLE)) {
            return parseCreateExternalTableStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_MATERIALIZED_VIEW)
            || tokens.matches(STMT_CREATE_OR_REPLACE_MATERIALIZED_VIEW)
            || tokens.matches(STMT_CREATE_SECURE_MATERIALIZED_VIEW)
            || tokens.matches(STMT_CREATE_OR_REPLACE_SECURE_MATERIALIZED_VIEW)) {
            return parseCreateMaterializedViewStatement(tokens, parentNode);
        } else if (matchesCreateViewStatement(tokens)) {
            return parseCreateViewStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_DATABASE)) {
            return parseStatement(tokens, STMT_CREATE_DATABASE, parentNode, TYPE_CREATE_DATABASE_STATEMENT);
        }  else if (tokens.matches(STMT_CREATE_FUNCTION)) {
            return parseCreateFunctionStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_OR_REPLACE_FUNCTION)) {
            return parseCreateFunctionStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_ROLE)) {
            return parseStatement(tokens, STMT_CREATE_ROLE, parentNode, TYPE_CREATE_ROLE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SEQUENCE) || tokens.matches(STMT_CREATE_OR_REPLACE_SEQUENCE)) {
            return parseCreateSequenceStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_USER)) {
            return parseStatement(tokens, STMT_CREATE_USER, parentNode, TYPE_CREATE_USER_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_MASKING_POLICY)) {
            return parseStatement(tokens, STMT_CREATE_MASKING_POLICY, parentNode, TYPE_CREATE_MASKING_POLICY);
        } else if (tokens.matches(STMT_CREATE_STAGE)) {
            return parseStatement(tokens, STMT_CREATE_STAGE, parentNode, TYPE_CREATE_STAGE);
        } else if (tokens.matches(STMT_CREATE_PIPE)) {
            return parseStatement(tokens, STMT_CREATE_PIPE, parentNode, TYPE_CREATE_PIPE);
        } else if (tokens.matches(STMT_CREATE_STREAM)) {
            return parseStatement(tokens, STMT_CREATE_STREAM, parentNode, TYPE_CREATE_STREAM);
        } else if (tokens.matches(STMT_CREATE_TASK)) {
            return parseStatement(tokens, STMT_CREATE_TASK, parentNode, TYPE_CREATE_TASK);
        } else if (tokens.matches(STMT_CREATE_PROCEDURE)) {
            return parseStatement(tokens, STMT_CREATE_PROCEDURE, parentNode, TYPE_CREATE_PROCEDURE);
        } else if (tokens.matches(STMT_CREATE_NETWORK_POLICY)) {
            return parseStatement(tokens, STMT_CREATE_NETWORK_POLICY, parentNode, TYPE_CREATE_NETWORK_POLICY);
        } else if (tokens.matches(STMT_CREATE_RESOURCE_MONITOR)) {
            return parseStatement(tokens, STMT_CREATE_RESOURCE_MONITOR, parentNode, TYPE_CREATE_RESOURCE_MONITOR);
        } else if (tokens.matches(STMT_CREATE_SHARE)) {
            return parseStatement(tokens, STMT_CREATE_SHARE, parentNode, TYPE_CREATE_SHARE);
        } else if (tokens.matches(STMT_CREATE_WAREHOUSE)) {
            return parseStatement(tokens, STMT_CREATE_WAREHOUSE, parentNode, TYPE_CREATE_WAREHOUSE);
        } else if (tokens.matches(STMT_CREATE_NOTIFICATION_INTEGRATION)) {
            return parseStatement(tokens, STMT_CREATE_NOTIFICATION_INTEGRATION, parentNode, TYPE_CREATE_NOTIFICATION_INTEGRATION);
        } else if (tokens.matches(STMT_CREATE_SECURITY_INTEGRATION)) {
            return parseStatement(tokens, STMT_CREATE_SECURITY_INTEGRATION, parentNode, TYPE_CREATE_SECURITY_INTEGRATION);
        } else if (tokens.matches(STMT_CREATE_STORAGE_INTEGRATION)) {
            return parseStatement(tokens, STMT_CREATE_STORAGE_INTEGRATION, parentNode, TYPE_CREATE_STORAGE_INTEGRATION);

        }

        return super.parseCreateStatement(tokens, parentNode);
    }

    private AstNode parseCreateExternalTableStatement(DdlTokenStream tokens, AstNode parentNode) {
        assert tokens != null;
        assert parentNode != null;
        /*
        CREATE [ OR REPLACE ] EXTERNAL TABLE [IF NOT EXISTS]
          <table_name>
            ( [ <col_name> <col_type> AS <expr> | <part_col_name> <col_type> AS <part_expr> ]
              [ inlineConstraint ]
              [ , <col_name> <col_type> AS <expr> | <part_col_name> <col_type> AS <part_expr> ... ]
              [ , ... ] )
         */
        markStartOfStatement(tokens);

        tokens.consume("CREATE"); // CREATE

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }
        tokens.consume("EXTERNAL", "TABLE"); 
        tokens.canConsume(IF_NOT_EXISTS);

        String tableName = parseName(tokens);
        AstNode externalTableNode = nodeFactory().node(tableName, parentNode, TYPE_CREATE_EXTERNAL_TABLE_STATEMENT);

        parseColumnsAndConstraints(tokens, externalTableNode);

        parseCreateExternalTableOptions(tokens, externalTableNode);

        markEndOfStatement(tokens, externalTableNode);

        return externalTableNode;
    }

    @Override
    protected void addExternalTableColumnConstraintProperty(AstNode columnNode, String constraintName) {
        if(constraintName != null) {
            columnNode.setProperty(SnowflakeDdlLexicon.CONSTRAINT_NAME, constraintName);
        }
    }

    @Override
    protected boolean parseAsClause(DdlTokenStream tokens, AstNode columnNode) {
        if(tokens.canConsume("AS") && tokens.matches("(")) {
            columnNode.setProperty(AS_CLAUSE, parseContentBetweenParens(tokens));
            return true;
        }
        return false;
    }

    protected void parseCreateExternalTableOptions(DdlTokenStream tokens,
                                                   AstNode tableNode ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        while (areNextTokensCreateExternalTableOptions(tokens)) {
            parseNextCreateExternalTableOption(tokens, tableNode);
        }

    }

    protected boolean areNextTokensCreateExternalTableOptions( DdlTokenStream tokens ) throws ParsingException {
        assert tokens != null;
        // current token can't be a terminator or the next n-tokens can't be a statement

        if (tokens.matches("INTEGRATION")
         || tokens.matches("PARTITION")
         || tokens.matches("WITH")
         || tokens.matches( "LOCATION")
         || tokens.matches("REFRESH_ON_CREATE")
         || tokens.matches("AUTO_REFRESH")
         || tokens.matches("PATTERN")
         || tokens.matches("FILE_FORMAT")
         || tokens.matches("AWS_SNS_TOPIC")
         || tokens.matches("COPY")
         || tokens.matches("COMMENT")) {
            return true;
        }
        return false;
    }

    protected void parseNextCreateExternalTableOption( DdlTokenStream tokens,
            AstNode tableNode ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        //         cloudProviderParams (for Microsoft Azure) ::=
        //          [ INTEGRATION = '<integration_name>' ]
        //             cloudProviderParams
        if (tokens.canConsume("INTEGRATION", "=")) {
            String integrationName = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.INTEGRATION, integrationName);
        }
        // [ PARTITION BY ( <part_col_name> [, <part_col_name> ... ] ) ]
        if (tokens.canConsume("PARTITION", "BY")) {
            String expression = consumeParenBoundedTokens(tokens, false);
            tableNode.setProperty(SnowflakeDdlLexicon.PARTITION_BY, expression.trim());
        }
        //  [ WITH ] LOCATION = externalStage
        if (tokens.canConsume("WITH", "LOCATION", "=") || tokens.canConsume("LOCATION", "=")) {
            String externalStage = parseExternalStage(tokens);
            tableNode.setProperty(SnowflakeDdlLexicon.LOCATION, externalStage);
        }

        // [ REFRESH_ON_CREATE =  { TRUE | FALSE } ]
        if (tokens.canConsume("REFRESH_ON_CREATE", "=")) {
            String refreshOnCreate = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.REFRESH_ON_CREATE, "true".equalsIgnoreCase(refreshOnCreate));
        }
        //  [ AUTO_REFRESH = { TRUE | FALSE } ]
        if (tokens.canConsume("AUTO_REFRESH", "=")) {
            String dataRetentionTime = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.AUTO_REFRESH, "true".equalsIgnoreCase(dataRetentionTime));
        }
        //  [ PATTERN = '<regex_pattern>' ]
        if (tokens.canConsume("PATTERN", "=")) {
            String maxDataExtension = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.PATTERN, maxDataExtension);
        }
        // FILE_FORMAT = ( { FORMAT_NAME = '<file_format_name>' | TYPE = { CSV | JSON | AVRO | ORC | PARQUET } [ formatTypeOptions ] } )
        // UWAGA: FILE_FORMAT jest obowiązkowy, ale dla uproszczenia przyjmijmy ze nie
        if (tokens.canConsume("FILE_FORMAT", "=")) {
            String fileFormat = consumeParenBoundedTokens(tokens, false);
            tableNode.setProperty(SnowflakeDdlLexicon.FILE_FORMAT, fileFormat.trim());
        }

        //   [ AWS_SNS_TOPIC = <string> ]
        if (tokens.canConsume("AWS_SNS_TOPIC", "=")) {
            String defaultDddlCollation = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.AWS_SNS_TOPIC, defaultDddlCollation);
        }
        // [ COPY GRANTS ]
        if (tokens.canConsume("COPY", "GRANTS")) {
            // do nothing
        }
        // [ COMMENT = '<string_literal>' ]
        if (tokens.canConsume("COMMENT", "=")) {
            String comment = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.COMMENT, comment);
        }
    }

    private String parseExternalStage(DdlTokenStream tokens) {
        /* externalStage ::=
        @[<namespace>.]<ext_stage_name>[/<path>] */
        // @ nie jest tokenizowane, przyjdzie albo w
        // 1. @namespace
        // 2. @ext_stage_name
        // nie ma to znaczenia dla wyniku
        StringBuilder sb = new StringBuilder();
        if (tokens.matches(TokenStream.ANY_VALUE, ".")) {
            String namespace = tokens.consume();
            sb.append(namespace);
            tokens.consume(".");
            sb.append(".");
        }
        String stageName = tokens.consume();
        sb.append(stageName);
        if (tokens.canConsume("/")) {
            sb.append("/");
            while (tokens.matches(TokenStream.ANY_VALUE, "/")) {
                String pathPath = tokens.consume(); tokens.consume("/");
                sb.append(pathPath + "/");
            }
        }
        return sb.toString();
    }


    private AstNode parseCreateMaterializedViewStatement(DdlTokenStream tokens, AstNode parentNode) {
        assert tokens != null;
        assert parentNode != null;
        /*
        CREATE [ OR REPLACE ] [ SECURE ] MATERIALIZED VIEW [ IF NOT EXISTS ] <name>
          [ COPY GRANTS ]
          ( <column_list> )
          [ COMMENT = '<string_literal>' ]
          [ CLUSTER BY ( <expr1> [, <expr2> ... ] ) ]
          AS <select_statement>

         */

        markStartOfStatement(tokens);

        tokens.consume("CREATE"); // CREATE

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }

        boolean secureClause = false;
        if (tokens.canConsume("SECURE")) {
            secureClause = true;
        }
        tokens.consume("MATERIALIZED", "VIEW");
        tokens.canConsume(IF_NOT_EXISTS);

        String viewName = parseName(tokens);
        AstNode createViewNode = nodeFactory().node(viewName, parentNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        if (secureClause) {
            createViewNode.setProperty(SnowflakeDdlLexicon.SECURE, true);
        }
        parseColumnsAndConstraints(tokens, createViewNode);

        tokens.canConsume("COPY", "GRANTS");
        if (tokens.canConsume("COMMENT", "=")) {
            String comment = tokens.consume();
            createViewNode.setProperty(SnowflakeDdlLexicon.COMMENT, comment);
        }

        if (tokens.canConsume("CLUSTER", "BY")) {
            String clusterBy = parseContentBetweenParens(tokens);
            createViewNode.setProperty(SnowflakeDdlLexicon.CLUSTER_BY, clusterBy);
        }


        tokens.consume("AS");

        boolean didUseTermintor = doUseTerminator();
        setDoUseTerminator(true);
        String queryExpression = parseUntilTerminator(tokens);
        setDoUseTerminator(didUseTermintor);

        createViewNode.setProperty(CREATE_VIEW_QUERY_EXPRESSION, queryExpression);

        markEndOfStatement(tokens, createViewNode);

        return createViewNode;
    }

    @Override
    protected AstNode parseCreateTableStatement( DdlTokenStream tokens,
                                                 AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        /* CREATE [ OR REPLACE ] [ { [ LOCAL | GLOBAL ] TEMP[ORARY] | VOLATILE } | TRANSIENT ] TABLE [ IF NOT EXISTS ]
  <table_name>
    ( <col_name> <col_type>
                             [ COLLATE '<collation_specification>' ]
        // COLLATE is supported only for text data types (VARCHAR and synonyms)
				[ { DEFAULT <expr>
                               | { AUTOINCREMENT | IDENTITY } [ ( <start_num> , <step_num> ) | START <num> INCREMENT <num> ] } ]
        // AUTOINCREMENT (or IDENTITY) is supported only for numeric data types (NUMBER, INT, FLOAT, etc.)
				[ NOT NULL ]
				[ inlineConstraint ]
				[ , <col_name> <col_type> [ ... ] ]
				[ , outoflineConstraint ]
				[ , ... ] )
				[... props] */

        markStartOfStatement(tokens);

        tokens.consume("CREATE"); // CREATE

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }

        tokens.canConsumeAnyOf("LOCAL", "GLOBAL");
        tokens.canConsumeAnyOf("TEMP", "TEMPORARY");
        tokens.canConsume("VOLATILE");
        
        boolean transientTable = false;
        if(tokens.canConsume("TRANSIENT")) {
            transientTable = true;
        }

        tokens.consume("TABLE"); // TABLE
        
        tokens.canConsume(IF_NOT_EXISTS);

        String tableName = parseName(tokens);
        AstNode tableNode = nodeFactory().node(tableName, parentNode, TYPE_CREATE_TABLE_STATEMENT);
        if (transientTable) {
            tableNode.setProperty(TRANSIENT_TABLE, true);
        }
        
        parseColumnsAndConstraints(tokens, tableNode);

        // go inside ...
        parseCreateTableOptions(tokens, tableNode);
        
        markEndOfStatement(tokens, tableNode);

        return tableNode;
    }

    protected void parseCreateTableOptions( DdlTokenStream tokens,
                                            AstNode tableNode ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        /*
            [ CLUSTER BY ( <expr> [ , <expr> , ... ] ) ]
            [ STAGE_FILE_FORMAT = ( { FORMAT_NAME = '<file_format_name>'
            | TYPE = { CSV | JSON | AVRO | ORC | PARQUET | XML } [ formatTypeOptions ] } ) ]
            [ STAGE_COPY_OPTIONS = ( copyOptions ) ]
            [ DATA_RETENTION_TIME_IN_DAYS = <integer> ]
            [ MAX_DATA_EXTENSION_TIME_IN_DAYS = <integer> ]
            [ CHANGE_TRACKING = { TRUE | FALSE } ]
            [ DEFAULT_DDL_COLLATION = '<collation_specification>' ]
            [ COPY GRANTS ]
            [ COMMENT = '<string_literal>' ]
         */
        while (areNextTokensCreateTableOptions(tokens)) {
            parseNextCreateTableOption(tokens, tableNode);
        }

    }

    protected void parseNextCreateTableOption( DdlTokenStream tokens,
                                               AstNode tableNode ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        /*
            [ CLUSTER BY ( <expr> [ , <expr> , ... ] ) ]
            [ STAGE_FILE_FORMAT = ( { FORMAT_NAME = '<file_format_name>'
            | TYPE = { CSV | JSON | AVRO | ORC | PARQUET | XML } [ formatTypeOptions ] } ) ]
            [ STAGE_COPY_OPTIONS = ( copyOptions ) ]
            [ DATA_RETENTION_TIME_IN_DAYS = <integer> ]
            [ MAX_DATA_EXTENSION_TIME_IN_DAYS = <integer> ]
            [ CHANGE_TRACKING = { TRUE | FALSE } ]
            [ DEFAULT_DDL_COLLATION = '<collation_specification>' ]
            [ COPY GRANTS ]
            [ COMMENT = '<string_literal>' ]
         */

        if (tokens.canConsume("CLUSTER", "BY")) {
            String expression = consumeParenBoundedTokens(tokens, false);
            tableNode.setProperty(SnowflakeDdlLexicon.CLUSTER_BY, expression);
        }
        if (tokens.canConsume("STAGE_FILE_FORMAT", "=")) {
            String fileFormat = consumeParenBoundedTokens(tokens, false);
            tableNode.setProperty(SnowflakeDdlLexicon.STAGE_FILE_FORMAT, fileFormat.trim());
        }

        // [ STAGE_COPY_OPTIONS = ( copyOptions ) ]
        if (tokens.canConsume("STAGE_COPY_OPTIONS", "=")) {
            String copyOptions = consumeParenBoundedTokens(tokens, false);
            tableNode.setProperty(SnowflakeDdlLexicon.STAGE_COPY_OPTIONS, copyOptions.trim());
        }
        // [ DATA_RETENTION_TIME_IN_DAYS = <integer> ]
        if (tokens.canConsume("DATA_RETENTION_TIME_IN_DAYS", "=")) {
            String dataRetentionTime = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.DATA_RETENTION, dataRetentionTime);
        }
        // [ MAX_DATA_EXTENSION_TIME_IN_DAYS = <integer> ]
        if (tokens.canConsume("MAX_DATA_EXTENSION_TIME_IN_DAYS", "=")) {
            String maxDataExtension = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.MAX_DATA_EXTENSION, maxDataExtension);
        }
        // [ CHANGE_TRACKING = { TRUE | FALSE } ]
        if (tokens.canConsume("CHANGE_TRACKING", "=")) {
            String changeTracking = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.CHANGE_TRACKING, "true".equalsIgnoreCase(changeTracking));
        }
        // [ DEFAULT_DDL_COLLATION = '<collation_specification>' ]
        if (tokens.canConsume("DEFAULT_DDL_COLLATION", "=")) {
            String defaultDddlCollation = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.DEFAULT_DDL_COLLATION, defaultDddlCollation);
        }
        // [ COPY GRANTS ]
        if (tokens.canConsume("COPY", "GRANTS")) {
            // do nothing
        }
        // [ COMMENT = '<string_literal>' ]
        if (tokens.canConsume("COMMENT", "=")) {
            String comment = tokens.consume();
            tableNode.setProperty(SnowflakeDdlLexicon.COMMENT, comment);
        }
    }

    protected boolean areNextTokensCreateTableOptions( DdlTokenStream tokens ) throws ParsingException {
        assert tokens != null;
        // current token can't be a terminator or the next n-tokens can't be a statement
        boolean result = false;
        if (tokens.matches("CLUSTER", "BY")) {
            result = true;
        }
        if (tokens.matches("STAGE_COPY_OPTIONS")) {
            result = true;
        }
        if (tokens.matches("DATA_RETENTION_TIME_IN_DAYS")) {
            result = true;
        }
        if (tokens.matches("MAX_DATA_EXTENSION_TIME_IN_DAYS")) {
            result = true;
        }
        if (tokens.matches("CHANGE_TRACKING")) {
            result = true;
        }
        if (tokens.matches("DEFAULT_DDL_COLLATION")) {
            result = true;
        }
        if (tokens.matches("COPY")) {
            result = true;
        }
        if (tokens.matches("COMMENT")) {
            result = true;
        }
        return result;
    }

    @Override
    protected AstNode parseCreateViewStatement( DdlTokenStream tokens,
                                                AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);
        // <view definition> ::=
        // CREATE [ OR REPLACE ] [ SECURE ] [ RECURSIVE ] VIEW [ IF NOT EXISTS ] <name>
        //  [ ( <column_list> ) ]
        //  [ COPY GRANTS ]
        //  [ COMMENT = '<string_literal>' ]
        //  AS <select_statement>

        tokens.consume("CREATE");
        
        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
        }

        boolean secureClause = false;
        if (tokens.canConsume("SECURE")) {
            secureClause = true;
        }

        boolean recursiveClause = false;
        if (tokens.canConsume("RECURSIVE")) {
            recursiveClause = true;
        }

        tokens.consume("VIEW");

        String name = parseName(tokens);

        AstNode createViewNode = nodeFactory().node(name, parentNode, TYPE_CREATE_VIEW_STATEMENT);
        createViewNode.setProperty(OR_REPLACE_CLAUSE, orReplaceClause);
        createViewNode.setProperty(SnowflakeDdlLexicon.SECURE, secureClause);
        createViewNode.setProperty(SnowflakeDdlLexicon.RECURSIVE, recursiveClause);
        
        // CONSUME COLUMNS
        parseColumnNameList(tokens, createViewNode, TYPE_COLUMN_REFERENCE);

        tokens.canConsume("COPY", "GRANTS");
        if (tokens.canConsume("COMMENT", "=")) {
            String comment = tokens.consume();
            createViewNode.setProperty(SnowflakeDdlLexicon.COMMENT, comment);
        }

        tokens.consume("AS");

        boolean didUseTermintor = doUseTerminator();
        setDoUseTerminator(true);
        String queryExpression = parseUntilTerminator(tokens);
        setDoUseTerminator(didUseTermintor);

        createViewNode.setProperty(CREATE_VIEW_QUERY_EXPRESSION, queryExpression);

        markEndOfStatement(tokens, createViewNode);

        return createViewNode;
    }

    @Override
    protected boolean parseDefaultClause( DdlTokenStream tokens,
                                          AstNode columnNode ) {
        assert tokens != null;
        assert columnNode != null;

        /*
        	} else if( tokens.matches("NOW")){
        	    tokens.consume("NOW");
        	    tokens.consume('(');
        	    tokens.consume(')');
        	    defaultValue = "NOW()";
        	} else if( tokens.matches("NEXTVAL")){
        	    defaultValue = tokens.consume() + consumeParenBoundedTokens(tokens, true);
        	} 
         * 
         */
        // defaultClause
        // : 'WITH'? 'DEFAULT' defaultOption
        // ;
        // defaultOption : ('('? literal ')'?) | datetimeValueFunction
        // | 'SYSDATE' | 'USER' | 'CURRENT_USER' | 'SESSION_USER' | 'SYSTEM_USER' | 'NULL' | nowOption;
        String defaultValue = "";

        if (tokens.matchesAnyOf("WITH", "DEFAULT")) {
            if (tokens.matches("WITH")) {
                tokens.consume();
            }
            tokens.consume("DEFAULT");
            String optionID;
            int precision = -1;

            if (tokens.canConsume("CURRENT_DATE")) {

                optionID = DEFAULT_ID_DATETIME;
                defaultValue = "CURRENT_DATE";
            } else if (tokens.canConsume("CURRENT_TIME")) {
                optionID = DEFAULT_ID_DATETIME;
                defaultValue = "CURRENT_TIME";
                if (tokens.canConsume(L_PAREN)) {
                    // EXPECT INTEGER
                    precision = integer(tokens.consume());
                    tokens.canConsume(R_PAREN);
                }
            } else if (tokens.canConsume("CURRENT_TIMESTAMP")) {
                optionID = DEFAULT_ID_DATETIME;
                defaultValue = "CURRENT_TIMESTAMP";
                if (tokens.canConsume(L_PAREN)) {
                    // EXPECT INTEGER
                    precision = integer(tokens.consume());
                    tokens.canConsume(R_PAREN);
                }
                
            } else if (tokens.canConsume("USER")) {
                optionID = DEFAULT_ID_USER;
                defaultValue = "USER";
            } else if (tokens.canConsume("CURRENT_USER")) {
                optionID = DEFAULT_ID_CURRENT_USER;
                defaultValue = "CURRENT_USER";
            } else if (tokens.canConsume("SESSION_USER")) {
                optionID = DEFAULT_ID_SESSION_USER;
                defaultValue = "SESSION_USER";
            } else if (tokens.canConsume("SYSTEM_USER")) {
                optionID = DEFAULT_ID_SYSTEM_USER;
                defaultValue = "SYSTEM_USER";
            } else if (tokens.canConsume("NULL")) {
                optionID = DEFAULT_ID_NULL;
                defaultValue = "NULL";
            } else if (tokens.canConsume(L_PAREN)) {
                optionID = DEFAULT_ID_LITERAL;
                while (!tokens.canConsume(R_PAREN)) {
                    defaultValue = defaultValue + tokens.consume();
                    defaultValue += parsePossibleCastExpression(tokens);
                }
            } else if (tokens.matches("NOW")) {
                optionID = DEFAULT_ID_LITERAL;
                tokens.consume("NOW");
                tokens.consume('(');
                tokens.consume(')');
                defaultValue = "NOW()";
            } else if (tokens.matches("NEXTVAL")) {
                optionID = DEFAULT_ID_LITERAL;
                defaultValue = tokens.consume() + consumeParenBoundedTokens(tokens, true);
            } else {
                optionID = DEFAULT_ID_LITERAL;
                // Assume default was EMPTY or ''
                defaultValue = tokens.consume();
                // NOTE: default value could be a Real number as well as an integer, so
                // 1000.00 is valid
                if (tokens.canConsume(".")) {
                    defaultValue = defaultValue + '.' + tokens.consume();
                }
                
                defaultValue += parsePossibleCastExpression(tokens);
            }

            columnNode.setProperty(DEFAULT_OPTION, optionID);
            columnNode.setProperty(DEFAULT_VALUE, defaultValue);
            if (precision > -1) {
                columnNode.setProperty(DEFAULT_PRECISION, precision);
            }
            return true;
        }

        return false;
    }
    
    
    protected String parsePossibleCastExpression(DdlTokenStream tokens) {
        StringBuffer sb = new StringBuffer();
        
        if(tokens.canConsume(":", ":")) {
            sb.append("::");
            sb.append(tokens.consume());
        }
        
        return sb.toString();
    }
    

    @Override
    protected AstNode parseCustomStatement( DdlTokenStream tokens,
                                            AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_COMMENT_ON)) {
            return parseCommentStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_ABORT)) {
            return parseStatement(tokens, STMT_ABORT, parentNode, TYPE_ABORT_STATEMENT);
        } else if (tokens.matches(STMT_ANALYZE)) {
            return parseStatement(tokens, STMT_ANALYZE, parentNode, TYPE_ANALYZE_STATEMENT);
        } else if (tokens.matches(STMT_CLUSTER)) {
            return parseStatement(tokens, STMT_CLUSTER, parentNode, TYPE_CLUSTER_STATEMENT);
        } else if (tokens.matches(STMT_COPY)) {
            return parseStatement(tokens, STMT_COPY, parentNode, TYPE_COPY_STATEMENT);
        } else if (tokens.matches(STMT_DEALLOCATE_PREPARE)) {
            return parseStatement(tokens, STMT_DEALLOCATE_PREPARE, parentNode, TYPE_DEALLOCATE_STATEMENT);
        } else if (tokens.matches(STMT_DEALLOCATE)) {
            return parseStatement(tokens, STMT_DEALLOCATE, parentNode, TYPE_DEALLOCATE_STATEMENT);
        } else if (tokens.matches(STMT_DECLARE)) {
            return parseStatement(tokens, STMT_DECLARE, parentNode, TYPE_DECLARE_STATEMENT);
        } else if (tokens.matches(STMT_EXPLAIN_ANALYZE)) {
            return parseStatement(tokens, STMT_EXPLAIN_ANALYZE, parentNode, TYPE_EXPLAIN_STATEMENT);
        } else if (tokens.matches(STMT_EXPLAIN)) {
            return parseStatement(tokens, STMT_EXPLAIN, parentNode, TYPE_EXPLAIN_STATEMENT);
        } else if (tokens.matches(STMT_FETCH)) {
            return parseStatement(tokens, STMT_FETCH, parentNode, TYPE_FETCH_STATEMENT);
        } else if (tokens.matches(STMT_LISTEN)) {
            return parseStatement(tokens, STMT_LISTEN, parentNode, TYPE_LISTEN_STATEMENT);
        } else if (tokens.matches(STMT_LOAD)) {
            return parseStatement(tokens, STMT_LOAD, parentNode, TYPE_LOAD_STATEMENT);
        } else if (tokens.matches(STMT_LOCK_TABLE)) {
            return parseStatement(tokens, STMT_LOCK_TABLE, parentNode, TYPE_LOCK_TABLE_STATEMENT);
        } else if (tokens.matches(STMT_MOVE)) {
            return parseStatement(tokens, STMT_MOVE, parentNode, TYPE_MOVE_STATEMENT);
        } else if (tokens.matches(STMT_NOTIFY)) {
            return parseStatement(tokens, STMT_NOTIFY, parentNode, TYPE_NOTIFY_STATEMENT);
        } else if (tokens.matches(STMT_PREPARE)) {
            return parseStatement(tokens, STMT_PREPARE, parentNode, TYPE_PREPARE_STATEMENT);
        } else if (tokens.matches(STMT_REASSIGN_OWNED)) {
            return parseStatement(tokens, STMT_REASSIGN_OWNED, parentNode, TYPE_REASSIGN_OWNED_STATEMENT);
        } else if (tokens.matches(STMT_RELEASE_SAVEPOINT)) {
            return parseStatement(tokens, STMT_RELEASE_SAVEPOINT, parentNode, TYPE_RELEASE_SAVEPOINT_STATEMENT);
        } else if (tokens.matches(STMT_ROLLBACK)) {
            return parseStatement(tokens, STMT_ROLLBACK, parentNode, TYPE_ROLLBACK_STATEMENT);
        } else if (tokens.matches(STMT_SELECT_INTO)) {
            return parseStatement(tokens, STMT_SELECT_INTO, parentNode, TYPE_SELECT_INTO_STATEMENT);
        } else if (tokens.matches(STMT_SHOW)) {
            return parseStatement(tokens, STMT_SHOW, parentNode, TYPE_SHOW_STATEMENT);
        } else if (tokens.matches(STMT_TRUNCATE)) {
            return parseStatement(tokens, STMT_TRUNCATE, parentNode, TYPE_TRUNCATE_STATEMENT);
        } else if (tokens.matches(STMT_UNLISTEN)) {
            return parseStatement(tokens, STMT_UNLISTEN, parentNode, TYPE_UNLISTEN_STATEMENT);
        } else if (tokens.matches(STMT_VACUUM)) {
            return parseStatement(tokens, STMT_VACUUM, parentNode, TYPE_VACUUM_STATEMENT);
        } else if (tokens.matches(STMT_COMMIT)) {
            return parseStatement(tokens, STMT_COMMIT, parentNode, TYPE_COMMIT_STATEMENT);
        }

        return super.parseCustomStatement(tokens, parentNode);
    }

    @Override
    protected AstNode parseDropStatement( DdlTokenStream tokens,
                                          AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_DROP_DATABASE)) {
            return parseSimpleDropStatement(tokens, STMT_DROP_DATABASE, parentNode, TYPE_DROP_DATABASE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_FUNCTION)) {
            return parseStatement(tokens, STMT_DROP_FUNCTION, parentNode, TYPE_DROP_FUNCTION_STATEMENT);
        } else if (tokens.matches(STMT_DROP_ROLE)) {
            return parseSimpleDropStatement(tokens, STMT_DROP_ROLE, parentNode, TYPE_DROP_ROLE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SEQUENCE)) {
            return parseSimpleDropStatement(tokens, STMT_DROP_SEQUENCE, parentNode, TYPE_DROP_SEQUENCE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_USER)) {
            return parseSimpleDropStatement(tokens, STMT_DROP_USER, parentNode, TYPE_DROP_USER_STATEMENT);
        } else if (tokens.matches(StatementStartPhrases.STMT_DROP_DOMAIN)) {
            // -- DROP DOMAIN [ IF EXISTS ] name [, ...] [ CASCADE | RESTRICT ]
            return parseSimpleDropStatement(tokens,
                                            StatementStartPhrases.STMT_DROP_DOMAIN,
                                            parentNode,
                                            TYPE_DROP_DOMAIN_STATEMENT);
        } else if (tokens.matches(StatementStartPhrases.STMT_DROP_TABLE)) {
            // -- DROP TABLE [ IF EXISTS ] name [, ...] [ CASCADE | RESTRICT ]
            return parseSimpleDropStatement(tokens, StatementStartPhrases.STMT_DROP_TABLE, parentNode, TYPE_DROP_TABLE_STATEMENT);
        } else if (tokens.matches(StatementStartPhrases.STMT_DROP_VIEW)) {
            // -- DROP VIEW [ IF EXISTS ] name [, ...] [ CASCADE | RESTRICT ]
            return parseSimpleDropStatement(tokens, StatementStartPhrases.STMT_DROP_VIEW, parentNode, TYPE_DROP_VIEW_STATEMENT);
        } else if (tokens.matches(StatementStartPhrases.STMT_DROP_SCHEMA)) {
            // -- DROP SCHEMA [ IF EXISTS ] name [, ...] [ CASCADE | RESTRICT ]
            return parseSimpleDropStatement(tokens,
                                            StatementStartPhrases.STMT_DROP_SCHEMA,
                                            parentNode,
                                            TYPE_DROP_SCHEMA_STATEMENT);
        }

        return super.parseDropStatement(tokens, parentNode);
    }

    private AstNode parseSimpleDropStatement( DdlTokenStream tokens,
                                              String[] startPhrase,
                                              AstNode parentNode,
                                              String stmtType ) throws ParsingException {
        assert tokens != null;
        assert startPhrase != null && startPhrase.length > 0;
        assert parentNode != null;

        markStartOfStatement(tokens);

        String behavior = null;
        tokens.consume(startPhrase);
        boolean usesIfExists = tokens.canConsume("IF", "EXISTS"); // SUPER CLASS does not include "IF EXISTS"

        List<String> nameList = new ArrayList<String>();
        nameList.add(parseName(tokens));
        while (tokens.matches(COMMA)) {
            tokens.consume(COMMA);
            nameList.add(parseName(tokens));
        }

        if (tokens.canConsume("CASCADE")) {
            behavior = "CASCADE";
        } else if (tokens.canConsume("RESTRICT")) {
            behavior = "RESTRICT";
        }

        AstNode dropNode = nodeFactory().node(nameList.get(0), parentNode, stmtType);

        if (behavior != null) {
            dropNode.setProperty(DROP_BEHAVIOR, behavior);
        }

        markEndOfStatement(tokens, dropNode);

        // If there is only ONE name, then the EXPRESSION property is the whole expression and we don't need to set the
        // ORIGINAL EXPRESSION
        String originalExpression = (String)dropNode.getProperty(DDL_EXPRESSION);
        Object startLineNumber = dropNode.getProperty(DDL_START_LINE_NUMBER);
        Object startColumnNumber = dropNode.getProperty(DDL_START_COLUMN_NUMBER);
        Object startCharIndex = dropNode.getProperty(DDL_START_CHAR_INDEX);

        if (nameList.size() > 1) {
            for (int i = 1; i < nameList.size(); i++) {
                String nextName = nameList.get(i);
                AstNode newNode = createSingleDropNode(nextName,
                                                       startPhrase,
                                                       originalExpression,
                                                       usesIfExists,
                                                       behavior,
                                                       stmtType,
                                                       parentNode);
                newNode.setProperty(DDL_START_LINE_NUMBER, startLineNumber);
                newNode.setProperty(DDL_START_COLUMN_NUMBER, startColumnNumber);
                newNode.setProperty(DDL_START_CHAR_INDEX, startCharIndex);
            }

            // Since there is more than ONE name, then the EXPRESSION property of the first node's expression needs to be reset to
            // the first name and the ORIGINAL EXPRESSION property set to the entire statement.
            StringBuffer sb = new StringBuffer().append(getStatementTypeName(startPhrase));
            if (usesIfExists) {
                sb.append(SPACE).append("IF EXISTS");
            }
            sb.append(SPACE).append(nameList.get(0));
            if (behavior != null) {
                sb.append(SPACE).append(behavior);
            }
            sb.append(SEMICOLON);
            dropNode.setProperty(DDL_EXPRESSION, sb.toString());
            dropNode.setProperty(DDL_LENGTH, sb.length());
            dropNode.setProperty(DDL_ORIGINAL_EXPRESSION, originalExpression);
        }

        return dropNode;
    }

    private AstNode createSingleDropNode( String name,
                                          String[] startPhrase,
                                          String originalExpression,
                                          boolean usesIfExists,
                                          String behavior,
                                          String nodeType,
                                          AstNode parentNode ) {
        assert name != null;
        assert startPhrase != null && startPhrase.length > 0;
        assert nodeType != null;
        assert parentNode != null;

        AstNode newNode = nodeFactory().node(name, parentNode, nodeType);
        StringBuffer sb = new StringBuffer().append(getStatementTypeName(startPhrase));
        if (usesIfExists) {
            sb.append(SPACE).append("IF EXISTS");
        }
        sb.append(SPACE).append(name);
        if (behavior != null) {
            sb.append(SPACE).append(behavior);
        }
        sb.append(SEMICOLON);

        newNode.setProperty(DDL_EXPRESSION, sb.toString());
        newNode.setProperty(DDL_LENGTH, sb.length());
        newNode.setProperty(DDL_ORIGINAL_EXPRESSION, originalExpression);

        return newNode;
    }

    /**
     * {@inheritDoc}
     * 
     * @see StandardDdlParser#parseGrantStatement(DdlTokenStream,
     *      AstNode)
     */
    @Override
    protected AstNode parseGrantStatement( DdlTokenStream tokens,
                                           AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;
        assert tokens.matches(GRANT);

        markStartOfStatement(tokens);

        // NOTE: The first wack at this does not take into account the apparent potential repeating name elements after each type
        // declaration. Example:
        // GRANT { { SELECT | INSERT | UPDATE | DELETE | TRUNCATE | REFERENCES | TRIGGER }
        // [,...] | ALL [ PRIVILEGES ] }
        // ON [ TABLE ] tablename [, ...]
        // TO { [ GROUP ] rolename | PUBLIC } [, ...] [ WITH GRANT OPTION ]
        //
        // the "ON [ TABLE ] tablename [, ...]" seems to indicate that you can grant privileges on multiple tables at once, which
        // is
        // different thatn the SQL 92 standard. So this pass ONLY allows one and an parsing error will probably occur if multiple.
        //
        // Syntax for tables
        //
        // GRANT <privileges> ON <object name>
        // TO <grantee> [ { <comma> <grantee> }... ]
        // [ WITH GRANT OPTION ]
        //
        // <object name> ::=
        // [ TABLE ] <table name>
        // | SEQUENCE <sequence name>
        // | DATABASE <db name>
        // | FOREIGN DATA WRAPPER <fdw name>
        // | FOREIGN SERVER <server name>
        // | FUNCTION <function name>
        // | LANGUAGE <language name>
        // | SCHEMA <schema name>
        // | TABLESPACE <tablespace name>

        //
        // Syntax for roles
        //
        // GRANT roleName [ {, roleName }* ] TO grantees

        // privilege-types
        //
        // ALL PRIVILEGES | privilege-list
        //
        List<AstNode> grantNodes = new ArrayList<AstNode>();
        boolean allPrivileges = false;

        List<AstNode> privileges = new ArrayList<AstNode>();

        tokens.consume("GRANT");

        if (tokens.canConsume("ALL")) {
        	tokens.canConsume("PRIVILEGES");
            allPrivileges = true;
        } else {
            parseGrantPrivileges(tokens, privileges);
        }

        if (allPrivileges || !privileges.isEmpty()) {

            tokens.consume("ON");

            if (tokens.canConsume("SCHEMA")) {
                grantNodes = parseMultipleGrantTargets(tokens, parentNode, TYPE_GRANT_ON_SCHEMA_STATEMENT);
            } else if (tokens.canConsume("SEQUENCE")) {
                grantNodes = parseMultipleGrantTargets(tokens, parentNode, TYPE_GRANT_ON_SEQUENCE_STATEMENT);
            } else if (tokens.canConsume("TABLESPACE")) {
                grantNodes = parseMultipleGrantTargets(tokens, parentNode, TYPE_GRANT_ON_TABLESPACE_STATEMENT);
            } else if (tokens.canConsume("DATABASE")) {
                grantNodes = parseMultipleGrantTargets(tokens, parentNode, TYPE_GRANT_ON_DATABASE_STATEMENT);
            } else if (tokens.canConsume("FUNCTION")) {
                grantNodes = parseFunctionAndParameters(tokens, parentNode);
            } else if (tokens.canConsume("LANGUAGE")) {
                grantNodes = parseMultipleGrantTargets(tokens, parentNode, TYPE_GRANT_ON_LANGUAGE_STATEMENT);
            } else if (tokens.canConsume("FOREIGN", "DATA", "WRAPPER")) {
                grantNodes = parseMultipleGrantTargets(tokens, parentNode, TYPE_GRANT_ON_FOREIGN_DATA_WRAPPER_STATEMENT);
            } else if (tokens.canConsume("FOREIGN", "SERVER")) {
                grantNodes = parseMultipleGrantTargets(tokens, parentNode, TYPE_GRANT_ON_FOREIGN_SERVER_STATEMENT);
            } else {
                tokens.canConsume(TABLE); // OPTIONAL
                String name = parseName(tokens);
                AstNode grantNode = nodeFactory().node(name, parentNode, TYPE_GRANT_ON_TABLE_STATEMENT);
                grantNodes.add(grantNode);
                while (tokens.canConsume(COMMA)) {
                    // Assume more names here
                    name = parseName(tokens);
                    grantNode = nodeFactory().node(name, parentNode, TYPE_GRANT_ON_TABLE_STATEMENT);
                    grantNodes.add(grantNode);
                }
            }
        } else {
            // Assume ROLES here
            // role [, ...]
            AstNode grantNode = nodeFactory().node("roles", parentNode, TYPE_GRANT_ROLES_STATEMENT);
            grantNodes.add(grantNode);
            do {
                String role = parseName(tokens);
                nodeFactory().node(role, grantNode, ROLE);
            } while (tokens.canConsume(COMMA));
        }

        tokens.consume("TO");
        List<String> grantees = new ArrayList<String>();

        do {
            String grantee = parseName(tokens);
            grantees.add(grantee);
        } while (tokens.canConsume(COMMA));

        boolean withGrantOption = false;
        if (tokens.canConsume("WITH", "GRANT", "OPTION")) {
            withGrantOption = true;
        }

        // Set all properties and children on Grant Nodes
        for (AstNode grantNode : grantNodes) {
            List<AstNode> copyOfPrivileges = copyOfPrivileges(privileges);
            // Attach privileges to grant node
            for (AstNode node : copyOfPrivileges) {
                node.setParent(grantNode);
            }
            if (allPrivileges) {
                grantNode.setProperty(ALL_PRIVILEGES, allPrivileges);
            }
            for (String grantee : grantees) {
                nodeFactory().node(grantee, grantNode, GRANTEE);
            }

            if (withGrantOption) {
                AstNode optionNode = nodeFactory().node("withGrant", grantNode, TYPE_STATEMENT_OPTION);
                optionNode.setProperty(VALUE, "WITH GRANT OPTION");
            }
        }
        AstNode firstGrantNode = grantNodes.get(0);

        markEndOfStatement(tokens, firstGrantNode);

        // Update additional grant nodes with statement info

        for (int i = 1; i < grantNodes.size(); i++) {
            AstNode grantNode = grantNodes.get(i);
            grantNode.setProperty(DDL_EXPRESSION, firstGrantNode.getProperty(DDL_EXPRESSION));
            grantNode.setProperty(DDL_LENGTH, firstGrantNode.getProperty(DDL_LENGTH));
            grantNode.setProperty(DDL_START_LINE_NUMBER, firstGrantNode.getProperty(DDL_START_LINE_NUMBER));
            grantNode.setProperty(DDL_START_CHAR_INDEX, firstGrantNode.getProperty(DDL_START_CHAR_INDEX));
            grantNode.setProperty(DDL_START_COLUMN_NUMBER, firstGrantNode.getProperty(DDL_START_COLUMN_NUMBER));
        }

        return grantNodes.get(0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see StandardDdlParser#parseGrantPrivileges(DdlTokenStream,
     *      List)
     */
    @Override
    protected void parseGrantPrivileges( DdlTokenStream tokens,
                                         List<AstNode> privileges ) throws ParsingException {
        // privilege-types
        //
        // ALL PRIVILEGES | privilege-list
        //
        // privilege-list
        //
        // table-privilege {, table-privilege }*
        //
        // table-privilege
        // SELECT [ <left paren> <privilege column list> <right paren> ]
        // | DELETE
        // | INSERT [ <left paren> <privilege column list> <right paren> ]
        // | UPDATE [ <left paren> <privilege column list> <right paren> ]
        // | REFERENCES [ <left paren> <privilege column list> <right paren> ]
        // | USAGE
        // | TRIGGER
        // | TRUNCATE
        // | CREATE
        // | CONNECT
        // | TEMPORARY
        // | TEMP
        // | EXECUTE

        // POSTGRES has the following Privileges:
        // GRANT { { SELECT | INSERT | UPDATE | DELETE | TRUNCATE | REFERENCES | TRIGGER }

        do {
            AstNode node = null;

            if (tokens.canConsume(DELETE)) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, DELETE);
            } else if (tokens.canConsume(INSERT)) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, INSERT);
                parseColumnNameList(tokens, node, TYPE_COLUMN_REFERENCE);
            } else if (tokens.canConsume("REFERENCES")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "REFERENCES");
                parseColumnNameList(tokens, node, TYPE_COLUMN_REFERENCE);
            } else if (tokens.canConsume(SELECT)) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, SELECT);
                // Could have columns here
                // GRANT SELECT (col1), UPDATE (col1) ON mytable TO miriam_rw;

                // Let's just swallow the column data.

                consumeParenBoundedTokens(tokens, true);
            } else if (tokens.canConsume("USAGE")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "USAGE");
            } else if (tokens.canConsume(UPDATE)) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, UPDATE);
                parseColumnNameList(tokens, node, TYPE_COLUMN_REFERENCE);
            } else if (tokens.canConsume("TRIGGER")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "TRIGGER");
            } else if (tokens.canConsume("TRUNCATE")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "TRUNCATE");
            } else if (tokens.canConsume("CREATE")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "CREATE");
            } else if (tokens.canConsume("CONNECT")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "CONNECT");
            } else if (tokens.canConsume("TEMPORARY")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "TEMPORARY");
            } else if (tokens.canConsume("TEMP")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "TEMP");
            } else if (tokens.canConsume("EXECUTE")) {
                node = nodeFactory().node("privilege");
                node.setProperty(TYPE, "EXECUTE");
            }

            if (node == null) {
                break;
            }
            nodeFactory().setType(node, GRANT_PRIVILEGE);
            privileges.add(node);

        } while (tokens.canConsume(COMMA));

    }
    
    protected AstNode parseRevokeStatement( DdlTokenStream tokens,
    		AstNode parentNode ) throws ParsingException {
    	assert tokens != null;
    	assert parentNode != null;
    	assert tokens.matches(REVOKE);

    	markStartOfStatement(tokens);

    	// <revoke statement> ::=
    	// REVOKE [ GRANT OPTION FOR ]
    	// <privileges>
    	// ON <object name>
    	// FROM <grantee> [ { <comma> <grantee> }... ] <drop behavior>

    	AstNode revokeNode = null;
    	boolean allPrivileges = false;
    	boolean withGrantOption = false;

    	List<AstNode> privileges = new ArrayList<AstNode>();

    	tokens.consume("REVOKE");

    	withGrantOption = tokens.canConsume("WITH", "GRANT", "OPTION");

    	if (tokens.canConsume("ALL")) {
    		tokens.canConsume("PRIVILEGES");
    		allPrivileges = true;
    	} else {
    		parseGrantPrivileges(tokens, privileges);
    	}
    	tokens.consume("ON");

    	if (tokens.canConsume("DOMAIN")) {
    		String name = parseName(tokens);
    		revokeNode = nodeFactory().node(name, parentNode, TYPE_REVOKE_ON_DOMAIN_STATEMENT);
    	} else if (tokens.canConsume("COLLATION")) {
    		String name = parseName(tokens);
    		revokeNode = nodeFactory().node(name, parentNode, TYPE_REVOKE_ON_COLLATION_STATEMENT);
    	} else if (tokens.canConsume("CHARACTER", "SET")) {
    		String name = parseName(tokens);
    		revokeNode = nodeFactory().node(name, parentNode, TYPE_REVOKE_ON_CHARACTER_SET_STATEMENT);
    	} else if (tokens.canConsume("TRANSLATION")) {
    		String name = parseName(tokens);
    		revokeNode = nodeFactory().node(name, parentNode, TYPE_REVOKE_ON_TRANSLATION_STATEMENT);
    	} else if (tokens.canConsume("SCHEMA")) { 
    		String name = parseName(tokens);
    		revokeNode = nodeFactory().node(name, parentNode, TYPE_REVOKE_ON_SCHEMA_STATEMENT);
    	} else {
    		tokens.canConsume(TABLE); // OPTIONAL
    		String name = parseName(tokens);
    		revokeNode = nodeFactory().node(name, parentNode, TYPE_REVOKE_ON_TABLE_STATEMENT);
    	}

    	// Attach privileges to grant node
    	for (AstNode node : privileges) {
    		node.setParent(revokeNode);
    	}

    	if (allPrivileges) {
    		revokeNode.setProperty(ALL_PRIVILEGES, allPrivileges);
    	}

    	tokens.consume("FROM");

    	do {
    		String grantee = parseName(tokens);
    		nodeFactory().node(grantee, revokeNode, GRANTEE);
    	} while (tokens.canConsume(COMMA));

    	String behavior = null;

    	if (tokens.canConsume("CASCADE")) {
    		behavior = "CASCADE";
    	} else if (tokens.canConsume("RESTRICT")) {
    		behavior = "RESTRICT";
    	}

    	if (behavior != null) {
    		revokeNode.setProperty(DROP_BEHAVIOR, behavior);
    	}

    	if (withGrantOption) {
    		revokeNode.setProperty(WITH_GRANT_OPTION, "WITH GRANT OPTION");
    	}

    	markEndOfStatement(tokens, revokeNode);

    	return revokeNode;
    }

    private boolean matchesCreateViewStatement(DdlTokenStream tokens) {
        return tokens.matches(STMT_CREATE_VIEW)
                || tokens.matches(STMT_CREATE_OR_REPLACE_VIEW)
                || tokens.matches(STMT_CREATE_SECURE_VIEW)
                || tokens.matches(STMT_CREATE_OR_REPLACE_SECURE_VIEW)
                || tokens.matches(STMT_CREATE_RECURSIVE_VIEW)
                || tokens.matches(STMT_CREATE_OR_REPLACE_RECURSIVE_VIEW)
                || tokens.matches(STMT_CREATE_SECURE_RECURSIVE_VIEW)
                || tokens.matches(STMT_CREATE_OR_REPLACE_SECURE_RECURSIVE_VIEW);
    }


    private boolean matchesCreateTableStatement(DdlTokenStream tokens) {
        return tokens.matches(STMT_CREATE_OR_REPLACE_TABLE)
                || tokens.matches(joinMatches(STMT_CREATE_GLOBAL_TEMPORARY_TABLE, IF_NOT_EXISTS))
                || tokens.matches(joinMatches(STMT_CREATE_LOCAL_TEMPORARY_TABLE, IF_NOT_EXISTS))
                || tokens.matches(STMT_CREATE_TABLE_TRANSIENT_TABLE)
                || tokens.matches(STMT_CREATE_OR_REPLACE_TABLE_TRANSIENT_TABLE)
                || tokens.matches(STMT_CREATE_TABLE_VOLATILE_TABLE)
                || tokens.matches(STMT_CREATE_OR_REPLACE_TABLE_VOLATILE_TABLE);
    }
    
    private String[] joinMatches(String[] match1, String[] match2) {
        int iterator = 0;
        String[] matches = new String[match1.length + match2.length];
        
        for (String match : match1) {
            matches[iterator++] = match;
        }
        
        for (String match: match2) {
            matches[iterator++] = match;
        }
        
        return matches;
    }

    private List<AstNode> parseMultipleGrantTargets( DdlTokenStream tokens,
                                                     AstNode parentNode,
                                                     String nodeType ) throws ParsingException {
        List<AstNode> grantNodes = new ArrayList<AstNode>();
        String name = parseName(tokens);
        AstNode grantNode = nodeFactory().node(name, parentNode, nodeType);
        grantNodes.add(grantNode);
        while (tokens.canConsume(COMMA)) {
            // Assume more names here
            name = parseName(tokens);
            grantNode = nodeFactory().node(name, parentNode, nodeType);
            grantNodes.add(grantNode);
        }

        return grantNodes;
    }

    private List<AstNode> copyOfPrivileges( List<AstNode> privileges ) {
        List<AstNode> copyOfPrivileges = new ArrayList<AstNode>();
        for (AstNode node : privileges) {
            copyOfPrivileges.add(node.clone());
        }

        return copyOfPrivileges;
    }

    private List<AstNode> parseFunctionAndParameters( DdlTokenStream tokens,
                                                      AstNode parentNode ) throws ParsingException {
        boolean isFirstFunction = true;
        List<AstNode> grantNodes = new ArrayList<AstNode>();

        // FUNCTION funcname ( [ [ argmode ] [ argname ] argtype [, ...] ] ) [, ...]

        // argmode = [ IN, OUT, INOUT, or VARIADIC ]

        // p(a int, b TEXT), q(integer, double)

        // [postgresddl:grantOnFunctionStatement] > ddl:grantStatement, postgresddl:functionOperand mixin
        // + * (postgresddl:functionParameter) = postgresddl:functionParameter multiple

        do {
            String name = parseName(tokens);
            AstNode grantFunctionNode = nodeFactory().node(name, parentNode, TYPE_GRANT_ON_FUNCTION_STATEMENT);

            grantNodes.add(grantFunctionNode);

            // Parse Parameter Data
            if (tokens.matches(L_PAREN)) {
                tokens.consume(L_PAREN);

                if (!tokens.canConsume(R_PAREN)) {
                    // check for datatype
                    do {
                        String mode = null;

                        if (tokens.matchesAnyOf("IN", "OUT", "INOUT", "VARIADIC")) {
                            mode = tokens.consume();
                        }
                        AstNode paramNode = null;

                        DataType dType = getDatatypeParser().parse(tokens);
                        if (dType != null) {
                            // NO Parameter Name, only DataType
                            paramNode = nodeFactory().node("parameter", grantFunctionNode, TYPE_FUNCTION_PARAMETER);
                            if (mode != null) {
                                paramNode.setProperty(FUNCTION_PARAMETER_MODE, mode);
                            }
                            getDatatypeParser().setPropertiesOnNode(paramNode, dType);
                        } else {
                            String paramName = parseName(tokens);
                            dType = getDatatypeParser().parse(tokens);
                            assert paramName != null;

                            paramNode = nodeFactory().node(paramName, grantFunctionNode, TYPE_FUNCTION_PARAMETER);
                            if (mode != null) {
                                paramNode.setProperty(FUNCTION_PARAMETER_MODE, mode);
                            }
                            if (dType != null) {
                                getDatatypeParser().setPropertiesOnNode(paramNode, dType);
                            }
                        }
                    } while (tokens.canConsume(COMMA));

                    tokens.consume(R_PAREN);
                }
            }

            // RESET first parameter flag
            if (isFirstFunction) {
                isFirstFunction = false;
            }
        } while (tokens.canConsume(COMMA));

        return grantNodes;
    }

    @Override
    protected AstNode parseSetStatement( DdlTokenStream tokens,
                                         AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        return super.parseSetStatement(tokens, parentNode);
    }

    private AstNode parseCommentStatement( DdlTokenStream tokens,
                                           AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);

        /*
        --  TABLE object_name |
        --  COLUMN table_name.column_name |
        --  AGGREGATE agg_name (agg_type [, ...] ) |
        --  CAST (sourcetype AS targettype) |
        --  CONSTRAINT constraint_name ON table_name |
        --  CONVERSION object_name |
        --  DATABASE object_name |
        --  DOMAIN object_name |
        --  FUNCTION func_name ( [ [ argmode ] [ argname ] argtype [, ...] ] ) |
        --  INDEX object_name |
        --  LARGE OBJECT large_object_oid |
        --  OPERATOR op (leftoperand_type, rightoperand_type) |
        --  OPERATOR CLASS object_name USING index_method |
        --  OPERATOR FAMILY object_name USING index_method |
        --  [ PROCEDURAL ] LANGUAGE object_name |
        --  ROLE object_name |
        --  RULE rule_name ON table_name |
        --  SCHEMA object_name |
        --  SEQUENCE object_name |
        --  TABLESPACE object_name |
        --  TEXT SEARCH CONFIGURATION object_name |
        --  TEXT SEARCH DICTIONARY object_name |
        --  TEXT SEARCH PARSER object_name |
        --  TEXT SEARCH TEMPLATE object_name |
        --  TRIGGER trigger_name ON table_name |
        --  TYPE object_name |
        --  VIEW object_name
        --} IS ’text’
         */
        tokens.consume("COMMENT", "ON"); // consumes 'COMMENT' 'ON'

        String objectType = null;
        String objectName = null;

        if (tokens.matches(TABLE)) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("COLUMN")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("AGGREGATE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
            // (agg_type [, ...] )
            consumeParenBoundedTokens(tokens, true);
        } else if (tokens.matches("CAST")) {
            objectType = tokens.consume();
            consumeParenBoundedTokens(tokens, true);
        } else if (tokens.matches("CONSTRAINT")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
            tokens.consume("ON");
            tokens.consume(); // table_name
        } else if (tokens.matches("CONVERSION")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("DATABASE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("DOMAIN")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("FUNCTION")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
            consumeParenBoundedTokens(tokens, true);
        } else if (tokens.matches("INDEX")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("LARGE", "OBJECT")) {
            tokens.consume("LARGE", "OBJECT");
            objectType = "LARGE OBJECT";
            objectName = parseName(tokens);
        } else if (tokens.matches("OPERATOR", "FAMILY")) {
            tokens.consume("OPERATOR", "FAMILY");
            objectType = "OPERATOR FAMILY";
            objectName = parseName(tokens);
            tokens.consume("USING");
            tokens.consume(); // index_method
        } else if (tokens.matches("OPERATOR", "CLASS")) {
            tokens.consume("OPERATOR", "CLASS");
            objectType = "OPERATOR CLASS";
            objectName = parseName(tokens);
            tokens.consume("USING");
            tokens.consume(); // index_method
        } else if (tokens.matches("OPERATOR")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
            consumeParenBoundedTokens(tokens, true);
        } else if (tokens.matches("PROCEDURAL", "LANGUAGE")) {
            tokens.consume("PROCEDURAL", "LANGUAGE");
            objectType = "PROCEDURAL LANGUAGE";
            objectName = parseName(tokens);
        } else if (tokens.matches("LANGUAGE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("EXTENSION")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("ROLE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("RULE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
            tokens.consume("ON");
            tokens.consume(); // table_name
        } else if (tokens.matches("SCHEMA")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("SEQUENCE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("TABLESPACE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("TEXT", "SEARCH", "CONFIGURATION")) {
            tokens.consume("TEXT", "SEARCH", "CONFIGURATION");
            objectType = "TEXT SEARCH CONFIGURATION";
            objectName = parseName(tokens);
        } else if (tokens.matches("TEXT", "SEARCH", "DICTIONARY")) {
            tokens.consume("TEXT", "SEARCH", "DICTIONARY");
            objectType = "TEXT SEARCH DICTIONARY";
            objectName = parseName(tokens);
        } else if (tokens.matches("TEXT", "SEARCH", "PARSER")) {
            tokens.consume("TEXT", "SEARCH", "PARSER");
            objectType = "TEXT SEARCH PARSER";
            objectName = parseName(tokens);
        } else if (tokens.matches("TEXT", "SEARCH", "TEMPLATE")) {
            tokens.consume("TEXT", "SEARCH", "TEMPLATE");
            objectType = "TEXT SEARCH TEMPLATE";
            objectName = parseName(tokens);
        } else if (tokens.matches("TRIGGER")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
            tokens.consume("ON");
            tokens.consume(); // table_name
        } else if (tokens.matches("TYPE")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("VIEW")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        }

        // System.out.println("  >> FOUND [COMMENT ON] STATEMENT >>  TABLE Name = " + objName);
        String commentString = null;

        tokens.consume("IS");
        if (tokens.matches("NULL")) {
            tokens.consume("NULL");
            commentString = "NULL";
        } else {
            commentString = parseUntilTerminator(tokens).trim();
        }

        AstNode commentNode = null;

        if (objectName != null) {
            commentNode = nodeFactory().node(objectName, parentNode, TYPE_COMMENT_ON_STATEMENT);
            commentNode.setProperty(SnowflakeDdlLexicon.TARGET_OBJECT_NAME, objectName);
        } else {
            commentNode = nodeFactory().node("commentOn", parentNode, TYPE_COMMENT_ON_STATEMENT);
        }
        commentNode.setProperty(SnowflakeDdlLexicon.COMMENT, commentString);
        commentNode.setProperty(SnowflakeDdlLexicon.TARGET_OBJECT_TYPE, objectType);

        markEndOfStatement(tokens, commentNode);

        return commentNode;
    }

    /**
     * Utility method designed to parse columns within an ALTER TABLE ADD statement.
     * 
     * @param tokens the tokenized {@link DdlTokenStream} of the DDL input content; may not be null
     * @param tableNode the parent {@link AstNode} node; may not be null
     * @param isAlterTable
     * @throws ParsingException
     */
    protected void parseColumns( DdlTokenStream tokens,
                                 AstNode tableNode,
                                 boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        String tableElementString = getTableElementsString(tokens, false);

        DdlTokenStream localTokens = new DdlTokenStream(tableElementString, DdlTokenStream.ddlTokenizer(false), false);

        localTokens.start();

        StringBuffer unusedTokensSB = new StringBuffer();

        do {
            if (isColumnDefinitionStart(localTokens)) {
                parseColumnDefinition(localTokens, tableNode, isAlterTable);
            } else {
                // THIS IS AN ERROR. NOTHING FOUND.
                // NEED TO absorb tokens
                unusedTokensSB.append(SPACE).append(localTokens.consume());
            }
        } while (localTokens.canConsume(COMMA));

        if (unusedTokensSB.length() > 0) {
            String msg = DdlSequencerI18n.unusedTokensParsingColumnDefinition.text(tableNode.getName());
            DdlParserProblem problem = new DdlParserProblem(Problems.WARNING, getCurrentMarkedPosition(), msg);
            problem.setUnusedSource(unusedTokensSB.toString());
            addProblem(problem, tableNode);
        }
    }


    private AstNode parseCreateFunctionStatement( DdlTokenStream tokens,
                                                  AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);

        boolean isReplace = tokens.canConsume(STMT_CREATE_OR_REPLACE_FUNCTION);

        tokens.canConsume(STMT_CREATE_FUNCTION);

        String name = parseName(tokens);

        AstNode node = nodeFactory().node(name, parentNode, TYPE_CREATE_FUNCTION_STATEMENT);

        if (isReplace) {
            // TODO: SET isReplace = TRUE to node (possibly a cnd mixin of "replaceable"
        }

        parseUntilTerminator(tokens);

        markEndOfStatement(tokens, node);

        return node;
    }
    
    protected AstNode parseCreateSequenceStatement(final DdlTokenStream tokens, final AstNode parentNode)
            throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        /*
        CREATE [ OR REPLACE ] SEQUENCE [ IF NOT EXISTS ] <name>
          [ WITH ]
          [ START [ WITH ] [ = ] <initial_value> ]
          [ INCREMENT [ BY ] [ = ] <sequence_interval> ]
          [ COMMENT = '<string_literal>' ] */

        markStartOfStatement(tokens);
        tokens.consume(CREATE);
        tokens.canConsume("OR", "REPLACE");
        tokens.consume("SEQUENCE");
        tokens.canConsume(IF_NOT_EXISTS);


        final String sequenceName = parseName(tokens);
        AstNode sequenceNode = nodeFactory().node(sequenceName, parentNode, TYPE_CREATE_SEQUENCE_STATEMENT);
        
        boolean lastMatched = true;
        while (tokens.hasNext() && !isTerminator(tokens) && lastMatched) {
            
            if (tokens.canConsume("INCREMENT")) {
                tokens.canConsume("BY");
                tokens.canConsume("=");
                String value = tokens.consume();
                long longValue = Long.parseLong(value);
                sequenceNode.setProperty(SEQ_INCREMENT_BY, longValue);
                
            } else if (tokens.canConsume("START")) {
                tokens.canConsume("WITH");
                tokens.canConsume("=");
                String value = tokens.consume();
                long longValue = Long.parseLong(value);
                sequenceNode.setProperty(SEQ_START_WITH, longValue);
                
            } else if(tokens.canConsume("WITH")) {
                // nop
            } else if(tokens.canConsume("COMMENT", "=")) {
                String comment = tokens.consume();
                sequenceNode.setProperty(COMMENT, comment);
            } else {
                lastMatched = false;
            }
        }
        
        parseUntilTerminator(tokens);

        markEndOfStatement(tokens, sequenceNode);
        return sequenceNode;
    }
    

    /**
     * {@inheritDoc}
     * 
     * @see StandardDdlParser#getValidSchemaChildTypes()
     */
    @Override
    protected String[] getValidSchemaChildTypes() {
        return SnowflakeStatementStartPhrases.VALID_SCHEMA_CHILD_STMTS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see StandardDdlParser#getDataTypeStartWords()
     */
    @Override
    protected List<String> getCustomDataTypeStartWords() {
        return SnowflakeDataTypes.CUSTOM_DATATYPE_START_WORDS;
    }

    class SnowflakeDataTypeParser extends DataTypeParser {


        public SnowflakeDataTypeParser() {
            super();
        }

        /**
         * {@inheritDoc}
         * 
         * @see DataTypeParser#isCustomDataType(DdlTokenStream)
         */
        @Override
        protected boolean isCustomDataType( DdlTokenStream tokens ) throws ParsingException {
            // Loop through the registered statement start string arrays and look for exact matches.

            for (String[] stmts : snowflakeDataTypeStrings) {
                if (tokens.matches(stmts)) return true;
            }
            return super.isCustomDataType(tokens);
        }

        /**
         * {@inheritDoc}
         * 
         * @see DataTypeParser#parseBitStringType(DdlTokenStream)
         */
        @Override
        protected DataType parseBitStringType( DdlTokenStream tokens ) throws ParsingException {
            return super.parseBitStringType(tokens);
        }

        /**
         * {@inheritDoc}
         * 
         * @see DataTypeParser#parseCharStringType(DdlTokenStream)
         */
        @Override
        protected DataType parseCharStringType( DdlTokenStream tokens ) throws ParsingException {

            DataType dataType = null;
            String typeName = null;

            // EDWM-1251
            if (tokens.matches(DataTypes.DTYPE_CHARACTER_VARYING)) {
                typeName = getStatementTypeName(DataTypes.DTYPE_CHARACTER_VARYING);
                dataType = new DataType(typeName);
                consume(tokens, dataType, false,
                        DataTypes.DTYPE_CHARACTER_VARYING);
                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }
            } else if (tokens.matches(SnowflakeDataTypes.DTYPE_STRING) || tokens.matches(SnowflakeDataTypes.DTYPE_TEXT)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, false);
                dataType.setName(typeName);
                if (tokens.matches(L_PAREN)) {
                    tryParseAndSetLength(tokens, dataType, typeName);
                }
            } else {
                dataType = super.parseCharStringType(tokens);
            }

            tokens.canConsume("FOR", "BIT", "DATA");

            return dataType;
        }

        /**
         * {@inheritDoc}
         * 
         * @see DataTypeParser#parseCustomType(DdlTokenStream)
         */
        @Override
        protected DataType parseCustomType( DdlTokenStream tokens ) throws ParsingException {
            
            DataType result = null;
            String typeName = null;

            if (tokens.matches(SnowflakeDataTypes.DTYPE_FLOAT4) || tokens.matches(SnowflakeDataTypes.DTYPE_FLOAT8)) {
                typeName = tokens.consume();
                result = new DataType(typeName);
                if (tokens.matches('(')) {
                    tryParseAndSetLength(tokens, result, typeName);
                }
                
            } else if (tokens.matches(SnowflakeDataTypes.DTYPE_NUMBER) || tokens.matches(SnowflakeDataTypes.DTYPE_DECIMAL)
                || tokens.matches(SnowflakeDataTypes.DTYPE_NUMERIC) || tokens.matches(SnowflakeDataTypes.DTYPE_INT)
                || tokens.matches(SnowflakeDataTypes.DTYPE_INTEGER) || tokens.matches(SnowflakeDataTypes.DTYPE_BIGINT)
                || tokens.matches(SnowflakeDataTypes.DTYPE_SMALLINT) || tokens.matches(SnowflakeDataTypes.DTYPE_FLOAT)
                || tokens.matches(SnowflakeDataTypes.DTYPE_FLOAT4) || tokens.matches(SnowflakeDataTypes.DTYPE_FLOAT8)
                || tokens.matches(SnowflakeDataTypes.DTYPE_DOUBLE) || tokens.matches(SnowflakeDataTypes.DTYPE_DOUBLE_PRECISION)
                || tokens.matches(SnowflakeDataTypes.DTYPE_REAL) || tokens.matches(SnowflakeDataTypes.DTYPE_VARCHAR)
                || tokens.matches(SnowflakeDataTypes.DTYPE_CHAR) || tokens.matches(SnowflakeDataTypes.DTYPE_CHARACTER)
                || tokens.matches(SnowflakeDataTypes.DTYPE_STRING) || tokens.matches(SnowflakeDataTypes.DTYPE_TEXT)
                || tokens.matches(SnowflakeDataTypes.DTYPE_BINARY) || tokens.matches(SnowflakeDataTypes.DTYPE_VARBINARY)
                || tokens.matches(SnowflakeDataTypes.DTYPE_BOOLEAN) || tokens.matches(SnowflakeDataTypes.DTYPE_DATE)
                || tokens.matches(SnowflakeDataTypes.DTYPE_DATETIME) || tokens.matches(SnowflakeDataTypes.DTYPE_TIME)
                || tokens.matches(SnowflakeDataTypes.DTYPE_TIMESTAMP) || tokens.matches(SnowflakeDataTypes.DTYPE_TIMESTAMP_LTZ)
                || tokens.matches(SnowflakeDataTypes.DTYPE_TIMESTAMP) || tokens.matches(SnowflakeDataTypes.DTYPE_TIMESTAMP_NTZ)
                || tokens.matches(SnowflakeDataTypes.DTYPE_TIMESTAMP) || tokens.matches(SnowflakeDataTypes.DTYPE_TIMESTAMP_TZ)
                || tokens.matches(SnowflakeDataTypes.DTYPE_VARIANT) || tokens.matches(SnowflakeDataTypes.DTYPE_OBJECT)
                || tokens.matches(SnowflakeDataTypes.DTYPE_ARRAY) || tokens.matches(SnowflakeDataTypes.DTYPE_GEOGRAPHY)) {
                typeName = tokens.consume();
                result = new DataType(typeName);
            }

            if (result == null) {
                super.parseCustomType(tokens);
            }
            return result;
        }

        /**
         * {@inheritDoc}
         * 
         * @see DataTypeParser#parseDateTimeType(DdlTokenStream)
         */
        @Override
        protected DataType parseDateTimeType( DdlTokenStream tokens ) throws ParsingException {
            DataType dtype = super.parseDateTimeType(tokens);

            tokens.canConsume("WITHOUT", "TIME", "ZONE");

            return dtype;
        }

        /**
         * {@inheritDoc}
         * 
         * @see DataTypeParser#parseExactNumericType(DdlTokenStream)
         */
        @Override
        protected DataType parseExactNumericType( DdlTokenStream tokens ) throws ParsingException {
            DataType dataType = null;
            String typeName = null;
            if (tokens.matches(SnowflakeDataTypes.DTYPE_NUMBER)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, false);
                dataType.setName(typeName);
                parsePrecisionAndScale(tokens, dataType, typeName);
            } else {
                return super.parseExactNumericType(tokens);
            }
            return dataType;
        }

        @Override
        protected boolean isDatatype(DdlTokenStream tokens, int type) {
            boolean result = super.isDatatype(tokens, type);
            if (result) {
                return true;
            }
            switch (type) {
                case DataTypes.DTYPE_CODE_CHAR_STRING: {
                    for (String[] stmts : list(SnowflakeDataTypes.DTYPE_STRING, SnowflakeDataTypes.DTYPE_TEXT)) {
                        if (tokens.matches(stmts)) {
                            return true;
                        }
                    }
                }
                break;
                case DataTypes.DTYPE_CODE_EXACT_NUMERIC: {
                    for (String[] stmts : list(SnowflakeDataTypes.DTYPE_NUMBER)) {
                        if (tokens.matches(stmts)) {
                            return true;
                        }
                    }
                }
                break;
            }

            return false;
        }

        @Override
        public boolean isDatatype(DdlTokenStream tokens) {
            boolean result = super.isDatatype(tokens);
            if (result) {
                return true;
            }
            for (String[] stmts : list(SnowflakeDataTypes.DTYPE_STRING, SnowflakeDataTypes.DTYPE_TEXT, SnowflakeDataTypes.DTYPE_NUMBER)) {
                if (tokens.matches(stmts)) {
                    return true;
                }
            }
            return false;
        }

    }

    List<String[]> list(String[] ...args) {
        List<String[]> result = new ArrayList<>();
        for (String[] a : args) {
            result.add(a);
        }
        return result;
    }

}
