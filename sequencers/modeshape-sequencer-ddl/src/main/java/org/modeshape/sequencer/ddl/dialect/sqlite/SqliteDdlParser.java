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
package org.modeshape.sequencer.ddl.dialect.sqlite;

import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CHECK_SEARCH_CONDITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CONSTRAINT_TYPE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NEW_NAME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NULLABLE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_REFERENCE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_STATEMENT_OPTION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_TABLE_CONSTRAINT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.VALUE;
import static org.modeshape.sequencer.ddl.dialect.sqlite.SqliteDdlLexicon.TYPE_CREATE_INDEX_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.sqlite.SqliteDdlLexicon.UNIQUE_INDEX;
import static org.modeshape.sequencer.ddl.dialect.sqlite.SqliteDdlLexicon.WHERE_CLAUSE;

import java.util.ArrayList;
import java.util.List;

import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.common.text.TokenStream;
import org.modeshape.sequencer.ddl.DdlParserProblem;
import org.modeshape.sequencer.ddl.DdlSequencerI18n;
import org.modeshape.sequencer.ddl.DdlTokenStream;
import org.modeshape.sequencer.ddl.DdlTokenStream.DdlTokenizer;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.StandardDdlParser;
import org.modeshape.sequencer.ddl.datatype.DataType;
import org.modeshape.sequencer.ddl.datatype.DataTypeParser;
import org.modeshape.sequencer.ddl.node.AstNode;

/**
 * Sqlite-specific DDL Parser. Includes custom data types as well as custom DDL statements.
 */
