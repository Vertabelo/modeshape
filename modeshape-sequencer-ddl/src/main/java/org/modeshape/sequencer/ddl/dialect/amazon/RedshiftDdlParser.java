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
package org.modeshape.sequencer.ddl.dialect.amazon;

import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.common.text.TokenStream;
import org.modeshape.sequencer.ddl.DdlParserProblem;
import org.modeshape.sequencer.ddl.DdlSequencerI18n;
import org.modeshape.sequencer.ddl.DdlTokenStream;
import org.modeshape.sequencer.ddl.DdlTokenStream.DdlTokenizer;
import org.modeshape.sequencer.ddl.StandardDdlParser;
import org.modeshape.sequencer.ddl.datatype.DataType;
import org.modeshape.sequencer.ddl.datatype.DataTypeParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import java.util.ArrayList;
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
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_COLUMN_DEFINITION;
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

import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.*;

/**
 * Redshift-specific DDL Parser. Includes custom data types as well as custom DDL statements.
 */
public class RedshiftDdlParser extends StandardDdlParser
    implements RedshiftDdlConstants, RedshiftDdlConstants.RedshiftStatementStartPhrases {

    /**
     * The Redshift parser identifier.
     */
    public static final String ID = "REDSHIFT";
    public static final int TRIES_OF_PARSE = 1000;

    static List<String[]> redshiftDataTypeStrings = new ArrayList<String[]>();

    // SQL COMMANDS FOUND @ https://docs.aws.amazon.com/redshift/latest/dg/c_SQL_commands.html

    private static final String TERMINATOR = ";";

    public RedshiftDdlParser() {
        setDatatypeParser(new RedshiftDataTypeParser());
        initialize();
    }

    private void initialize() {

        setDoUseTerminator(true);

        setTerminator(TERMINATOR);

        redshiftDataTypeStrings.addAll(RedshiftDataTypes.CUSTOM_DATATYPE_START_PHRASES);
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
        tokens.registerKeyWords(RedshiftDataTypes.CUSTOM_DATATYPE_START_WORDS);
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
        } else if (tokens.matches(STMT_ALTER_GROUP)) {
            return parseStatement(tokens, STMT_ALTER_GROUP, parentNode, TYPE_ALTER_GROUP_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_LIBRARY)) {
            return parseStatement(tokens, STMT_ALTER_LIBRARY, parentNode, TYPE_ALTER_LIBRARY_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_PROCEDURE)) {
            return parseStatement(tokens, STMT_ALTER_PROCEDURE, parentNode, TYPE_ALTER_PROCEDURE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SCHEMA)) {
            return parseStatement(tokens, STMT_ALTER_SCHEMA, parentNode, TYPE_ALTER_SCHEMA_STATEMENT);
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
        // TODO: Need to flesh out and store more info on alterTableStatement properties

        // NOTE: Not sure the rules of Postgress here. It appears that you can have comma separated clauses
        // but can't find many examples. Also don't know if you can mix clause types
        // EXAMPLE:
        //
        // ALTER TABLE distributors
        // ALTER COLUMN address TYPE varchar(80),
        // DROP COLUMN name RESTRICTED;

        // --ALTER TABLE [ ONLY ] name [ * ]
        // -- action [, ... ]

        // --where action is one of:
        // -- ADD [ COLUMN ] column type [ column_constraint [ ... ] ]
        // -- DROP [ COLUMN ] column [ RESTRICT | CASCADE ]
        // -- ALTER [ COLUMN ] column [ SET DATA ] TYPE type [ USING expression ]
        // -- ALTER [ COLUMN ] column SET DEFAULT expression
        // -- ALTER [ COLUMN ] column DROP DEFAULT
        // -- ALTER [ COLUMN ] column { SET | DROP } NOT NULL
        // -- ALTER [ COLUMN ] column SET STATISTICS integer
        // -- ALTER [ COLUMN ] column SET STORAGE { PLAIN | EXTERNAL | EXTENDED | MAIN }
        // -- ADD table_constraint
        // -- DROP CONSTRAINT constraint_name [ RESTRICT | CASCADE ]
        // -- DISABLE TRIGGER [ trigger_name | ALL | USER ]
        // -- ENABLE TRIGGER [ trigger_name | ALL | USER ]
        // -- ENABLE REPLICA TRIGGER trigger_name
        // -- ENABLE ALWAYS TRIGGER trigger_name
        // -- DISABLE RULE rewrite_rule_name
        // -- ENABLE RULE rewrite_rule_name
        // -- ENABLE REPLICA RULE rewrite_rule_name
        // -- ENABLE ALWAYS RULE rewrite_rule_name
        // -- CLUSTER ON index_name
        // -- SET WITHOUT CLUSTER
        // -- SET WITH OIDS
        // -- SET WITHOUT OIDS
        // -- SET ( storage_parameter = value [, ... ] )
        // -- RESET ( storage_parameter [, ... ] )
        // -- INHERIT parent_table
        // -- NO INHERIT parent_table
        // -- OWNER TO new_owner
        // -- SET TABLESPACE new_tablespace
        // =========== MISC.............
        // --ALTER TABLE [ ONLY ] name [ * ]
        // -- RENAME [ COLUMN ] column TO new_column
        // --ALTER TABLE name
        // -- RENAME TO new_name
        // --ALTER TABLE name new_tablespace
        // -- SET SCHEMA new_schema

        tokens.consume(); // consumes 'ALTER'
        tokens.consume("TABLE");

        tokens.canConsume("ONLY");
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
            if (isTableConstraint(tokens)) {
                parseTableConstraint(tokens, alterTableNode, true);
            } else {
                parseSingleCommaTerminatedColumnDefinition(tokens, alterTableNode, true);
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
            } else {
                // ALTER TABLE supplier
                // DROP COLUMN supplier_name;
                tokens.canConsume("COLUMN"); // "COLUMN" is optional

                String columnName = parseName(tokens);

                AstNode columnNode = nodeFactory().node(columnName, alterTableNode, TYPE_DROP_COLUMN_DEFINITION);

                if (tokens.canConsume(DropBehavior.CASCADE)) {
                    columnNode.setProperty(DROP_BEHAVIOR, DropBehavior.CASCADE);
                } else if (tokens.canConsume(DropBehavior.RESTRICT)) {
                    columnNode.setProperty(DROP_BEHAVIOR, DropBehavior.RESTRICT);
                }
            }
        } else if (tokens.matches("ALTER")) {
            // -- ALTER [ COLUMN ] column SET STORAGE { PLAIN | EXTERNAL | EXTENDED | MAIN }
            // -- ALTER [ COLUMN ] columnnew_tablespace SET STATISTICS integer
            // -- ALTER [ COLUMN ] column DROP DEFAULT
            // -- ALTER [ COLUMN ] column [ SET DATA ] TYPE type [ USING expression ]
            // -- ALTER [ COLUMN ] column SET DEFAULT expression
            // -- ALTER [ COLUMN ] column { SET | DROP } NOT NULL

            tokens.consume("ALTER");
            tokens.canConsume("COLUMN");
            String columnName = parseName(tokens);

            AstNode columnNode = nodeFactory().node(columnName, alterTableNode, TYPE_ALTER_COLUMN_DEFINITION);

            if (tokens.canConsume("SET", "STORAGE")) {
                tokens.consume(); // { PLAIN | EXTERNAL | EXTENDED | MAIN }
            } else if (tokens.canConsume("SET", "STATISTICS")) {
                tokens.consume(); // integer
            } else if (tokens.canConsume("DROP", "DEFAULT")) {

            } else if (tokens.canConsume("SET", "DATA")) {
                tokens.consume("TYPE");
                DataType datatype = getDatatypeParser().parse(tokens);

                getDatatypeParser().setPropertiesOnNode(columnNode, datatype);

                if (tokens.canConsume("USING")) {
                    // TODO: Not storing the following expression in properties.
                    parseUntilCommaOrTerminator(tokens);
                }
            } else if (tokens.canConsume("TYPE")) {
                DataType datatype = getDatatypeParser().parse(tokens);

                getDatatypeParser().setPropertiesOnNode(columnNode, datatype);

                if (tokens.canConsume("USING")) {
                    // TODO: Not storing the following expression in properties.
                    parseUntilCommaOrTerminator(tokens);
                }
            } else if (tokens.matches("SET", "DEFAULT")) {
                tokens.consume("SET");
                parseDefaultClause(tokens, columnNode);
            } else if (tokens.matches("SET") || tokens.matches("DROP")) {
                tokens.consume(); // { SET | DROP }
                tokens.canConsume("NOT", "NULL");
                tokens.canConsume("NULL");
            } else {
                System.out.println("  WARNING:  Option not found for ALTER TABLE - ALTER COLUMN. Check your DDL for incomplete statement.");
            }

        } else if (tokens.canConsume("ENABLE")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            StringBuffer sb = new StringBuffer("ENABLE");
            // -- ENABLE TRIGGER [ trigger_name | ALL | USER ]
            // -- ENABLE REPLICA TRIGGER trigger_name
            // -- ENABLE REPLICA RULE rewrite_rule_name
            // -- ENABLE ALWAYS TRIGGER trigger_name
            // -- ENABLE ALWAYS RULE rewrite_rule_name
            // -- ENABLE RULE rewrite_rule_name
            if (tokens.canConsume("TRIGGER")) {
                sb.append(SPACE).append("TRIGGER");
                if (!tokens.matches(getTerminator())) {
                    sb.append(SPACE).append(parseName(tokens)); // [ trigger_name | ALL | USER ]
                }
            } else if (tokens.canConsume("REPLICA", "TRIGGER")) {
                sb.append(SPACE).append("REPLICA TRIGGER");
                sb.append(SPACE).append(parseName(tokens)); // trigger_name
            } else if (tokens.canConsume("REPLICA", "RULE")) {
                sb.append(SPACE).append("REPLICA RULE");
                sb.append(SPACE).append(parseName(tokens)); // rewrite_rule_name
            } else if (tokens.canConsume("ALWAYS", "TRIGGER")) {
                sb.append(SPACE).append("ALWAYS TRIGGER");
                sb.append(SPACE).append(parseName(tokens)); // trigger_name
            } else if (tokens.canConsume("ALWAYS", "RULE")) {
                sb.append(SPACE).append("ALWAYS RULE");
                sb.append(SPACE).append(parseName(tokens)); // rewrite_rule_name
            } else if (tokens.canConsume("RULE")) {
                sb.append(SPACE).append("RULE");
                sb.append(SPACE).append(parseName(tokens)); // rewrite_rule_name
            } else {
                System.out.println("  WARNING:  Option not found for ALTER TABLE - ENABLE XXXX. Check your DDL for incomplete statement.");
            }
            optionNode.setProperty(VALUE, sb.toString());
        } else if (tokens.canConsume("DISABLE")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            StringBuffer sb = new StringBuffer("DISABLE");
            // -- DISABLE TRIGGER [ trigger_name | ALL | USER ]
            // -- DISABLE RULE rewrite_rule_name
            if (tokens.canConsume("TRIGGER")) {
                sb.append(SPACE).append("TRIGGER");
                if (!tokens.matches(getTerminator())) {
                    sb.append(SPACE).append(parseName(tokens)); // [ trigger_name | ALL | USER ]
                }
            } else if (tokens.canConsume("RULE")) {
                sb.append(SPACE).append("RULE");
                sb.append(SPACE).append(parseName(tokens)); // rewrite_rule_name
            } else {
                System.out.println("  WARNING:  Option not found for ALTER TABLE - DISABLE XXXX. Check your DDL for incomplete statement.");
            }
            optionNode.setProperty(VALUE, sb.toString());
        } else if (tokens.canConsume("CLUSTER", "ON")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            // -- CLUSTER ON index_name
            String indexName = parseName(tokens); // index_name
            optionNode.setProperty(VALUE, "CLUSTER ON" + SPACE + indexName);
        } else if (tokens.canConsume("OWNER", "TO")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            // -- OWNER TO new_owner
            optionNode.setProperty(VALUE, "OWNER TO" + SPACE + parseName(tokens));
        } else if (tokens.canConsume("INHERIT")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            // -- INHERIT parent_table
            optionNode.setProperty(VALUE, "INHERIT" + SPACE + parseName(tokens));
        } else if (tokens.canConsume("NO", "INHERIT")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            // -- NO INHERIT parent_table
            optionNode.setProperty(VALUE, "NO INHERIT" + SPACE + parseName(tokens));
        } else if (tokens.canConsume("SET", "TABLESPACE")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            // -- SET TABLESPACE new_tablespace
            optionNode.setProperty(VALUE, "SET TABLESPACE" + SPACE + parseName(tokens));
        } else if (tokens.canConsume("SET", "WITHOUT", "CLUSTER")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            optionNode.setProperty(VALUE, "SET WITHOUT CLUSTER");
        } else if (tokens.canConsume("SET", "WITHOUT", "OIDS")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            optionNode.setProperty(VALUE, "SET WITHOUT OIDS");
        } else if (tokens.canConsume("SET", "WITH", "OIDS")) {
            AstNode optionNode = nodeFactory().node("action", alterTableNode, TYPE_STATEMENT_OPTION);
            optionNode.setProperty(VALUE, "SET WITH OIDS");
        } else if (tokens.canConsume("RENAME", "TO")) {
            // --ALTER TABLE name
            // -- RENAME TO new_name
            String newTableName = parseName(tokens);
            alterTableNode.setProperty(NEW_NAME, newTableName);

        } else if (tokens.canConsume("RENAME")) {
            // --ALTER TABLE [ ONLY ] name [ * ]
            // -- RENAME [ COLUMN ] column TO new_column
            tokens.canConsume("COLUMN");
            String oldColumnName = parseName(tokens); // OLD COLUMN NAME
            tokens.consume("TO");
            String newColumnName = parseName(tokens); // NEW COLUMN NAME
            AstNode renameColumnNode = nodeFactory().node(oldColumnName, alterTableNode, TYPE_RENAME_COLUMN);
            renameColumnNode.setProperty(NEW_NAME, newColumnName);
        } else if (tokens.canConsume("SET", "SCHEMA")) {
            // ALTER TABLE myschema.distributors SET SCHEMA your schema;
            String schemaName = parseName(tokens);
            alterTableNode.setProperty(SCHEMA_NAME, schemaName);
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

        // in some cases the following loop does not finish, safeguards for this case
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
        } else if (tokens.matches(STMT_CREATE_DATABASE)) {
            return parseStatement(tokens, STMT_CREATE_DATABASE, parentNode, TYPE_CREATE_DATABASE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_EXTERNAL_SCHEMA)) {
            return parseStatement(tokens,
                    STMT_CREATE_EXTERNAL_SCHEMA,
                                  parentNode,
                                  TYPE_CREATE_FOREIGN_DATA_WRAPPER_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_FUNCTION)) {
            return parseCreateFunctionStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_OR_REPLACE_FUNCTION)) {
            return parseCreateFunctionStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_GROUP)) {
            return parseStatement(tokens, STMT_CREATE_GROUP, parentNode, TYPE_CREATE_GROUP_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_LIBRARY)) {
            return parseStatement(tokens, STMT_CREATE_LIBRARY, parentNode, TYPE_CREATE_LIBRARY_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_PROCEDURE)) {
            return parseStatement(tokens, STMT_CREATE_PROCEDURE, parentNode, TYPE_CREATE_PROCEDURE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_USER)) {
            return parseStatement(tokens, STMT_CREATE_USER, parentNode, TYPE_CREATE_USER_STATEMENT);
        }

        return super.parseCreateStatement(tokens, parentNode);
    }



    @Override
    protected AstNode parseCreateTableStatement( DdlTokenStream tokens,
                                                 AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);

        tokens.consume("CREATE"); // CREATE

        tokens.canConsumeAnyOf("LOCAL", "GLOBAL");
        tokens.canConsumeAnyOf("TEMP", "TEMPORARY");


        tokens.consume("TABLE"); // TABLE

        tokens.canConsume(IF_NOT_EXISTS);

        String tableName = parseName(tokens);
        AstNode tableNode = nodeFactory().node(tableName, parentNode, TYPE_CREATE_TABLE_STATEMENT);


        parseColumnsAndConstraints(tokens, tableNode);

        // [ BACKUP { YES | NO } ]
        // [table_attribute]
        // and table_attributes are:
        //  [ DISTSTYLE { AUTO | EVEN | KEY | ALL } ]
        //  [ DISTKEY ( column_name ) ]
        //  [ [COMPOUND | INTERLEAVED ] SORTKEY ( column_name [, ...] ) ]
        parseCreateTableOptions(tokens, tableNode);

        markEndOfStatement(tokens, tableNode);

        return tableNode;
    }



    /**
     * Implementation of column definition for Redshift
     *
     * { column_name data_type [column_attributes] [ column_constraints ]
     *
     * { <data type> } [ <default clause> ] [ <column constraint definition>... ]
     *
     * @param tokens the {@link DdlTokenStream} representing the tokenized DDL content; may not be null
     * @param tableNode
     * @param isAlterTable true if in-line constraint is part of add column in alter table statement
     * @throws ParsingException
     */
    protected void parseColumnDefinition( DdlTokenStream tokens,
                                          AstNode tableNode,
                                          boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        tokens.canConsume("COLUMN");
        String columnName = parseName(tokens);
        DataType datatype = getDatatypeParser().parse(tokens);

        AstNode columnNode = nodeFactory().node(columnName, tableNode, TYPE_COLUMN_DEFINITION);

        getDatatypeParser().setPropertiesOnNode(columnNode, datatype);

        // Now clauses and constraints can be defined in any order, so we need to keep parsing until we get to a comma
        StringBuffer unusedTokensSB = new StringBuffer();

        while (tokens.hasNext() && !tokens.matches(COMMA)) {
            boolean parsedDefaultClause = parseDefaultClause(tokens, columnNode);
            boolean parsedIdentity = parseIdentity(tokens, columnNode);
            boolean parsedEncode = parseEncode(tokens, columnNode);
            boolean parsedDistKey = parseDistKey(tokens, tableNode, columnName);
            boolean parsedSortKey = parseSortKey(tokens, tableNode, columnName);
            boolean parsedConstraint = parseColumnConstraint(tokens, columnNode, isAlterTable);

            if (!parsedDefaultClause
                    && !parsedIdentity
                    && !parsedEncode
                    && !parsedDistKey
                    && !parsedSortKey
                    && !parsedConstraint) {
                // THIS IS AN ERROR. NOTHING FOUND.
                // NEED TO absorb tokens
                unusedTokensSB.append(SPACE).append(tokens.consume());
            }

            tokens.canConsume(DdlTokenizer.COMMENT);
        }

        if (unusedTokensSB.length() > 0) {
            String msg = DdlSequencerI18n.unusedTokensParsingColumnDefinition.text(tableNode.getName());
            DdlParserProblem problem = new DdlParserProblem(Problems.WARNING, Position.EMPTY_CONTENT_POSITION, msg);
            problem.setUnusedSource(unusedTokensSB.toString());
            addProblem(problem, tableNode);
        }
    }



    private boolean parseIdentity(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("IDENTITY", L_PAREN)) {
            if (tokens.matches(TokenStream.ANY_VALUE, COMMA, TokenStream.ANY_VALUE)) {
                Integer seed = tokens.consumeInteger();
                tokens.consume();
                Integer step = tokens.consumeInteger();
                tokens.consume();
                columnNode.setProperty(IDENTITY, true);
                columnNode.setProperty(IDENTITY_SEED, seed);
                columnNode.setProperty(IDENTITY_STEP, step);
                return true;
            }
        }
        return false;
    }


    private boolean parseEncode(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("ENCODE")) {
            String encoding = tokens.consume();
            columnNode.setProperty(ENCODING, encoding);
            return true;
        }
        return false;
    }

    private boolean parseSortKey(DdlTokenStream tokens, AstNode tableNode, String columnName) {
        if (tokens.canConsume("DISTKEY")) {
            tableNode.setProperty(DISTKEY, columnName);
            return true;
        }
        return false;
    }

    private boolean parseDistKey(DdlTokenStream tokens, AstNode tableNode, String columnName) {
        if (tokens.canConsume("SORTKEY")) {
            String sortKey = (String)tableNode.getProperty(COMPOUND_SORTKEY);
            if (sortKey != null) {
                sortKey = sortKey + "," + columnName;
            } else {
                sortKey = columnName;
            }
            tableNode.setProperty(COMPOUND_SORTKEY, columnName);
            return true;
        }
        return false;
    }


    @Override
    protected void parseNextCreateTableOption( DdlTokenStream tokens,
                                               AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if(tokens.canConsume("DISTSTYLE")) {
            // [ DISTSTYLE tablespace_name ]
            String diststyle = parseName(tokens);
            parentNode.setProperty(DISTSTYLE, diststyle);

        } else if (tokens.matches("DISTKEY",  L_PAREN)) {
            tokens.consume();
            // [  DISTKEY ( column_name ) ]
            String columnName = parseContentBetweenParens(tokens);
            parentNode.setProperty(DISTKEY, columnName);

        } else if (tokens.canConsume("COMPOUND", "SORTKEY")) {
            String columnNames = parseContentBetweenParens(tokens);
            parentNode.setProperty(COMPOUND_SORTKEY, columnNames);
        } else if(tokens.canConsume("INTERLEAVED", "SORTKEY")) {
            String columnNames = parseContentBetweenParens(tokens);
            parentNode.setProperty(INTERLEAVED_SORTKEY, columnNames);
        } else if (tokens.canConsume("BACKUP")) {
            String backup = parseName(tokens);
            if ("YES".equals(backup.toUpperCase())) {
                parentNode.setProperty(BACKUP, true);
            }
            if ("NO".equals(backup.toUpperCase())) {
                parentNode.setProperty(BACKUP, false);
            }
        }
    }

    @Override
    protected boolean areNextTokensCreateTableOptions( DdlTokenStream tokens ) throws ParsingException {
        assert tokens != null;

        boolean result = false;

        if (tokens.matches("DISTSTYLE") || tokens.matches("DISTKEY")
                || tokens.matches("COMPOUND", "SORTKEY")
                || tokens.matches("INTERLEAVED", "SORTKEY")
                || tokens.matches("BACKUP")) {
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
        // CREATE [ OR REPLACE ] [ TEMP | TEMPORARY ] VIEW name [ ( column_name [, ...] ) ]
        // [ WITH ( view_option_name [= view_option_value] [, ... ] ) ]
        // AS query

        String stmtType = "CREATE";
        tokens.consume("CREATE");

        boolean orReplaceClause = false;
        if (tokens.canConsume("OR", "REPLACE")) {
            orReplaceClause = true;
            stmtType = stmtType + SPACE + "OR REPLACE";
        }

        boolean isMaterialized = false;
        if (tokens.canConsume("MATERIALIZED")) {
            isMaterialized = true;
            stmtType = stmtType + SPACE + "MATERIALIZED";
        }

        // skip tokens
        tokens.canConsume("TEMP");
        tokens.canConsume("TEMPORARY");

        tokens.consume("VIEW");
        stmtType = stmtType + SPACE + "VIEW";

        String name = parseName(tokens);

        AstNode createViewNode = nodeFactory().node(name, parentNode, TYPE_CREATE_VIEW_STATEMENT);
        createViewNode.setProperty(OR_REPLACE_CLAUSE, orReplaceClause);
        createViewNode.setProperty(RedshiftDdlLexicon.MATERIALIZED, isMaterialized);

        // CONSUME COLUMNS
        parseColumnNameList(tokens, createViewNode, TYPE_COLUMN_REFERENCE);

        // WITH CHECK OPTION
        if (tokens.canConsume("WITH")) {
            String withCheckOption = "CASCADED";

            if (tokens.matches("LOCAL")) {
                withCheckOption = "LOCAL";
                tokens.consume("LOCAL");
            } else if (tokens.matches("CASCADED")) {
                withCheckOption = "CASCADED";
                tokens.consume("CASCADED");
            }

            tokens.consume("CHECK", "OPTION");
            createViewNode.setProperty(RedshiftDdlLexicon.WITH_CHECK_OPTION, withCheckOption);
        }

        tokens.consume("AS");

        boolean didUseTermintar = doUseTerminator();
        setDoUseTerminator(true);
        String queryExpression = parseUntilTerminator(tokens);
        setDoUseTerminator(didUseTermintar);

        int withNoSchemaBindingIndex = queryExpression.toUpperCase().indexOf("WITH NO SCHEMA BINDING");
        if(withNoSchemaBindingIndex >= 0) {
            queryExpression = queryExpression.substring(0, withNoSchemaBindingIndex);
            createViewNode.setProperty(WITH_NO_SCHEMA_BINDING, true);
        }

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
        } else if (tokens.matches(STMT_LOCK_TABLE)) {
            return parseStatement(tokens, STMT_LOCK_TABLE, parentNode, TYPE_LOCK_TABLE_STATEMENT);
        } else if (tokens.matches(STMT_PREPARE)) {
            return parseStatement(tokens, STMT_PREPARE, parentNode, TYPE_PREPARE_STATEMENT);
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
        } if (tokens.matches(STMT_DROP_FUNCTION)) {
            return parseStatement(tokens, STMT_DROP_FUNCTION, parentNode, TYPE_DROP_FUNCTION_STATEMENT);
        } else if (tokens.matches(STMT_DROP_GROUP)) {
            return parseSimpleDropStatement(tokens, STMT_DROP_GROUP, parentNode, TYPE_DROP_GROUP_STATEMENT);
        } if (tokens.matches(STMT_DROP_LIBRARY)) {
            return parseSimpleDropStatement(tokens, STMT_DROP_LIBRARY, parentNode, TYPE_DROP_LIBRARY_STATEMENT);
        } else if (tokens.matches(STMT_DROP_PROCEDURE)) {
            return parseSimpleDropStatement(tokens, STMT_DROP_PROCEDURE, parentNode, TYPE_DROP_PROCEDURE_STATEMENT);
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

    private boolean matchesCreateTableStatement(DdlTokenStream tokens) {
        return tokens.matches(STMT_CREATE_TEMP_TABLE)
                || tokens.matches(STMT_CREATE_LOCAL_TEMP_TABLE)
                || tokens.matches(STMT_CREATE_TABLE_IF_NOT_EXISTS)
                || tokens.matches(joinMatches(STMT_CREATE_TEMP_TABLE, IF_NOT_EXISTS))
                || tokens.matches(joinMatches(STMT_CREATE_LOCAL_TEMP_TABLE, IF_NOT_EXISTS))
                || tokens.matches(joinMatches(STMT_CREATE_GLOBAL_TEMPORARY_TABLE, IF_NOT_EXISTS))
                || tokens.matches(joinMatches(STMT_CREATE_LOCAL_TEMPORARY_TABLE, IF_NOT_EXISTS));
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
            COMMENT ON
            {
            TABLE object_name |
            COLUMN object_name.column_name |
            CONSTRAINT constraint_name ON table_name |
            DATABASE object_name |
            VIEW object_name
            }
            IS 'text'
         */
        tokens.consume("COMMENT", "ON"); // consumes 'COMMENT' 'ON'

        String objectType = null;
        String objectName = null;
        String tableName = null;

        if (tokens.matches(TABLE)) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("COLUMN")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
        } else if (tokens.matches("CONSTRAINT")) {
            objectType = tokens.consume();
            objectName = parseName(tokens);
            tokens.consume("ON");
            tableName = parseName(tokens); // table_name
        } else if (tokens.matches("DATABASE")) {
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
            commentNode.setProperty(RedshiftDdlLexicon.TARGET_OBJECT_NAME, objectName);
        } else {
            commentNode = nodeFactory().node("commentOn", parentNode, TYPE_COMMENT_ON_STATEMENT);
        }
        commentNode.setProperty(RedshiftDdlLexicon.COMMENT, commentString);
        commentNode.setProperty(RedshiftDdlLexicon.TARGET_OBJECT_TYPE, objectType);
        if (tableName != null) {
            commentNode.setProperty(TABLE_NAME, tableName);
        }

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


    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#getValidSchemaChildTypes()
     */
    @Override
    protected String[] getValidSchemaChildTypes() {
        return RedshiftStatementStartPhrases.VALID_SCHEMA_CHILD_STMTS;
    }

    /**
     * {@inheritDoc}
     *
     * @see StandardDdlParser#getDataTypeStartWords()
     */
    @Override
    protected List<String> getCustomDataTypeStartWords() {
        return RedshiftDataTypes.CUSTOM_DATATYPE_START_WORDS;
    }

    class RedshiftDataTypeParser extends DataTypeParser {

        /**
         * {@inheritDoc}
         *
         * @see DataTypeParser#isCustomDataType(DdlTokenStream)
         */
        @Override
        protected boolean isCustomDataType( DdlTokenStream tokens ) throws ParsingException {
            // Loop through the registered statement start string arrays and look for exact matches.

            for (String[] stmts : redshiftDataTypeStrings) {
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

            if (tokens.matches(RedshiftDataTypes.DTYPE_FLOAT4) || tokens.matches(RedshiftDataTypes.DTYPE_FLOAT8)) {
                typeName = tokens.consume();
                result = new DataType(typeName);
                if (tokens.matches('(')) {
                    tryParseAndSetLength(tokens, result, typeName);
                }

            } else if (tokens.matches(RedshiftDataTypes.DTYPE_INT2) || tokens.matches(RedshiftDataTypes.DTYPE_INT4)
                || tokens.matches(RedshiftDataTypes.DTYPE_INT8)
                || tokens.matches(RedshiftDataTypes.DTYPE_BOOL) || tokens.matches(RedshiftDataTypes.DTYPE_BOOLEAN)
                || tokens.matches(RedshiftDataTypes.DTYPE_TEXT)
                || tokens.matches(RedshiftDataTypes.DTYPE_BIGINT)
                || tokens.matches(RedshiftDataTypes.DTYPE_TIMESTAMPTZ)) {
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
            return super.parseExactNumericType(tokens);
        }

    }

}