public class SqliteDdlParser extends StandardDdlParser
    implements SqliteDdlConstants, SqliteDdlConstants.SqliteStatementStartPhrases {

    /**
     * The Sqlite parser identifier.
     */
    @SuppressWarnings("hiding")
    public static final String ID = "SQLITE";

    static List<String[]> sqliteDataTypeStrings = new ArrayList<String[]>();

    public SqliteDdlParser() {
        super();

        setDatatypeParser(new SqliteDataTypeParser());
        initialize();
    }
    
    @Override
    protected boolean areNextTokensCreateTableOptions( final DdlTokenStream tokens ) throws ParsingException {
        // current token can't be a terminator or the next n-tokens can't be a statement  
        return (tokens.hasNext() && !isTerminator(tokens) && (tokens.computeNextStatementStartKeywordCount() == 0));
    }
    
    @Override
    protected void parseNextCreateTableOption( final DdlTokenStream tokens,
                                               final AstNode tableNode ) throws ParsingException {
        final String tableProperty = tokens.consume();
        boolean processed = false;

        // if token is a number add it to previous option
        if (tableProperty.matches("\\b\\d+\\b")) {
            final List<AstNode> options = tableNode.getChildren(TYPE_STATEMENT_OPTION);

            if (!options.isEmpty()) {
                final AstNode option = options.get(options.size() - 1);
                final String currValue = (String)option.getProperty(VALUE);
                option.setProperty(VALUE, currValue + SPACE + tableProperty);
                processed = true;
            }
        }
        
        if (!processed) {
            final AstNode tableOption = nodeFactory().node("option", tableNode, TYPE_STATEMENT_OPTION);
            tableOption.setProperty(VALUE, tableProperty);
        }
    }

    private void initialize() {
        setTerminator(DEFAULT_TERMINATOR);

        setDoUseTerminator(true);

        sqliteDataTypeStrings.addAll(SqliteDataTypes.CUSTOM_DATATYPE_START_PHRASES);
    }
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    protected void initializeTokenStream( DdlTokenStream tokens ) {
        super.initializeTokenStream(tokens);
        tokens.registerKeyWords(CUSTOM_KEYWORDS);
        tokens.registerKeyWords(SqliteDataTypes.CUSTOM_DATATYPE_START_WORDS);
        tokens.registerStatementStartPhrase(ALTER_PHRASES);
        tokens.registerStatementStartPhrase(CREATE_PHRASES);
        tokens.registerStatementStartPhrase(DROP_PHRASES);
        tokens.registerStatementStartPhrase(MISC_PHRASES);
    }
    
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
        
        if (tokens.matches(STMT_CREATE_INDEX) || tokens.matches(STMT_CREATE_UNIQUE_INDEX)) {
            return parseCreateIndex(tokens, parentNode);
            
        } else if (tokens.matches(SqliteDdlConstants.SqliteStatementStartPhrases.STMT_CREATE_TABLE)) {
            return parseCreateTableStatement(tokens, parentNode);
            
        } else if (tokens.matches(SqliteDdlConstants.SqliteStatementStartPhrases.STMT_CREATE_VIEW)) { 
            return parseCreateViewStatement(tokens, parentNode);
        }
        
        return super.parseCreateStatement(tokens, parentNode);
    }
    
    @Override
    protected AstNode parseCreateTableStatement( DdlTokenStream tokens,
                                                 AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);

        tokens.consume(CREATE);
        tokens.consume(TABLE);
        final boolean ifNotExists = tokens.canConsume("IF", "NOT", "EXISTS");
        
        String tableName = parseName(tokens);
        AstNode tableNode = nodeFactory().node(tableName, parentNode, TYPE_CREATE_TABLE_STATEMENT);
        tableNode.setProperty(SqliteDdlLexicon.IF_NOT_EXISTS_CLAUSE, ifNotExists);
        
        if(tokens.canConsume("AS")) {
            // CREATE TABLE ... AS select-stmt - we don't support this
            parseUntilTerminator(tokens); // ignore the rest
            markEndOfStatement(tokens, tableNode);
            return tableNode;
        }
        
        parseColumnsAndConstraints(tokens, tableNode);
        
        final boolean isWithoutRowid = tokens.canConsume("WITHOUT", "ROWID");
        tableNode.setProperty(SqliteDdlLexicon.WITHOUT_ROWID_CLAUSE, isWithoutRowid);
        
        parseUntilTerminator(tokens); // ignore the rest

        markEndOfStatement(tokens, tableNode);

        return tableNode;
    }
    
    
    @Override
    protected void parseColumnsAndConstraints(DdlTokenStream tokens, AstNode tableNode) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        // (column-def, column-def, ..., table-constraint, ...)
        if (!tokens.matches(L_PAREN)) {
            return;
        }

        String tableElementString = getTableElementsString(tokens, false);
        DdlTokenStream localTokens = new DdlTokenStream(tableElementString, ddlTokenizer(false), false);
        
        localTokens.start();

        do {
            if (isTableConstraint(localTokens)) {
                parseTableConstraint(localTokens, tableNode, false);
            } else {
                parseColumnDefinition(localTokens, tableNode, false);
            }
        } while (localTokens.canConsume(COMMA));
    }
    
    
    @SuppressWarnings("static-access")
    protected boolean isColumnConstraint(DdlTokenStream tokens) {
        String[][] columnConstraintStartPhrases = {
                {"PRIMARY", "KEY"},
                {"NOT", "NULL"},
                {"UNIQUE"},
                {"CHECK"},
                {"DEFAULT"},
                {"COLLATE"},
                {"REFERENCES"},
                {"CONSTRAINT", DdlTokenStream.ANY_VALUE, "PRIMARY", "KEY"},
                {"CONSTRAINT", DdlTokenStream.ANY_VALUE, "NOT", "NULL"},
                {"CONSTRAINT", DdlTokenStream.ANY_VALUE, "UNIQUE"},
                {"CONSTRAINT", DdlTokenStream.ANY_VALUE, "CHECK"},
                {"CONSTRAINT", DdlTokenStream.ANY_VALUE, "DEFAULT"},
                {"CONSTRAINT", DdlTokenStream.ANY_VALUE, "COLLATE"},
                {"CONSTRAINT", DdlTokenStream.ANY_VALUE, "REFERENCES"}
        };
        
        for (String[] startPhrase : columnConstraintStartPhrases) {
            if(tokens.matches(startPhrase)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected void parseConflictClause(DdlTokenStream tokens, AstNode node) {
        if(tokens.canConsume("ON", "CONFLICT")) {
            String conflictAction = tokens.consume();
            
            if(node != null) {
                node.setProperty(SqliteDdlLexicon.ON_CONFLICT_CLAUSE, conflictAction);
            }
        }
    }
    
    
    @Override
    protected void parseTableConstraint( DdlTokenStream tokens,
                                         AstNode tableNode,
                                         boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        String mixinType = isAlterTable ? TYPE_ADD_TABLE_CONSTRAINT_DEFINITION : TYPE_TABLE_CONSTRAINT;
        
        consumeComment(tokens);

        if (tokens.matches("UNIQUE") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "UNIQUE")) {
            String uc_name = "UC_1";
            if(tokens.canConsume("CONSTRAINT")) {
                uc_name = parseName(tokens); // UNIQUE CONSTRAINT NAME
            }
            
            tokens.consume("UNIQUE"); // UNIQUE
            
            AstNode constraintNode = nodeFactory().node(uc_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, UNIQUE);
            
            // CONSUME COLUMNS
            final String columnExpressionList = parseContentBetweenParens(tokens);
            parseIndexColumnExpressionList("(" + columnExpressionList + ")", constraintNode, tableNode);
            
            parseConflictClause(tokens, constraintNode);
            parseConstraintAttributes(tokens, constraintNode);
            
            consumeComment(tokens);
            
        } else if (tokens.matches("PRIMARY", "KEY") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "PRIMARY", "KEY")) {
            String pk_name = "PK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                pk_name = parseName(tokens);
            }
            tokens.consume("PRIMARY", "KEY");
            
            AstNode constraintNode = nodeFactory().node(pk_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, PRIMARY_KEY);
            
            // CONSUME COLUMNS
            final String columnExpressionList = parseContentBetweenParens(tokens);
            parseIndexColumnExpressionList("(" + columnExpressionList + ")", constraintNode, tableNode);
            
            parseConflictClause(tokens, constraintNode);
            parseConstraintAttributes(tokens, constraintNode);
            
            consumeComment(tokens);

        } else if (tokens.matches("FOREIGN", "KEY")
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "FOREIGN", "KEY")) {
            String fk_name = "FK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                fk_name = parseName(tokens);
            }
            tokens.consume("FOREIGN", "KEY");

            AstNode constraintNode = nodeFactory().node(fk_name, tableNode, mixinType);

            constraintNode.setProperty(CONSTRAINT_TYPE, FOREIGN_KEY);

            // CONSUME COLUMNS
            parseColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

            // Parse the references to table and columns
            parseReferences(tokens, constraintNode);
            
            parseConstraintAttributes(tokens, constraintNode);
            
            consumeComment(tokens);
            
        } else if (tokens.matches("CHECK") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "CHECK")) {
            String ck_name = "CHECK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                ck_name = parseName(tokens);
            }
            tokens.consume("CHECK");
            
            AstNode constraintNode = nodeFactory().node(ck_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, CHECK);
            
            String clause = consumeParenBoundedTokens(tokens, true);
            constraintNode.setProperty(CHECK_SEARCH_CONDITION, clause);
        }
    }
    
    
    @Override
    protected void parseColumnDefinition( DdlTokenStream tokens,
            AstNode tableNode,
            boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;
        
        String columnName = parseName(tokens);
        AstNode columnNode = nodeFactory().node(columnName, tableNode, TYPE_COLUMN_DEFINITION);
        
        DataType datatype = getDatatypeParser().parse(tokens);
        getDatatypeParser().setPropertiesOnNode(columnNode, datatype);
        
        
        // Now clauses and constraints can be defined in any order, so we need to keep parsing until we get to a comma
        StringBuffer unusedTokensSB = new StringBuffer();
        
        while (tokens.hasNext() && !tokens.matches(COMMA)) {
            boolean parsedDefaultClause = parseDefaultClause(tokens, columnNode);
            boolean parsedCollate = parseCollateClause(tokens, columnNode);
            boolean parsedConstraint = parseColumnConstraint(tokens, columnNode, isAlterTable);

            if (!parsedDefaultClause 
                    && !parsedCollate
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
    
    
    @Override
    protected boolean parseColumnConstraint( DdlTokenStream tokens,
                                             AstNode columnNode,
                                             boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert columnNode != null;

        String mixinType = isAlterTable ? TYPE_ADD_TABLE_CONSTRAINT_DEFINITION : TYPE_TABLE_CONSTRAINT;

        boolean result = false;
        
        String colName = columnNode.getName();

        if (tokens.canConsume("NULL")
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "NULL")) {
            if(tokens.canConsume("CONSTRAINT")) {
                parseName(tokens); // CONSTRAINT NAME
            }
            
            columnNode.setProperty(NULLABLE, "NULL");
            parseConflictClause(tokens, null);
            result = true;
            
        } else if (tokens.canConsume("NOT", "NULL")
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "NOT", "NULL")) {
            result = true;
            if(tokens.canConsume("CONSTRAINT")) {
                parseName(tokens); // CONSTRAINT NAME
            }
            
            columnNode.setProperty(NULLABLE, "NOT NULL");
            parseConflictClause(tokens, null);
            
        } else if (tokens.matches("UNIQUE") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "UNIQUE")) {
            result = true;
            String uc_name = "UC_1";
            if(tokens.canConsume("CONSTRAINT")) {
                uc_name = parseName(tokens); // UNIQUE CONSTRAINT NAME
            }
            
            tokens.consume("UNIQUE"); // UNIQUE
            
            AstNode constraintNode = nodeFactory().node(uc_name, columnNode.getParent(), mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, UNIQUE);
            
            // CONSUME COLUMNS
            nodeFactory().node(colName, constraintNode, TYPE_COLUMN_REFERENCE);
            
            parseConflictClause(tokens, constraintNode);
            parseConstraintAttributes(tokens, constraintNode);
            
            consumeComment(tokens);
            
        } else if (tokens.matches("PRIMARY", "KEY") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "PRIMARY", "KEY")) {
            result = true;
            String pk_name = "PK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                pk_name = parseName(tokens);
            }
            tokens.consume("PRIMARY", "KEY");
            tokens.canConsume("ASC");
            tokens.canConsume("DESC");

            AstNode constraintNode = nodeFactory().node(pk_name, columnNode.getParent(), mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, PRIMARY_KEY);
            
            // CONSUME COLUMNS
            nodeFactory().node(colName, constraintNode, TYPE_COLUMN_REFERENCE);
            
            parseConflictClause(tokens, constraintNode);
            if(tokens.canConsume("AUTOINCREMENT")) {
                columnNode.setProperty(SqliteDdlLexicon.AUTOINCREMENT, true);
            }
            parseConstraintAttributes(tokens, constraintNode);
            
            consumeComment(tokens);

        } else if (tokens.matches("REFERENCES")
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "REFERENCES")) {
            result = true;
            String fk_name = "FK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                fk_name = parseName(tokens);
            }
            
            AstNode constraintNode = nodeFactory().node(fk_name, columnNode.getParent(), mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, FOREIGN_KEY);
            
            // CONSUME COLUMNS
            nodeFactory().node(colName, constraintNode, TYPE_COLUMN_REFERENCE);
            
            // Parse the references to table and columns
            parseReferences(tokens, constraintNode);
            
            parseConstraintAttributes(tokens, constraintNode);
            
            consumeComment(tokens);
            
        } else if (tokens.matches("CHECK") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "CHECK")) {
            result = true;
            String ck_name = "CHECK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                ck_name = parseName(tokens);
            }
            tokens.consume("CHECK");
            
            AstNode constraintNode = nodeFactory().node(ck_name, columnNode.getParent(), mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, CHECK);
            
            String clause = consumeParenBoundedTokens(tokens, true);
            constraintNode.setProperty(CHECK_SEARCH_CONDITION, clause);
            
            parseConstraintAttributes(tokens, constraintNode);
        }

        return result;
    }
    
    @Override
    protected boolean parseDefaultClause(DdlTokenStream tokens, AstNode columnNode) throws ParsingException {
        if(tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "DEFAULT")) {
            // [ CONSTRAINT constraint_name ] DEFAULT default_expression
            tokens.consume("CONSTRAINT");
            parseName(tokens);
        }
        return super.parseDefaultClause(tokens, columnNode);
    }
    
    @Override
    protected boolean parseCollateClause(DdlTokenStream tokens, AstNode columnNode) throws ParsingException {
        if(tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "COLLATE")) {
            // [ CONSTRAINT constraint_name ] COLLATE collation-name
            tokens.consume("CONSTRAINT");
            parseName(tokens);
        }
        return super.parseCollateClause(tokens, columnNode);
    }
    
    @Override
    protected AstNode parseAlterTableStatement( DdlTokenStream tokens,
                                                AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches("ALTER", "TABLE", TokenStream.ANY_VALUE, "ADD")) {
            
            markStartOfStatement(tokens);
            
            tokens.consume(ALTER, TABLE);
            
            String tableName = parseName(tokens);
            
            AstNode alterTableNode = nodeFactory().node(tableName, parentNode, TYPE_ALTER_TABLE_STATEMENT);
            
            tokens.consume("ADD"); 
            tokens.canConsume("COLUMN");
            
            parseSingleTerminatedColumnDefinition(tokens, alterTableNode, true);
            
            parseUntilTerminator(tokens);
            markEndOfStatement(tokens, alterTableNode);
            
            return alterTableNode;
            
        } else if (tokens.matches("ALTER", "TABLE", TokenStream.ANY_VALUE, "RENAME")) {
            
            // ALTER TABLE customers RENAME TO my_customers;
            markStartOfStatement(tokens);
            
            tokens.consume(ALTER, TABLE);
            
            String oldName = parseName(tokens);
            AstNode alterTableNode = nodeFactory().node(oldName, parentNode, TYPE_ALTER_TABLE_STATEMENT);
            
            tokens.consume("RENAME", "TO");
            String newName = parseName(tokens);
            alterTableNode.setProperty(NEW_NAME, newName);
            
            parseUntilTerminator(tokens);
            
            markEndOfStatement(tokens, alterTableNode);
            
            return alterTableNode;
        }
        
        return null;
    }

    @Override
    protected AstNode parseAlterStatement( DdlTokenStream tokens,
                                           AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(ALTER, TABLE)) {
            return parseAlterTableStatement(tokens, parentNode);
        }

        return null;
    }
    
    @Override
    protected AstNode parseCreateViewStatement( DdlTokenStream tokens,
                                                AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);
        
        tokens.consume("CREATE");
        tokens.consume("VIEW");
        boolean ifNotExists = tokens.canConsume("IF", "NOT", "EXISTS");
        
        
        String name = parseName(tokens);
        AstNode createViewNode = nodeFactory().node(name, parentNode, TYPE_CREATE_VIEW_STATEMENT);
        if(ifNotExists) {
            createViewNode.setProperty(SqliteDdlLexicon.IF_NOT_EXISTS_CLAUSE, true);
        }
        
        tokens.consume("AS");
        
        String queryExpression = parseUntilTerminator(tokens);
        
        createViewNode.setProperty(CREATE_VIEW_QUERY_EXPRESSION, queryExpression);
        
        markEndOfStatement(tokens, createViewNode);
        
        return createViewNode;
    }
    
    
    private void parseIndexColumnExpressionList( final String columnExpressionList,
                                                 final AstNode node,
                                                 final AstNode tableNode) {
        final DdlTokenStream tokens = new DdlTokenStream(columnExpressionList, DdlTokenStream.ddlTokenizer(false), false);
        tokens.start();
        
        tokens.consume(L_PAREN); // must have opening paren
        
        if(tableNode != null) {
            // we found a table this index relates to - we can do the rest of the stuff
            List<AstNode> columnNodes = tableNode.getChildren(StandardDdlLexicon.TYPE_COLUMN_DEFINITION);
            
            String colName = null;
            String collation = null;
            boolean isAsc = false;
            boolean isDesc = false;
            while(!tokens.matches(R_PAREN)) {
                colName = parseName(tokens);
                
                if(tokens.matches("COLLATE")) {
                    collation = parseName(tokens);
                }

                isAsc = tokens.canConsume("ASC");
                isDesc = tokens.canConsume("DESC");
                
                // find this column
                for (final AstNode colNode : columnNodes) {
                    if (colNode.getName().toUpperCase().equals(colName.toUpperCase())) {
                        // found - add it
                        final AstNode colRef = nodeFactory().node(colName, node, TYPE_COLUMN_REFERENCE);
                        
                        if (isAsc || isDesc) {
                            colRef.addMixin(SqliteDdlLexicon.TYPE_INDEX_ORDERABLE);
                            if (isAsc) {
                                colRef.setProperty(SqliteDdlLexicon.INDEX_ORDER, "ASC");
                            } else {
                                colRef.setProperty(SqliteDdlLexicon.INDEX_ORDER, "DESC");
                            }
                        }
                    }
                }
                
                // reset temp values
                colName = null;
                collation = null;
                isAsc = false;
                isDesc = false;
                
                tokens.canConsume(COMMA);
            }
            
        } else {
            // unable to find table - ignore columns
            while(!tokens.matches(R_PAREN)) {
                tokens.consume();
            }
        }
        
        
        tokens.consume(R_PAREN); // must have closing paren
    }
    
    /**
     * Parses DDL CREATE INDEX
     * 
     * @param tokens the tokenized {@link DdlTokenStream} of the DDL input content; may not be null
     * @param parentNode the parent {@link AstNode} node; may not be null
     * @return the parsed CREATE INDEX
     * @throws ParsingException
     */
    private AstNode parseCreateIndex( final DdlTokenStream tokens,
                                      final AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;
        assert (tokens.matches(STMT_CREATE_INDEX) || tokens.matches(STMT_CREATE_UNIQUE_INDEX));

        markStartOfStatement(tokens);
        tokens.consume(CREATE);

        final boolean isUnique = tokens.canConsume(UNIQUE);

        tokens.consume(INDEX);
        final boolean ifNotExists = tokens.canConsume("IF", "NOT", "EXISTS");
        final String indexName = parseName(tokens);
        
        tokens.consume(ON);

        AstNode indexNode = null;

        final String tableName = parseName(tokens);
        indexNode = nodeFactory().node(indexName, parentNode, TYPE_CREATE_INDEX_STATEMENT);
        indexNode.setProperty(SqliteDdlLexicon.TABLE_NAME, tableName);
        indexNode.setProperty(UNIQUE_INDEX, isUnique);
        indexNode.setProperty(SqliteDdlLexicon.IF_NOT_EXISTS_CLAUSE, ifNotExists);
        
        
        // find a table this index relates to
        final AstNode parent = indexNode.getParent();
        final List<AstNode> nodes = parent.childrenWithName(tableName);
        AstNode tableNode = null;

        if (!nodes.isEmpty()) {
            if (nodes.size() == 1) {
                tableNode = nodes.get(0);
            } else {
                for (final AstNode node : nodes) {
                    if (node.hasMixin(StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT)) {
                        tableNode = node;
                        break;
                    }
                }
            }
        }
        
        // parse left-paren content right-paren
        final String columnExpressionList = parseContentBetweenParens(tokens);
        if(tableNode != null) {
            parseIndexColumnExpressionList("(" + columnExpressionList + ")", indexNode, tableNode);
        }


        boolean lastMatched = true;
        while (tokens.hasNext() && !isTerminator(tokens) && lastMatched) {

            if(tokens.canConsume("WHERE")) {
                // it can be any expression... parse until next keyword or terminator
                StringBuilder sb = new StringBuilder();
                while (tokens.hasNext() && !isTerminator(tokens)){
                    sb.append(SPACE).append(tokens.consume());
                }
                indexNode.setProperty(WHERE_CLAUSE, sb.toString());
                
            } else {
                lastMatched = false;
            }
        }
        
        parseUntilTerminator(tokens);
        
        markEndOfStatement(tokens, indexNode);
        return indexNode;
    }
    
    
    @Override
    protected AstNode parseDropStatement( DdlTokenStream tokens,
                                          AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        AstNode dropNode = null;

        if (tokens.matches(StatementStartPhrases.STMT_DROP_TABLE)) {
            markStartOfStatement(tokens);

            // DROP TABLE [ database_name . [ schema_name ] . | schema_name . ]
            // table_name [ ,...n ] [ ; ]

            tokens.consume(DROP, TABLE);
            String name = parseName(tokens);
            dropNode = nodeFactory().node(name, parentNode, TYPE_DROP_TABLE_STATEMENT);
            markEndOfStatement(tokens, dropNode);

            return dropNode;
        } else if (tokens.matches(STMT_DROP_INDEX)) {
            return parseStatement(tokens, STMT_DROP_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_TRIGGER)) {
            return parseStatement(tokens, STMT_DROP_TRIGGER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(SqliteDdlConstants.SqliteStatementStartPhrases.STMT_DROP_VIEW)) {
            return parseStatement(tokens, SqliteDdlConstants.SqliteStatementStartPhrases.STMT_DROP_VIEW, parentNode, TYPE_STATEMENT);
        }

        return super.parseDropStatement(tokens, parentNode);
    }
    

    // ===========================================================================================================================
    // PARSE OBJECTS
    // ===========================================================================================================================

    /**
     * This class provides custom data type parsing for Sqlite-specific data types.
     */
    class SqliteDataTypeParser extends DataTypeParser {

        @SuppressWarnings("synthetic-access")
        @Override
        public DataType parse(DdlTokenStream tokens) throws ParsingException {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            
            while(tokens.hasNext()
                    && !tokens.matches(COMMA)
                    && !tokens.matches(R_PAREN)
                    && !isColumnConstraint(tokens)) {
                
                if(tokens.matches(L_PAREN)) {
                    String contentBetweenParens = parseContentBetweenParens(tokens);
                    contentBetweenParens = contentBetweenParens.replace(" , ",  ",");
                    sb.append("(" + contentBetweenParens + ")");
                    
                } else {
                    if(!first) {
                        sb.append(SPACE);
                    }
                    sb.append(tokens.consume());
                }
                first = false;
            }
            
            String typeName = (sb.length() == 0) ? "" : sb.toString();
            return new DataType(typeName);
        }
    }
}
