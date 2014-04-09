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
package org.modeshape.sequencer.ddl.dialect.sqlserver;

import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CHECK_SEARCH_CONDITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CONSTRAINT_TYPE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DATATYPE_NAME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NULLABLE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ADD_TABLE_CONSTRAINT_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_TABLE_CONSTRAINT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.VALUE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DROP_BEHAVIOR;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NEW_NAME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_REFERENCE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_TABLE_CONSTRAINT_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_STATEMENT_OPTION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_TABLE_REFERENCE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_UNKNOWN_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.sqlserver.SqlServerDdlLexicon.*;
import java.util.ArrayList;
import java.util.List;
import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.common.text.TokenStream;
import org.modeshape.sequencer.ddl.DdlParserProblem;
import org.modeshape.sequencer.ddl.DdlSequencerI18n;
import org.modeshape.sequencer.ddl.DdlTokenStream;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.DdlTokenStream.DdlTokenizer;
import org.modeshape.sequencer.ddl.StandardDdlParser;
import org.modeshape.sequencer.ddl.datatype.DataType;
import org.modeshape.sequencer.ddl.datatype.DataTypeParser;
import org.modeshape.sequencer.ddl.node.AstNode;

/**
 * SqlServer-specific DDL Parser. Includes custom data types as well as custom DDL statements.
 */
public class SqlServerDdlParser extends StandardDdlParser
    implements SqlServerDdlConstants, SqlServerDdlConstants.SqlServerStatementStartPhrases {

    /**
     * The SqlServer parser identifier.
     */
    @SuppressWarnings("hiding")
    public static final String ID = "SQLSERVER";

    static List<String[]> sqlServerDataTypeStrings = new ArrayList<String[]>();

    public SqlServerDdlParser() {
        super();

        setDatatypeParser(new SqlServerDataTypeParser());
        initialize();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#areNextTokensCreateTableOptions(org.modeshape.sequencer.ddl.DdlTokenStream)
     */
    @Override
    protected boolean areNextTokensCreateTableOptions( final DdlTokenStream tokens ) throws ParsingException {
        // current token can't be a terminator or the next n-tokens can't be a statement  
        return (tokens.hasNext() && !isTerminator(tokens) && (tokens.computeNextStatementStartKeywordCount() == 0));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#parseNextCreateTableOption(org.modeshape.sequencer.ddl.DdlTokenStream, org.modeshape.sequencer.ddl.node.AstNode)
     */
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

        sqlServerDataTypeStrings.addAll(SqlServerDataTypes.CUSTOM_DATATYPE_START_PHRASES); //
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#getId()
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#getIdentifyingKeywords()
     */
    @Override
    public String[] getIdentifyingKeywords() {
        return new String[] {getId(), "spool.log"};
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#initializeTokenStream(org.modeshape.sequencer.ddl.DdlTokenStream)
     */
    @Override
    protected void initializeTokenStream( DdlTokenStream tokens ) {
        super.initializeTokenStream(tokens);
        tokens.registerKeyWords(CUSTOM_KEYWORDS);
        tokens.registerKeyWords(SqlServerDataTypes.CUSTOM_DATATYPE_START_WORDS);
        tokens.registerStatementStartPhrase(ALTER_PHRASES);
        tokens.registerStatementStartPhrase(CREATE_PHRASES);
        tokens.registerStatementStartPhrase(DROP_PHRASES);
        tokens.registerStatementStartPhrase(MISC_PHRASES);
        tokens.registerStatementStartPhrase(SET_PHRASES);
    }

    @Override
    protected void rewrite( DdlTokenStream tokens,
                            AstNode rootNode ) {
        assert tokens != null;
        assert rootNode != null;

        // We may have a prepare statement that is followed by a missing terminator node
        // We also may have nodes that have an extra terminator node representing the '/' backslash
        // These nodes will have type "TYPE_BACKSLASH_TERMINATOR".

        List<AstNode> copyOfNodes = new ArrayList<AstNode>(rootNode.getChildren());

        AstNode complexNode = null;

        for (AstNode child : copyOfNodes) {

            if ((complexNode != null && nodeFactory().hasMixinType(child, TYPE_UNKNOWN_STATEMENT))
                || (complexNode != null && nodeFactory().hasMixinType(child, TYPE_BACKSLASH_TERMINATOR))) {
                mergeNodes(tokens, complexNode, child);
                rootNode.removeChild(child);
            } else {
                complexNode = null;
            }
        }

        // We also may have nodes that have an extra terminator node representing the '/' backslash
        // These nodes will have type "TYPE_BACKSLASH_TERMINATOR".

        super.rewrite(tokens, rootNode); // Removes all extra "missing terminator" nodes

        // Now we need to walk the tree again looking for unknown nodes under the root
        // and attach them to the previous node, assuming the node can contain multiple nested statements.
        // CREATE FUNCTION is one of those types

        copyOfNodes = new ArrayList<AstNode>(rootNode.getChildren());
        boolean foundComplexNode = false;
        complexNode = null;
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

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#consumeIdentifier(org.modeshape.sequencer.ddl.DdlTokenStream)
     */
    @Override
    protected String consumeIdentifier( DdlTokenStream tokens ) throws ParsingException {
        Position startPosition = tokens.nextPosition();
        String id = super.consumeIdentifier(tokens);
        Position nextPosition = tokens.nextPosition();

        while (((nextPosition.getIndexInContent() - startPosition.getIndexInContent() - id.length()) == 0)) {
            // allowed symbols in an identifier: underscore, dollar sign, pound sign, at sign
            if (tokens.matches(DdlTokenizer.SYMBOL)) {
                if (tokens.matches('$') || tokens.matches('#') || tokens.matches('@')) {
                    id += tokens.consume(); // consume symbol
                } else {
                    break; // not a valid ID symbol
                }
            } else {
                id += tokens.consume(); // consume text
            }

            nextPosition = tokens.nextPosition();
        }

        return id;
    }
    
    private boolean matchesComplexNode( AstNode node ) {
        assert node != null;

        for (String mixin : COMPLEX_STMT_TYPES) {
            if (nodeFactory().hasMixinType(node, mixin)) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#handleUnknownToken(org.modeshape.sequencer.ddl.DdlTokenStream,
     *      java.lang.String)
     */
    @Override
    public AstNode handleUnknownToken( DdlTokenStream tokens,
                                       String tokenValue ) throws ParsingException {
        if (tokenValue.equals("/")) {
            return nodeFactory().node("backslashTerminator", getRootNode(), TYPE_BACKSLASH_TERMINATOR);
        }

        return null;
    }

    /**
     * {@inheritDoc} The CREATE SCHEMA statement can include CREATE TABLE, CREATE VIEW, and GRANT statements.
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#parseCreateSchemaStatement(org.modeshape.sequencer.ddl.DdlTokenStream,
     *      org.modeshape.sequencer.ddl.node.AstNode)
     */
    @Override
    protected AstNode parseCreateSchemaStatement( DdlTokenStream tokens,
                                                  AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        return super.parseCreateSchemaStatement(tokens, parentNode);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#parseCustomStatement(org.modeshape.sequencer.ddl.DdlTokenStream,
     *      org.modeshape.sequencer.ddl.node.AstNode)
     */
    @Override
    protected AstNode parseCustomStatement( DdlTokenStream tokens,
                                            AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_BEGIN_DISTRIBUTED_TRANSACTION)) {
            return parseStatement(tokens, STMT_BEGIN_DISTRIBUTED_TRANSACTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_BEGIN_DISTRIBUTED_TRAN)) {
            return parseStatement(tokens, STMT_BEGIN_DISTRIBUTED_TRAN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ROLLBACK_TRANSACTION)) {
            return parseStatement(tokens, STMT_ROLLBACK_TRANSACTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ROLLBACK_TRAN)) {
            return parseStatement(tokens, STMT_ROLLBACK_TRAN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_BEGIN_TRANSACTION)) {
            return parseStatement(tokens, STMT_BEGIN_TRANSACTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_BEGIN_TRAN)) {
            return parseStatement(tokens, STMT_BEGIN_TRAN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ROLLBACK_WORK)) {
            return parseStatement(tokens, STMT_ROLLBACK_WORK, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ROLLBACK)) {
            return parseStatement(tokens, STMT_ROLLBACK, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_COMMIT_TRANSACTION)) {
            return parseStatement(tokens, STMT_COMMIT_TRANSACTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_COMMIT_TRAN)) {
            return parseStatement(tokens, STMT_COMMIT_TRAN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SAVE_TRANSACTION)) {
            return parseStatement(tokens, STMT_SAVE_TRANSACTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SAVE_TRAN)) {
            return parseStatement(tokens, STMT_SAVE_TRAN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_COMMIT_WORK)) {
            return parseStatement(tokens, STMT_COMMIT_WORK, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_COMMIT)) {
            return parseStatement(tokens, STMT_COMMIT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DISABLE_TRIGGER)) {
            return parseStatement(tokens, STMT_DISABLE_TRIGGER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ENABLE_TRIGGER)) {
            return parseStatement(tokens, STMT_ENABLE_TRIGGER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_TRUNCATE_TABLE)) {
            return parseStatement(tokens, STMT_TRUNCATE_TABLE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_UPDATE_STATISTICS)) {
            return parseStatement(tokens, STMT_UPDATE_STATISTICS, parentNode, TYPE_STATEMENT);
        } else {
            return super.parseCustomStatement(tokens, parentNode);
        }
    }

    @Override
    protected AstNode parseCreateStatement( DdlTokenStream tokens,
                                            AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_CREATE_INDEX) || tokens.matches(STMT_CREATE_UNIQUE_INDEX)
            || tokens.matches(STMT_CREATE_CLUSTERED_INDEX) || tokens.matches(STMT_CREATE_NONCLUSTERED_INDEX)
            || tokens.matches(STMT_CREATE_UNIQUE_CLUSTERED_INDEX) || tokens.matches(STMT_CREATE_UNIQUE_NONCLUSTERED_INDEX)) {
            return parseCreateIndex(tokens, parentNode);
            
        } else if (tokens.matches(STMT_CREATE_SEQUENCE)) {
            return parseCreateSequenceStatement(tokens, parentNode);
            
        // ignorable
        } else if (tokens.matches(STMT_CREATE_AGGREGATE)) {
            return parseStatement(tokens, STMT_CREATE_AGGREGATE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_APPLICATION_ROLE)) {
            return parseStatement(tokens, STMT_CREATE_APPLICATION_ROLE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_ASSEMBLY)) {
            return parseStatement(tokens, STMT_CREATE_ASSEMBLY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_ASYMMETRIC_KEY)) {
            return parseStatement(tokens, STMT_CREATE_ASYMMETRIC_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_BROKER_PRIORITY)) {
            return parseStatement(tokens, STMT_CREATE_BROKER_PRIORITY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_CERTIFICATE)) {
            return parseStatement(tokens, STMT_CREATE_CERTIFICATE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_NONCLUSTERED_COLUMNSTORE_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_NONCLUSTERED_COLUMNSTORE_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_COLUMNSTORE_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_COLUMNSTORE_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_CONTRACT)) {
            return parseStatement(tokens, STMT_CREATE_CONTRACT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_CREDENTIAL)) {
            return parseStatement(tokens, STMT_CREATE_CREDENTIAL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_CRYPTOGRAPHIC_PROVIDER)) {
            return parseStatement(tokens, STMT_CREATE_CRYPTOGRAPHIC_PROVIDER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_DATABASE_AUDIT_SPECIFICATION)) {
            return parseStatement(tokens, STMT_CREATE_DATABASE_AUDIT_SPECIFICATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_DATABASE_ENCRYPTION_KEY)) {
            return parseStatement(tokens, STMT_CREATE_DATABASE_ENCRYPTION_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_DATABASE)) {
            return parseStatement(tokens, STMT_CREATE_DATABASE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_DEFAULT)) {
            return parseStatement(tokens, STMT_CREATE_DEFAULT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_ENDPOINT)) {
            return parseStatement(tokens, STMT_CREATE_ENDPOINT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_EVENT_NOTIFICATION)) {
            return parseStatement(tokens, STMT_CREATE_EVENT_NOTIFICATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_EVENT_SESSION)) {
            return parseStatement(tokens, STMT_CREATE_EVENT_SESSION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_FULLTEXT_CATALOG)) {
            return parseStatement(tokens, STMT_CREATE_FULLTEXT_CATALOG, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_FULLTEXT_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_FULLTEXT_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_FULLTEXT_STOPLIST)) {
            return parseStatement(tokens, STMT_CREATE_FULLTEXT_STOPLIST, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_FUNCTION)) {
            return parseStatement(tokens, STMT_CREATE_FUNCTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_UNIQUE_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_UNIQUE_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_CLUSTERED_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_CLUSTERED_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_NONCLUSTERED_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_NONCLUSTERED_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_UNIQUE_CLUSTERED_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_UNIQUE_CLUSTERED_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_UNIQUE_NONCLUSTERED_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_UNIQUE_NONCLUSTERED_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_LOGIN)) {
            return parseStatement(tokens, STMT_CREATE_LOGIN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_MASTER_KEY)) {
            return parseStatement(tokens, STMT_CREATE_MASTER_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_MESSAGE_TYPE)) {
            return parseStatement(tokens, STMT_CREATE_MESSAGE_TYPE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_PARTITION_FUNCTION)) {
            return parseStatement(tokens, STMT_CREATE_PARTITION_FUNCTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_PARTITION_SCHEME)) {
            return parseStatement(tokens, STMT_CREATE_PARTITION_SCHEME, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_PROCEDURE)) {
            return parseStatement(tokens, STMT_CREATE_PROCEDURE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_PROC)) {
            return parseStatement(tokens, STMT_CREATE_PROC, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_QUEUE)) {
            return parseStatement(tokens, STMT_CREATE_QUEUE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_REMOTE_SERVICE_BINDING)) {
            return parseStatement(tokens, STMT_CREATE_REMOTE_SERVICE_BINDING, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_RESOURCE_POOL)) {
            return parseStatement(tokens, STMT_CREATE_RESOURCE_POOL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_ROLE)) {
            return parseStatement(tokens, STMT_CREATE_ROLE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_ROUTE)) {
            return parseStatement(tokens, STMT_CREATE_ROUTE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_RULE)) {
            return parseStatement(tokens, STMT_CREATE_RULE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(SqlServerStatementStartPhrases.STMT_CREATE_SCHEMA)) {
            return parseStatement(tokens, SqlServerStatementStartPhrases.STMT_CREATE_SCHEMA, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SEARCH_PROPERTY_LIST)) {
            return parseStatement(tokens, STMT_CREATE_SEARCH_PROPERTY_LIST, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SEQUENCE)) {
            return parseStatement(tokens, STMT_CREATE_SEQUENCE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SERVER_AUDIT_SPECIFICATION)) {
            return parseStatement(tokens, STMT_CREATE_SERVER_AUDIT_SPECIFICATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SERVER_AUDIT)) {
            return parseStatement(tokens, STMT_CREATE_SERVER_AUDIT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SERVICE)) {
            return parseStatement(tokens, STMT_CREATE_SERVICE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SPATIAL_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_SPATIAL_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_STATISTICS)) {
            return parseStatement(tokens, STMT_CREATE_STATISTICS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SYMMETRIC_KEY)) {
            return parseStatement(tokens, STMT_CREATE_SYMMETRIC_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_SYNONYM)) {
            return parseStatement(tokens, STMT_CREATE_SYNONYM, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_TRIGGER)) {
            return parseStatement(tokens, STMT_CREATE_TRIGGER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_TYPE)) {
            return parseStatement(tokens, STMT_CREATE_TYPE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_USER)) {
            return parseStatement(tokens, STMT_CREATE_USER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_WORKLOAD_GROUP)) {
            return parseStatement(tokens, STMT_CREATE_WORKLOAD_GROUP, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_XML_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_XML_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_PRIMARY_XML_INDEX)) {
            return parseStatement(tokens, STMT_CREATE_PRIMARY_XML_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_CREATE_XML_SCHEMA_COLLECTION)) {
            return parseStatement(tokens, STMT_CREATE_XML_SCHEMA_COLLECTION, parentNode, TYPE_STATEMENT);
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
        String tableName = parseName(tokens);
        AstNode tableNode = nodeFactory().node(tableName, parentNode, TYPE_CREATE_TABLE_STATEMENT);
        
        // AS FileTable
        if(tokens.canConsume("AS", "FILETABLE")){
            tableNode.setProperty(AS_FILETABLE, true);
        }
        
        parseColumnsAndConstraints(tokens, tableNode);
        

        boolean lastMatched = true;
        while (tokens.hasNext() && !isTerminator(tokens) && lastMatched) {

            if(tokens.canConsume("WITH")) {
                String withOptionsList = parseContentBetweenParens(tokens);
                tableNode.setProperty(WITH_OPTIONS, withOptionsList);
                
            } else if(tokens.canConsume("ON")) {
                String name = parseName(tokens);
                if(tokens.matches(L_PAREN)) {
                    String columnName = parseContentBetweenParens(tokens);
                    name = name + " (" + columnName + ")";
                }
                tableNode.setProperty(ON_CLAUSE, name);
                
            } else if(tokens.canConsume("FILESTREAM_ON")) {
                String name = parseName(tokens);
                tableNode.setProperty(FILESTREAM_ON_CLAUSE, name);
                
            } else if(tokens.canConsume("TEXTIMAGE_ON")) {
                String name = parseName(tokens);
                tableNode.setProperty(TEXTIMAGE_ON_CLAUSE, name);
                
            } else {
                lastMatched = false;
            }
        }
        
        parseUntilTerminator(tokens); // ignore the rest

        markEndOfStatement(tokens, tableNode);

        return tableNode;
    }
    
    @Override
    protected void parseTableConstraint( DdlTokenStream tokens,
                                         AstNode tableNode,
                                         boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        String mixinType = isAlterTable ? TYPE_ADD_TABLE_CONSTRAINT_DEFINITION : TYPE_TABLE_CONSTRAINT;

        /*
        < table_constraint > ::=
            [ CONSTRAINT constraint_name ] 
            { 
            { PRIMARY KEY | UNIQUE } 
                [ CLUSTERED | NONCLUSTERED ] 
                (column [ ASC | DESC ] [ ,...n ] ) 
                [ 
                    WITH FILLFACTOR = fillfactor 
                   |WITH ( <index_option> [ , ...n ] ) 
                ]
                [ ON { partition_scheme_name (partition_column_name)
                    | filegroup | "default" } ] 
            | FOREIGN KEY 
                ( column [ ,...n ] ) 
                REFERENCES referenced_table_name [ ( ref_column [ ,...n ] ) ] 
                [ ON DELETE { NO ACTION | CASCADE | SET NULL | SET DEFAULT } ] 
                [ ON UPDATE { NO ACTION | CASCADE | SET NULL | SET DEFAULT } ] 
                [ NOT FOR REPLICATION ] 
            | CHECK [ NOT FOR REPLICATION ] ( logical_expression ) 
            } 
         */
        consumeComment(tokens);

        if (tokens.matches("UNIQUE") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "UNIQUE")) {
            String uc_name = "UC_1";
            if(tokens.canConsume("CONSTRAINT")) {
                uc_name = parseName(tokens); // UNIQUE CONSTRAINT NAME
            }
            
            tokens.consume("UNIQUE"); // UNIQUE
            tokens.canConsumeAnyOf("CLUSTERED", "NONCLUSTERED");

            AstNode constraintNode = nodeFactory().node(uc_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, UNIQUE);

            // CONSUME COLUMNS
            parseColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

            parseConstraintAttributes(tokens, constraintNode);
            
            parseOptionalPKorUniqueAttributes(tokens, constraintNode);

            consumeComment(tokens);
            
        } else if (tokens.matches("PRIMARY", "KEY") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "PRIMARY", "KEY")) {
            String pk_name = "PK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                pk_name = parseName(tokens);
            }
            tokens.consume("PRIMARY", "KEY");
            tokens.canConsumeAnyOf("CLUSTERED", "NONCLUSTERED");

            AstNode constraintNode = nodeFactory().node(pk_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, PRIMARY_KEY);

            // CONSUME COLUMNS
            parseColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);

            parseConstraintAttributes(tokens, constraintNode);
            
            parseOptionalPKorUniqueAttributes(tokens, constraintNode);

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
            tokens.canConsume("NOT", "FOR", "REPLICATION");
            consumeComment(tokens);
            
        } else if (tokens.matches("CHECK") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "CHECK")) {
            String ck_name = "CHECK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                ck_name = parseName(tokens);
            }
            tokens.consume("CHECK");
            tokens.canConsume("NOT", "FOR", "REPLICATION");
            
            AstNode constraintNode = nodeFactory().node(ck_name, tableNode, mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, CHECK);
            
            String clause = consumeParenBoundedTokens(tokens, true);
            constraintNode.setProperty(CHECK_SEARCH_CONDITION, clause);
        }
    }
    
    
    protected void parseOptionalPKorUniqueAttributes(DdlTokenStream tokens, AstNode parentNode) {
        /*
            [ 
                WITH FILLFACTOR = fillfactor 
               |WITH ( <index_option> [ , ...n ] ) 
            ]
            [ ON { partition_scheme_name (partition_column_name)
                | filegroup | "default" } ] 
         */
        
        if(tokens.canConsume("WITH", "FILLFACTOR")) {
            tokens.consume("=");
            tokens.consume();
            
        } else if (tokens.matches("WITH", L_PAREN)) {
            tokens.consume("WITH");
            parseContentBetweenParens(tokens);
        }
        
        if(tokens.canConsume("ON")) {
            if(tokens.matches(TokenStream.ANY_VALUE, L_PAREN)) {
                tokens.consume(); // partition_scheme_name
                parseContentBetweenParens(tokens); // (partition_column_name)
                
            } else {
                parseName(tokens); // filegroup | "default"
            }
        }
    }
    
    @Override
    protected void parseColumnDefinition( DdlTokenStream tokens,
            AstNode tableNode,
            boolean isAlterTable ) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;
        
        tokens.canConsume("COLUMN");
        String columnName = parseName(tokens);
        AstNode columnNode = nodeFactory().node(columnName, tableNode, TYPE_COLUMN_DEFINITION);

        // TODO columnName == TIMESTAMP -> column of type timestamp without name
        
        if(tokens.canConsume("AS")) {
            // <computed_column_definition> ::= 
            // column_name AS computed_column_expression ...
            columnNode.setProperty(DATATYPE_NAME, "unknown");
            
            String computedColumnExpression = null;
            if(tokens.matches(L_PAREN)) {
                computedColumnExpression = parseContentBetweenParens(tokens);
                
            } else {
                StringBuilder sb = new StringBuilder();
                while (tokens.hasNext() 
                        && !(tokens.matches(COMMA)
                          || tokens.matches("PERSISTED")
                          || tokens.matches("CONSTRAINT")
                          || tokens.matches("PRIMARY", "KEY")
                          || tokens.matches("UNIQUE")
                          || tokens.matches("FOREIGN", "KEY")
                          || tokens.matches("CHECK"))) {
                    if(tokens.matches(L_PAREN)) {
                        sb.append(parseContentBetweenParens(tokens));
                    } else {
                        sb.append(SPACE).append(tokens.consume());
                    }
                }
                computedColumnExpression = sb.toString();
            }
            tokens.canConsume("PERSISTED");
            tokens.canConsume("NOT", "NULL");
            
        } else if(tokens.canConsume("XML", "COLUMN_SET", "FOR", "ALL_SPARSE_COLUMNS")) {
            // <column_set_definition> ::= 
            // column_set_name XML COLUMN_SET FOR ALL_SPARSE_COLUMNS
            columnNode.setProperty(DATATYPE_NAME, "XML COLUMN_SET FOR ALL_SPARSE_COLUMNS");
            
        } else {
            // <column_definition> ::= column_name <data_type> ...
            DataType datatype = getDatatypeParser().parse(tokens);
            getDatatypeParser().setPropertiesOnNode(columnNode, datatype);
        }
        
        
        // Now clauses and constraints can be defined in any order, so we need to keep parsing until we get to a comma
        StringBuffer unusedTokensSB = new StringBuffer();
        
        while (tokens.hasNext() && !tokens.matches(COMMA)) {
            boolean parsedFilestream = tokens.canConsume("FILESTREAM");
            boolean parsedDefaultClause = parseDefaultClause(tokens, columnNode);
            boolean parsedSparse = tokens.canConsume("SPARSE");
            boolean parsedCollate = parseCollateClause(tokens, columnNode);
            boolean parsedConstraint = parseColumnConstraint(tokens, columnNode, isAlterTable);
            boolean parsedIdentity = parseIdentityClause(tokens, columnNode);
            boolean parsedRowguidcol = tokens.canConsume("ROWGUIDCOL");

            if(parsedFilestream) {
                columnNode.setProperty(COLUMN_FILESTREAM, true);
            }
            if(parsedSparse) {
                columnNode.setProperty(COLUMN_SPARSE, true);
            }
            if(parsedIdentity) {
                columnNode.setProperty(COLUMN_IDENTITY, true);
            }
            if(parsedRowguidcol) {
                columnNode.setProperty(COLUMN_ROWGUIDCOL, true);
            }
            if (!parsedFilestream 
                    && !parsedDefaultClause 
                    && !parsedSparse
                    && !parsedCollate
                    && !parsedConstraint
                    && !parsedIdentity
                    && !parsedRowguidcol) {
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
        
        /*
        <column_constraint> ::= 
        [ CONSTRAINT constraint_name ] 
        {     { PRIMARY KEY | UNIQUE } 
                [ CLUSTERED | NONCLUSTERED ] 
                [ 
                    WITH FILLFACTOR = fillfactor  
                  | WITH ( < index_option > [ , ...n ] ) 
                ] 
                [ ON { partition_scheme_name ( partition_column_name ) 
                    | filegroup | "default" } ]
        
          | [ FOREIGN KEY ] 
                REFERENCES [ schema_name . ] referenced_table_name [ ( ref_column ) ] 
                [ ON DELETE { NO ACTION | CASCADE | SET NULL | SET DEFAULT } ] 
                [ ON UPDATE { NO ACTION | CASCADE | SET NULL | SET DEFAULT } ] 
                [ NOT FOR REPLICATION ] 
        
          | CHECK [ NOT FOR REPLICATION ] ( logical_expression ) 
        } 
         */
        String colName = columnNode.getName();

        if (tokens.canConsume("NULL")) {
            columnNode.setProperty(NULLABLE, "NULL");
            result = true;
        } else if (tokens.canConsume("NOT", "NULL")) {
            columnNode.setProperty(NULLABLE, "NOT NULL");
            result = true;
        } else if (tokens.matches("UNIQUE") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "UNIQUE")) {
            result = true;
            String uc_name = "UC_1";
            if(tokens.canConsume("CONSTRAINT")) {
                uc_name = parseName(tokens); // UNIQUE CONSTRAINT NAME
            }
            
            tokens.consume("UNIQUE"); // UNIQUE
            tokens.canConsumeAnyOf("CLUSTERED", "NONCLUSTERED");

            AstNode constraintNode = nodeFactory().node(uc_name, columnNode.getParent(), mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, UNIQUE);

            // CONSUME COLUMNS
//            parseColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);
            nodeFactory().node(colName, constraintNode, TYPE_COLUMN_REFERENCE);

            parseConstraintAttributes(tokens, constraintNode);
            
            parseOptionalPKorUniqueAttributes(tokens, constraintNode);

            consumeComment(tokens);
            
        } else if (tokens.matches("PRIMARY", "KEY") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "PRIMARY", "KEY")) {
            result = true;
            String pk_name = "PK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                pk_name = parseName(tokens);
            }
            tokens.consume("PRIMARY", "KEY");
            tokens.canConsumeAnyOf("CLUSTERED", "NONCLUSTERED");

            AstNode constraintNode = nodeFactory().node(pk_name, columnNode.getParent(), mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, PRIMARY_KEY);

            // CONSUME COLUMNS
//            parseColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);
            nodeFactory().node(colName, constraintNode, TYPE_COLUMN_REFERENCE);

            parseConstraintAttributes(tokens, constraintNode);
            
            parseOptionalPKorUniqueAttributes(tokens, constraintNode);

            consumeComment(tokens);

        } else if (tokens.matches("FOREIGN", "KEY")
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "FOREIGN", "KEY")) {
            result = true;
            String fk_name = "FK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                fk_name = parseName(tokens);
            }
            tokens.consume("FOREIGN", "KEY");

            AstNode constraintNode = nodeFactory().node(fk_name, columnNode.getParent(), mixinType);

            constraintNode.setProperty(CONSTRAINT_TYPE, FOREIGN_KEY);

            // CONSUME COLUMNS
//            parseColumnNameList(tokens, constraintNode, TYPE_COLUMN_REFERENCE);
            nodeFactory().node(colName, constraintNode, TYPE_COLUMN_REFERENCE);

            // Parse the references to table and columns
            parseReferences(tokens, constraintNode);

            parseConstraintAttributes(tokens, constraintNode);

            consumeComment(tokens);
            tokens.canConsume("NOT", "FOR", "REPLICATION");
            consumeComment(tokens);
            
        } else if (tokens.matches("CHECK") 
                || tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "CHECK")) {
            result = true;
            String ck_name = "CHECK_1";
            if(tokens.canConsume("CONSTRAINT")) {
                ck_name = parseName(tokens);
            }
            tokens.consume("CHECK");
            tokens.canConsume("NOT", "FOR", "REPLICATION");
            
            AstNode constraintNode = nodeFactory().node(ck_name, columnNode.getParent(), mixinType);
            constraintNode.setProperty(CONSTRAINT_TYPE, CHECK);
            
            String clause = consumeParenBoundedTokens(tokens, true);
            constraintNode.setProperty(CHECK_SEARCH_CONDITION, clause);
        }

        return result;
    }
    
    @Override
    protected boolean parseDefaultClause(DdlTokenStream tokens, AstNode columnNode) throws ParsingException {
        if(tokens.matches("CONSTRAINT", TokenStream.ANY_VALUE, "DEFAULT")) {
            // [ CONSTRAINT constraint_name ] DEFAULT constant_expression
            // doc: "To maintain compatibility with earlier versions of SQL Server, a constraint name can be assigned to a DEFAULT."
            tokens.consume("CONSTRAINT");
            parseName(tokens);
        }
        return super.parseDefaultClause(tokens, columnNode);
    }
    
    protected boolean parseIdentityClause(DdlTokenStream tokens, AstNode columnNode) throws ParsingException {
        // IDENTITY [ ( seed ,increment ) ] [ NOT FOR REPLICATION ] 
        if(tokens.canConsume("IDENTITY")) {
            if(tokens.matches(L_PAREN)) {
                parseContentBetweenParens(tokens);
            }
            
            tokens.canConsume("NOT", "FOR", "REPLICATION");
            return true;
        }
        
        return false;
    }
    
    @Override
    protected AstNode parseAlterTableStatement( DdlTokenStream tokens,
                                                AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches("ALTER", "TABLE", TokenStream.ANY_VALUE, "ADD")) {

            // ALTER TABLE
            // ADD ( {column_definition | virtual_column_definition
            // [, column_definition | virtual_column_definition] ... } [ column_properties ]

            markStartOfStatement(tokens);

            tokens.consume(ALTER, TABLE);

            String tableName = parseName(tokens);

            AstNode alterTableNode = nodeFactory().node(tableName, parentNode, TYPE_ALTER_TABLE_STATEMENT);

            tokens.consume("ADD"); // FIXME MK "ALTER COLUMN", "DROP" and others...

            // System.out.println("  >> PARSING ALTER STATEMENT >>  TABLE Name = " + tableName);

            if (isTableConstraint(tokens)) {
                parseTableConstraint(tokens, alterTableNode, true);
            } else {
                // This segment can also be enclosed in "()" brackets to handle multiple ColumnDefinition ADDs
                if (tokens.matches(L_PAREN, "REF")) {
                    // ALTER TABLE staff ADD (REF(dept) WITH ROWID);
                    tokens.consume(L_PAREN, "REF", L_PAREN);
                    parseName(tokens);
                    tokens.consume(R_PAREN, "WITH", "ROWID", R_PAREN);

                } else if (tokens.matches(L_PAREN, "SCOPE")) {
                    // ALTER TABLE staff ADD (SCOPE FOR (dept) IS offices);
                    tokens.consume(L_PAREN, "SCOPE", "FOR", L_PAREN);
                    parseName(tokens);
                    tokens.consume(R_PAREN, "IS");
                    parseName(tokens);
                    tokens.consume(R_PAREN);
                } else if (tokens.matches(L_PAREN)) {
                    parseColumns(tokens, alterTableNode, true);
                } else {
                    // Assume single ADD COLUMN
                    parseSingleTerminatedColumnDefinition(tokens, alterTableNode, true);
                }
            }

            parseUntilTerminator(tokens); // COULD BE "NESTED TABLE xxxxxxxx" option clause

            markEndOfStatement(tokens, alterTableNode);

            return alterTableNode;
        } else if (tokens.matches("ALTER", "TABLE", TokenStream.ANY_VALUE, "DROP")) {
            markStartOfStatement(tokens);

            tokens.consume(ALTER, TABLE);

            String tableName = parseName(tokens);

            AstNode alterTableNode = nodeFactory().node(tableName, parentNode, TYPE_ALTER_TABLE_STATEMENT);

            tokens.consume(DROP);

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

                String columnName = parseName(tokens);

                AstNode columnNode = nodeFactory().node(columnName, alterTableNode, TYPE_DROP_COLUMN_DEFINITION);

                if (tokens.canConsume(DropBehavior.CASCADE)) {
                    columnNode.setProperty(DROP_BEHAVIOR, DropBehavior.CASCADE);
                } else if (tokens.canConsume(DropBehavior.RESTRICT)) {
                    columnNode.setProperty(DROP_BEHAVIOR, DropBehavior.RESTRICT);
                }
            } else {
                parseUntilTerminator(tokens); // EXAMPLE: "DROP UNIQUE (email)", or "DROP (col_1, col_2)"
            }

            markEndOfStatement(tokens, alterTableNode);

            return alterTableNode;
        } else if (tokens.matches("ALTER", "TABLE", TokenStream.ANY_VALUE, "RENAME")) {

            // ALTER TABLE customers RENAME TO my_customers;
            // ALTER TABLE customers RENAME CONSTRAINT cust_fname_nn TO cust_firstname_nn;
            markStartOfStatement(tokens);

            tokens.consume(ALTER, TABLE);

            String oldName = parseName(tokens);
            AstNode alterTableNode = nodeFactory().node(oldName, parentNode, TYPE_ALTER_TABLE_STATEMENT);

            if (tokens.canConsume("RENAME", "TO")) {
                String newName = parseName(tokens);
                alterTableNode.setProperty(NEW_NAME, newName);

                parseUntilTerminator(tokens);

            } else if (tokens.canConsume("RENAME", "COLUMN")) {
                String oldColumnName = parseName(tokens);
                tokens.consume("TO");
                String newColumnName = parseName(tokens);

                parseUntilTerminator(tokens);

                AstNode renameColumnNode = nodeFactory().node(oldColumnName, alterTableNode, TYPE_RENAME_COLUMN);
                renameColumnNode.setProperty(NEW_NAME, newColumnName);

            } else if (tokens.canConsume("RENAME", "CONSTRAINT")) {
                String oldConstraintName = parseName(tokens);
                tokens.consume("TO");
                String newConstraintName = parseName(tokens);

                parseUntilTerminator(tokens);

                AstNode renameColumnNode = nodeFactory().node(oldConstraintName, alterTableNode, TYPE_RENAME_CONSTRAINT);
                renameColumnNode.setProperty(NEW_NAME, newConstraintName);
            }

            markEndOfStatement(tokens, alterTableNode);

            return alterTableNode;
        } else if (tokens.matches("ALTER", "TABLE", TokenStream.ANY_VALUE, "MODIFY")) {

        }

        return super.parseAlterTableStatement(tokens, parentNode);
    }

    @Override
    protected AstNode parseAlterStatement( DdlTokenStream tokens,
                                           AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(ALTER, TABLE)) {
            return parseAlterTableStatement(tokens, parentNode);
            
        } else if (tokens.matches(STMT_ALTER_APPLICATION_ROLE)) {
            return parseStatement(tokens, STMT_ALTER_APPLICATION_ROLE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_ASSEMBLY)) {
            return parseStatement(tokens, STMT_ALTER_ASSEMBLY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_ASYMMETRIC_KEY)) {
            return parseStatement(tokens, STMT_ALTER_ASYMMETRIC_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_AUTHORIZATION)) {
            return parseStatement(tokens, STMT_ALTER_AUTHORIZATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_BROKER_PRIORITY)) {
            return parseStatement(tokens, STMT_ALTER_BROKER_PRIORITY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_CERTIFICATE)) {
            return parseStatement(tokens, STMT_ALTER_CERTIFICATE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_CREDENTIAL)) {
            return parseStatement(tokens, STMT_ALTER_CREDENTIAL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_CRYPTOGRAPHIC_PROVIDER)) {
            return parseStatement(tokens, STMT_ALTER_CRYPTOGRAPHIC_PROVIDER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_DATABASE)) {
            return parseStatement(tokens, STMT_ALTER_DATABASE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_ENDPOINT)) {
            return parseStatement(tokens, STMT_ALTER_ENDPOINT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_EVENT_SESSION)) {
            return parseStatement(tokens, STMT_ALTER_EVENT_SESSION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_FULLTEXT_CATALOG)) {
            return parseStatement(tokens, STMT_ALTER_FULLTEXT_CATALOG, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_FULLTEXT_INDEX)) {
            return parseStatement(tokens, STMT_ALTER_FULLTEXT_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_FULLTEXT_STOPLIST)) {
            return parseStatement(tokens, STMT_ALTER_FULLTEXT_STOPLIST, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_FUNCTION)) {
            return parseStatement(tokens, STMT_ALTER_FUNCTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_INDEX)) {
            return parseStatement(tokens, STMT_ALTER_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_LOGIN)) {
            return parseStatement(tokens, STMT_ALTER_LOGIN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_MASTER_KEY)) {
            return parseStatement(tokens, STMT_ALTER_MASTER_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_MESSAGE_TYPE)) {
            return parseStatement(tokens, STMT_ALTER_MESSAGE_TYPE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_PARTITION_FUNCTION)) {
            return parseStatement(tokens, STMT_ALTER_PARTITION_FUNCTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_PARTITION_SCHEME)) {
            return parseStatement(tokens, STMT_ALTER_PARTITION_SCHEME, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_PROCEDURE)) {
            return parseStatement(tokens, STMT_ALTER_PROCEDURE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_PROC)) {
            return parseStatement(tokens, STMT_ALTER_PROC, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_QUEUE)) {
            return parseStatement(tokens, STMT_ALTER_QUEUE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_REMOTE_SERVICE_BINDING)) {
            return parseStatement(tokens, STMT_ALTER_REMOTE_SERVICE_BINDING, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_RESOURCE_GOVERNOR)) {
            return parseStatement(tokens, STMT_ALTER_RESOURCE_GOVERNOR, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_RESOURCE_POOL)) {
            return parseStatement(tokens, STMT_ALTER_RESOURCE_POOL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_ROLE)) {
            return parseStatement(tokens, STMT_ALTER_ROLE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_ROUTE)) {
            return parseStatement(tokens, STMT_ALTER_ROUTE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SCHEMA)) {
            return parseStatement(tokens, STMT_ALTER_SCHEMA, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SEARCH_PROPERTY_LIST)) {
            return parseStatement(tokens, STMT_ALTER_SEARCH_PROPERTY_LIST, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SEQUENCE)) {
            return parseStatement(tokens, STMT_ALTER_SEQUENCE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SERVER_AUDIT)) {
            return parseStatement(tokens, STMT_ALTER_SERVER_AUDIT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SERVER_AUDIT_SPECIFICATION)) {
            return parseStatement(tokens, STMT_ALTER_SERVER_AUDIT_SPECIFICATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SERVICE)) {
            return parseStatement(tokens, STMT_ALTER_SERVICE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SERVICE_MASTER_KEY)) {
            return parseStatement(tokens, STMT_ALTER_SERVICE_MASTER_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_SYMMETRIC_KEY)) {
            return parseStatement(tokens, STMT_ALTER_SYMMETRIC_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_TRIGGER)) {
            return parseStatement(tokens, STMT_ALTER_TRIGGER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_USER)) {
            return parseStatement(tokens, STMT_ALTER_USER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_VIEW)) {
            return parseStatement(tokens, STMT_ALTER_VIEW, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_WORKLOAD_GROUP)) {
            return parseStatement(tokens, STMT_ALTER_WORKLOAD_GROUP, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_ALTER_XML_SCHEMA_COLLECTION)) {
            return parseStatement(tokens, STMT_ALTER_XML_SCHEMA_COLLECTION, parentNode, TYPE_STATEMENT);
        }

        return super.parseAlterStatement(tokens, parentNode);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#parseCreateViewStatement(org.modeshape.sequencer.ddl.DdlTokenStream,
     *      org.modeshape.sequencer.ddl.node.AstNode)
     */
    @Override
    protected AstNode parseCreateViewStatement( DdlTokenStream tokens,
                                                AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);
        // CREATE VIEW [ schema_name . ] view_name [ (column [ ,...n ] ) ] 
        //         [ WITH <view_attribute> [ ,...n ] ] 
        //         AS select_statement 
        //         [ WITH CHECK OPTION ] [ ; ]
        // 
        //         <view_attribute> ::= 
        //         {
        //             [ ENCRYPTION ]
        //             [ SCHEMABINDING ]
        //             [ VIEW_METADATA ]     } 
        
        
        tokens.consume("CREATE");
        tokens.consume("VIEW");
        String name = parseName(tokens);
        AstNode createViewNode = nodeFactory().node(name, parentNode, TYPE_CREATE_VIEW_STATEMENT);

        // CONSUME COLUMNS
        parseColumnNameList(tokens, createViewNode, TYPE_COLUMN_REFERENCE);
        
        // WITH ENCRYPTION, SCHEMABINDING, VIEW_METADATA
        if (tokens.canConsume("WITH")) {
            StringBuilder sb = new StringBuilder();
            
            do {
                sb.append(SPACE).append(tokens.consume());
            } while (!tokens.matches("AS"));
            
            createViewNode.setProperty(VIEW_WITH_ATTRIBUTES, sb.toString());
        }
        
        tokens.consume("AS");
        
        // "WITH CHECK OPTION" is consumed in queryExpression - check it
        String queryExpression = parseUntilTerminator(tokens);
        if(queryExpression.toUpperCase().endsWith("WITH CHECK OPTION")){
            queryExpression = queryExpression.substring(0, queryExpression.length() - "WITH CHECK OPTION".length());
            createViewNode.setProperty(VIEW_WITH_CHECK_OPTION, true);
        }
        
        createViewNode.setProperty(CREATE_VIEW_QUERY_EXPRESSION, queryExpression);
        
        markEndOfStatement(tokens, createViewNode);
        
        return createViewNode;
    }

    /**
     * If the index type is a bitmap-join the columns are from the dimension tables which are defined in the FROM clause. All other
     * index types the columns are from the table the index in on.
     * <p>
     * <code>
     * column-expression == left-paren column-name [ASC | DESC] | constant | function [, column-name [ASC | DESC] | constant | function ]* right-paren
     * </code>
     * 
     * @param columnExpressionList the comma separated column expression list (cannot be <code>null</code>)
     * @param indexNode the index node whose column expression list is being processed (cannot be <code>null</code>)
     */
    private void parseIndexColumnExpressionList( final String columnExpressionList,
                                                 final AstNode indexNode ) {
        final DdlTokenStream tokens = new DdlTokenStream(columnExpressionList, DdlTokenStream.ddlTokenizer(false), false);
        tokens.start();

        tokens.consume(L_PAREN); // must have opening paren
        int numLeft = 1;
        int numRight = 0;

        // must have content between the parens
        if (!tokens.matches(R_PAREN)) {
            final List<String> possibleColumns = new ArrayList<String>(); // dimension table columns
            final List<String> functions = new ArrayList<String>(); // functions, constants
            final StringBuilder text = new StringBuilder();
            boolean isFunction = false;

            while (tokens.hasNext()) {
                if (tokens.canConsume(COMMA)) {
                    if (isFunction) {
                        functions.add(text.toString());
                    } else {
                        possibleColumns.add(text.toString());
                    }

                    text.setLength(0); // clear out
                    isFunction = false;
                    continue;
                }

                if (tokens.matches(L_PAREN)) {
                    isFunction = true;
                    ++numLeft;
                } else if (tokens.matches("ASC") || tokens.matches("DESC")) {
                    text.append(SPACE);
                } else if (tokens.matches(R_PAREN)) {
                    if (numLeft == ++numRight) {
                        if (isFunction) {
                            functions.add(text.toString());
                        } else {
                            possibleColumns.add(text.toString());
                        }

                        break;
                    }
                }

                text.append(tokens.consume());
            }

            if (!possibleColumns.isEmpty()) {
                List<AstNode> tableNodes = null;
                final boolean tableIndex = indexNode.hasMixin(SqlServerDdlLexicon.TYPE_CREATE_INDEX_STATEMENT);

                // find appropriate table nodes
                if (tableIndex) {
                    // table index so find table node
                    final String tableName = (String)indexNode.getProperty(SqlServerDdlLexicon.TABLE_NAME);
                    final AstNode parent = indexNode.getParent();
                    final List<AstNode> nodes = parent.childrenWithName(tableName);

                    if (!nodes.isEmpty()) {
                        if (nodes.size() == 1) {
                            tableNodes = nodes;
                        } else {
                            // this should not be possible but check none the less
                            for (final AstNode node : nodes) {
                                if (node.hasMixin(StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT)) {
                                    tableNodes = new ArrayList<AstNode>(1);
                                    tableNodes.add(node);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    // must be bitmap-join
                    tableNodes = indexNode.getChildren(StandardDdlLexicon.TYPE_TABLE_REFERENCE);
                }

                if ((tableNodes != null) && !tableNodes.isEmpty()) {
                    boolean processed = false;

                    for (String possibleColumn : possibleColumns) {
                        // first determine any ordering
                        final int ascIndex = possibleColumn.toUpperCase().indexOf(" ASC");
                        final boolean asc = (ascIndex != -1);
                        final int descIndex = possibleColumn.toUpperCase().indexOf(" DESC");
                        boolean desc = (descIndex != -1);
    
                        // adjust column name if there is ordering
                        if (asc) {
                            possibleColumn = possibleColumn.substring(0, ascIndex);
                        } else if (desc) {
                            possibleColumn = possibleColumn.substring(0, descIndex);
                        }

                        if (tableIndex) {
                            if (tableNodes.isEmpty()) {
                                if (asc) {
                                    functions.add(possibleColumn + SPACE + "ASC");
                                } else if (desc) {
                                    functions.add(possibleColumn + SPACE + "DESC");
                                } else {
                                    functions.add(possibleColumn);
                                }
                            } else {
                                // only one table reference. need to find column.
                                final AstNode tableNode = tableNodes.get(0);
                                final List<AstNode> columnNodes = tableNode.getChildren(StandardDdlLexicon.TYPE_COLUMN_DEFINITION);

                                if (!columnNodes.isEmpty()) {
                                    // find column
                                    for (final AstNode colNode : columnNodes) {
                                        if (colNode.getName().toUpperCase().equals(possibleColumn.toUpperCase())) {
                                            final AstNode colRef = nodeFactory().node(possibleColumn, indexNode, TYPE_COLUMN_REFERENCE);
                                            
                                            if (asc || desc) {
                                                colRef.addMixin(SqlServerDdlLexicon.TYPE_INDEX_ORDERABLE);
                
                                                if (asc) {
                                                    colRef.setProperty(SqlServerDdlLexicon.INDEX_ORDER, "ASC");
                                                } else {
                                                    colRef.setProperty(SqlServerDdlLexicon.INDEX_ORDER, "DESC");
                                                }
                                            }
                
                                            processed = true;
                                            break;
                                        }
                                    }
                                }
                                
                                if (!processed) {
                                    if (asc) {
                                        functions.add(possibleColumn + SPACE + "ASC");
                                    } else if (desc) {
                                        functions.add(possibleColumn + SPACE + "DESC");
                                    } else {
                                        functions.add(possibleColumn);
                                    }

                                    processed = true;
                                }
                            }
                        } else {
                            // bitmap-join
                            for (final AstNode dimensionTableNode : tableNodes) {
                                if (possibleColumn.toUpperCase().startsWith(dimensionTableNode.getName().toUpperCase() + PERIOD)) {
                                    final AstNode colRef = nodeFactory().node(possibleColumn, indexNode, TYPE_COLUMN_REFERENCE);
                                    
                                    if (asc || desc) {
                                        colRef.addMixin(SqlServerDdlLexicon.TYPE_INDEX_ORDERABLE);
        
                                        if (asc) {
                                            colRef.setProperty(SqlServerDdlLexicon.INDEX_ORDER, "ASC");
                                        } else {
                                            colRef.setProperty(SqlServerDdlLexicon.INDEX_ORDER, "DESC");
                                        }
                                    }
        
                                    processed = true;
                                    break;
                                }
                            }

                            // probably a constant or function
                            if (!processed) {
                                if (asc) {
                                    functions.add(possibleColumn + SPACE + "ASC");
                                } else if (desc) {
                                    functions.add(possibleColumn + SPACE + "DESC");
                                } else {
                                    functions.add(possibleColumn);
                                }

                                processed = true;
                            }
                        }
                    }
                }
            }

            if (!functions.isEmpty()) {
                indexNode.setProperty(SqlServerDdlLexicon.OTHER_INDEX_REFS, functions);
            }
        }

        if (numLeft != numRight) {
            throw new ParsingException(tokens.nextPosition());
        }

        tokens.consume(R_PAREN); // must have closing paren
    }
    
    /**
     * Parses DDL CREATE INDEX
     * <p>
     * <code>
     * CREATE [ UNIQUE | BITMAP ] INDEX index-name ON { cluster_index_clause | table_index_clause | bitmap_join_index_clause } [index_attributes] [UNUSABLE]
     * 
     * cluster_index_clause = CLUSTER cluster-name
     * table_index_clause = table-name [table-alias] ( { column | constant | function } [ ASC | DESC ] [ , { column | column_expression } [ ASC | DESC ]] * )
     * bitmap_join_index_clause = table-name ( column [ASC | DESC] [, column [ASC | DESC]] ) FROM table-name [, table-name] WHERE condition [local-partition-index]
     * 
     * </code>
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
        assert (tokens.matches(STMT_CREATE_INDEX) || tokens.matches(STMT_CREATE_UNIQUE_INDEX)
                || tokens.matches(STMT_CREATE_CLUSTERED_INDEX) || tokens.matches(STMT_CREATE_NONCLUSTERED_INDEX)
                || tokens.matches(STMT_CREATE_UNIQUE_CLUSTERED_INDEX) || tokens.matches(STMT_CREATE_UNIQUE_NONCLUSTERED_INDEX));

        markStartOfStatement(tokens);
        tokens.consume(CREATE);

        final boolean isUnique = tokens.canConsume(UNIQUE);
        final boolean isClustered = tokens.canConsume("CLUSTERED");
        final boolean isNonclustered = tokens.canConsume("NONCLUSTERED");

        tokens.consume(INDEX);
        final String indexName = parseName(tokens);

        tokens.consume(ON);

        AstNode indexNode = null;

        final String tableName = parseName(tokens);
        indexNode = nodeFactory().node(indexName, parentNode, TYPE_CREATE_INDEX_STATEMENT);
        indexNode.setProperty(SqlServerDdlLexicon.INDEX_TYPE, SqlServerDdlConstants.IndexTypes.TABLE);
        indexNode.setProperty(SqlServerDdlLexicon.TABLE_NAME, tableName);
        indexNode.setProperty(UNIQUE_INDEX, isUnique);
        indexNode.setProperty(INDEX_CLUSTERED, isClustered);
        indexNode.setProperty(INDEX_NONCLUSTERED, isNonclustered);
        
        // parse left-paren content right-paren
        final String columnExpressionList = parseContentBetweenParens(tokens);
        parseIndexColumnExpressionList('(' + columnExpressionList + ')', indexNode);


        boolean lastMatched = true;
        while (tokens.hasNext() && !isTerminator(tokens) && lastMatched) {

            if(tokens.canConsume("INCLUDE")) {
                String includeColumnsList = parseContentBetweenParens(tokens);
                indexNode.setProperty(INCLUDE_COLUMNS, includeColumnsList);
                
            } else if(tokens.canConsume("WITH")) {
                String withOptionsList = parseContentBetweenParens(tokens);
                indexNode.setProperty(WITH_OPTIONS, withOptionsList);
                
            } else if(tokens.canConsume("ON")) {
                String name = parseName(tokens);
                if(tokens.matches(L_PAREN)) {
                    String columnName = parseContentBetweenParens(tokens);
                    name = name + " (" + columnName + ")";
                }
                indexNode.setProperty(ON_CLAUSE, name);
                
            } else if(tokens.canConsume("FILESTREAM_ON")) {
                String name = parseName(tokens);
                indexNode.setProperty(FILESTREAM_ON_CLAUSE, name);
                
            } else if(tokens.canConsume("WHERE")) {
                // it can be any expression... parse until next keyword or terminator
                StringBuilder sb = new StringBuilder();
                while (tokens.hasNext() && !isTerminator(tokens)
                        && !(tokens.matches("INCLUDE") 
                                || tokens.matches("WITH") 
                                || tokens.matches("ON") 
                                || tokens.matches("FILESTREAM_ON"))){
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

    private void parseTableReferenceList( final DdlTokenStream tokens,
                                          final AstNode parentNode ) {
        final List<String> tableRefs = parseNameList(tokens);

        if (!tableRefs.isEmpty()) {
            for (String tableName : tableRefs) {
                nodeFactory().node(tableName, parentNode, TYPE_TABLE_REFERENCE);
            }
        }
    }
    
    protected AstNode parseCreateSequenceStatement(final DdlTokenStream tokens, final AstNode parentNode)
            throws ParsingException {
        assert tokens != null;
        assert parentNode != null;
        assert tokens.matches(STMT_CREATE_SEQUENCE);
        
        // CREATE SEQUENCE [schema_name . ] sequence_name
        // [ AS [ built_in_integer_type | user-defined_integer_type ] ]
        // [ START WITH <constant> ]
        // [ INCREMENT BY <constant> ]
        // [ { MINVALUE [ <constant> ] } | { NO MINVALUE } ]
        // [ { MAXVALUE [ <constant> ] } | { NO MAXVALUE } ]
        // [ CYCLE | { NO CYCLE } ]
        // [ { CACHE [ <constant> ] } | { NO CACHE } ]
        // [ ; ]

        markStartOfStatement(tokens);
        tokens.consume(STMT_CREATE_SEQUENCE);
        
        final String sequenceName = parseName(tokens);
        AstNode sequenceNode = nodeFactory().node(sequenceName, parentNode, TYPE_CREATE_SEQUENCE_STATEMENT);

        boolean lastMatched = true;
        while (tokens.hasNext() && !isTerminator(tokens) && lastMatched) {
            
            if(tokens.canConsume("INCREMENT", "BY")) {
                String value = tokens.consume();
                long longValue = Long.parseLong(value);
                sequenceNode.setProperty(SEQ_INCREMENT_BY, longValue);
                
            } else if(tokens.canConsume("START", "WITH")) {
                String value = tokens.consume();
                long longValue = Long.parseLong(value);
                sequenceNode.setProperty(SEQ_START_WITH, longValue);
                
            } else if(tokens.canConsume("NO", "MAXVALUE")) {
                sequenceNode.setProperty(SEQ_NO_MAX_VALUE, true);
                
            } else if(tokens.canConsume("MAXVALUE")) {
                String value = tokens.consume();
                long longValue = Long.parseLong(value);
                sequenceNode.setProperty(SEQ_MAX_VALUE, longValue);
                
            } else if(tokens.canConsume("NO", "MINVALUE")) {
                sequenceNode.setProperty(SEQ_NO_MIN_VALUE, true);
                
            } else if(tokens.canConsume("MINVALUE")) {
                String value = tokens.consume();
                long longValue = Long.parseLong(value);
                sequenceNode.setProperty(SEQ_MIN_VALUE, longValue);
                
            } else if(tokens.canConsume("NO", "CYCLE")) {
                sequenceNode.setProperty(SEQ_CYCLE, false);
                
            } else if(tokens.canConsume("CYCLE")) {
                sequenceNode.setProperty(SEQ_CYCLE, true);
                
            } else if(tokens.canConsume("NO", "CACHE")) {
                sequenceNode.setProperty(SEQ_NO_CACHE, true);
                
            } else if(tokens.canConsume("CACHE")) {
                String value = tokens.consume();
                long longValue = Long.parseLong(value);
                sequenceNode.setProperty(SEQ_CACHE, longValue);
                
            } else if(tokens.canConsume("AS")) {
                String dataType = tokens.consume();
                sequenceNode.setProperty(SEQ_AS_DATA_TYPE, dataType);
                
            } else {
                // unknown sequence parameter
                lastMatched = false;
            }
        }
        
        parseUntilTerminator(tokens);

        markEndOfStatement(tokens, sequenceNode);
        return sequenceNode;
    }

    @Override
    protected AstNode parseSetStatement( DdlTokenStream tokens,
                                         AstNode parentNode ) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        if (tokens.matches(STMT_SET_DATEFIRST)) {
            return parseStatement(tokens, STMT_SET_DATEFIRST, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_DATEFORMAT)) {
            return parseStatement(tokens, STMT_SET_DATEFORMAT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_DEADLOCK_PRIORITY)) {
            return parseStatement(tokens, STMT_SET_DEADLOCK_PRIORITY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_LOCK_TIMEOUT)) {
            return parseStatement(tokens, STMT_SET_LOCK_TIMEOUT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_CONCAT_NULL_YIELDS_NULL)) {
            return parseStatement(tokens, STMT_SET_CONCAT_NULL_YIELDS_NULL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_CURSOR_CLOSE_ON_COMMIT)) {
            return parseStatement(tokens, STMT_SET_CURSOR_CLOSE_ON_COMMIT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_FIPS_FLAGGER)) {
            return parseStatement(tokens, STMT_SET_FIPS_FLAGGER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_IDENTITY_INSERT)) {
            return parseStatement(tokens, STMT_SET_IDENTITY_INSERT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_LANGUAGE)) {
            return parseStatement(tokens, STMT_SET_LANGUAGE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_OFFSETS)) {
            return parseStatement(tokens, STMT_SET_OFFSETS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_QUOTED_IDENTIFIER)) {
            return parseStatement(tokens, STMT_SET_QUOTED_IDENTIFIER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ARITHABORT)) {
            return parseStatement(tokens, STMT_SET_ARITHABORT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ARITHIGNORE)) {
            return parseStatement(tokens, STMT_SET_ARITHIGNORE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_FMTONLY)) {
            return parseStatement(tokens, STMT_SET_FMTONLY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ANSI_DEFAULTS)) {
            return parseStatement(tokens, STMT_SET_ANSI_DEFAULTS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ANSI_NULL_DFLT_OFF)) {
            return parseStatement(tokens, STMT_SET_ANSI_NULL_DFLT_OFF, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ANSI_NULL_DFLT_ON)) {
            return parseStatement(tokens, STMT_SET_ANSI_NULL_DFLT_ON, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ANSI_NULLS)) {
            return parseStatement(tokens, STMT_SET_ANSI_NULLS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ANSI_PADDING)) {
            return parseStatement(tokens, STMT_SET_ANSI_PADDING, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ANSI_WARNINGS)) {
            return parseStatement(tokens, STMT_SET_ANSI_WARNINGS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_FORCEPLAN)) {
            return parseStatement(tokens, STMT_SET_FORCEPLAN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_SHOWPLAN_ALL)) {
            return parseStatement(tokens, STMT_SET_SHOWPLAN_ALL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_SHOWPLAN_TEXT)) {
            return parseStatement(tokens, STMT_SET_SHOWPLAN_TEXT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_SHOWPLAN_XML)) {
            return parseStatement(tokens, STMT_SET_SHOWPLAN_XML, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_STATISTICS)) {
            return parseStatement(tokens, STMT_SET_STATISTICS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_IMPLICIT_TRANSACTIONS)) {
            return parseStatement(tokens, STMT_SET_IMPLICIT_TRANSACTIONS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_REMOTE_PROC_TRANSACTIONS)) {
            return parseStatement(tokens, STMT_SET_REMOTE_PROC_TRANSACTIONS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_TRANSACTION_ISOLATION_LEVEL)) {
            return parseStatement(tokens, STMT_SET_TRANSACTION_ISOLATION_LEVEL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_XACT_ABORT)) {
            return parseStatement(tokens, STMT_SET_XACT_ABORT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_NOCOUNT)) {
            return parseStatement(tokens, STMT_SET_NOCOUNT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_NOEXEC)) {
            return parseStatement(tokens, STMT_SET_NOEXEC, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_NUMERIC_ROUNDABORT)) {
            return parseStatement(tokens, STMT_SET_NUMERIC_ROUNDABORT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_PARSEONLY)) {
            return parseStatement(tokens, STMT_SET_PARSEONLY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_QUERY_GOVERNOR_COST_LIMIT)) {
            return parseStatement(tokens, STMT_SET_QUERY_GOVERNOR_COST_LIMIT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_ROWCOUNT)) {
            return parseStatement(tokens, STMT_SET_ROWCOUNT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_SET_TEXTSIZE)) {
            return parseStatement(tokens, STMT_SET_TEXTSIZE, parentNode, TYPE_STATEMENT);
        }

        return super.parseSetStatement(tokens, parentNode);
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
        } else if (tokens.matches(STMT_DROP_AGGREGATE)) {
            return parseStatement(tokens, STMT_DROP_AGGREGATE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_APPLICATION_ROLE)) {
            return parseStatement(tokens, STMT_DROP_APPLICATION_ROLE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_ASSEMBLY)) {
            return parseStatement(tokens, STMT_DROP_ASSEMBLY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_ASYMMETRIC_KEY)) {
            return parseStatement(tokens, STMT_DROP_ASYMMETRIC_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_BROKER_PRIORITY)) {
            return parseStatement(tokens, STMT_DROP_BROKER_PRIORITY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_CERTIFICATE)) {
            return parseStatement(tokens, STMT_DROP_CERTIFICATE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_CONTRACT)) {
            return parseStatement(tokens, STMT_DROP_CONTRACT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_CREDENTIAL)) {
            return parseStatement(tokens, STMT_DROP_CREDENTIAL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_CRYPTOGRAPHIC_PROVIDER)) {
            return parseStatement(tokens, STMT_DROP_CRYPTOGRAPHIC_PROVIDER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_DATABASE_AUDIT_SPECIFICATION)) {
            return parseStatement(tokens, STMT_DROP_DATABASE_AUDIT_SPECIFICATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_DATABASE_ENCRYPTION_KEY)) {
            return parseStatement(tokens, STMT_DROP_DATABASE_ENCRYPTION_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_DATABASE)) {
            return parseStatement(tokens, STMT_DROP_DATABASE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_DEFAULT)) {
            return parseStatement(tokens, STMT_DROP_DEFAULT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_ENDPOINT)) {
            return parseStatement(tokens, STMT_DROP_ENDPOINT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_EVENT_NOTIFICATION)) {
            return parseStatement(tokens, STMT_DROP_EVENT_NOTIFICATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_EVENT_SESSION)) {
            return parseStatement(tokens, STMT_DROP_EVENT_SESSION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_FULLTEXT_CATALOG)) {
            return parseStatement(tokens, STMT_DROP_FULLTEXT_CATALOG, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_FULLTEXT_INDEX)) {
            return parseStatement(tokens, STMT_DROP_FULLTEXT_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_FULLTEXT_STOPLIST)) {
            return parseStatement(tokens, STMT_DROP_FULLTEXT_STOPLIST, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_FUNCTION)) {
            return parseStatement(tokens, STMT_DROP_FUNCTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_INDEX)) {
            return parseStatement(tokens, STMT_DROP_INDEX, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_LOGIN)) {
            return parseStatement(tokens, STMT_DROP_LOGIN, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_MASTER_KEY)) {
            return parseStatement(tokens, STMT_DROP_MASTER_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_MESSAGE_TYPE)) {
            return parseStatement(tokens, STMT_DROP_MESSAGE_TYPE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_PARTITION_FUNCTION)) {
            return parseStatement(tokens, STMT_DROP_PARTITION_FUNCTION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_PARTITION_SCHEME)) {
            return parseStatement(tokens, STMT_DROP_PARTITION_SCHEME, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_PROCEDURE)) {
            return parseStatement(tokens, STMT_DROP_PROCEDURE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_PROC)) {
            return parseStatement(tokens, STMT_DROP_PROC, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_QUEUE)) {
            return parseStatement(tokens, STMT_DROP_QUEUE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_REMOTE_SERVICE_BINDING)) {
            return parseStatement(tokens, STMT_DROP_REMOTE_SERVICE_BINDING, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_RESOURCE_POOL)) {
            return parseStatement(tokens, STMT_DROP_RESOURCE_POOL, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_ROLE)) {
            return parseStatement(tokens, STMT_DROP_ROLE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_ROUTE)) {
            return parseStatement(tokens, STMT_DROP_ROUTE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_RULE)) {
            return parseStatement(tokens, STMT_DROP_RULE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SEARCH_PROPERTY_LIST)) {
            return parseStatement(tokens, STMT_DROP_SEARCH_PROPERTY_LIST, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SEQUENCE)) {
            return parseStatement(tokens, STMT_DROP_SEQUENCE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SERVER_AUDIT_SPECIFICATION)) {
            return parseStatement(tokens, STMT_DROP_SERVER_AUDIT_SPECIFICATION, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SERVER_AUDIT)) {
            return parseStatement(tokens, STMT_DROP_SERVER_AUDIT, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SERVICE)) {
            return parseStatement(tokens, STMT_DROP_SERVICE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SIGNATURE)) {
            return parseStatement(tokens, STMT_DROP_SIGNATURE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_COUNTER_SIGNATURE)) {
            return parseStatement(tokens, STMT_DROP_COUNTER_SIGNATURE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_STATISTICS)) {
            return parseStatement(tokens, STMT_DROP_STATISTICS, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SYMMETRIC_KEY)) {
            return parseStatement(tokens, STMT_DROP_SYMMETRIC_KEY, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_SYNONYM)) {
            return parseStatement(tokens, STMT_DROP_SYNONYM, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_TRIGGER)) {
            return parseStatement(tokens, STMT_DROP_TRIGGER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_TYPE)) {
            return parseStatement(tokens, STMT_DROP_TYPE, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_USER)) {
            return parseStatement(tokens, STMT_DROP_USER, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_WORKLOAD_GROUP)) {
            return parseStatement(tokens, STMT_DROP_WORKLOAD_GROUP, parentNode, TYPE_STATEMENT);
        } else if (tokens.matches(STMT_DROP_XML_SCHEMA_COLLECTION)) {
            return parseStatement(tokens, STMT_DROP_XML_SCHEMA_COLLECTION, parentNode, TYPE_STATEMENT);
        }

        return super.parseDropStatement(tokens, parentNode);
    }

    /**
     * Utility method designed to parse columns within an ALTER TABLE ADD statement.
     * 
     * @param tokens the tokenized {@link DdlTokenStream} of the DDL input content; may not be null
     * @param tableNode
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
                parseColumnDefinition(localTokens, tableNode, true);
            } else {
                // THIS IS AN ERROR. NOTHING FOUND.
                // NEED TO absorb tokens
                while (localTokens.hasNext() && !localTokens.matches(COMMA)) {
                    unusedTokensSB.append(SPACE).append(localTokens.consume());
                }
            }
        } while (localTokens.canConsume(COMMA));

        if (unusedTokensSB.length() > 0) {
            String msg = DdlSequencerI18n.unusedTokensParsingColumnDefinition.text(tableNode.getName());
            DdlParserProblem problem = new DdlParserProblem(Problems.WARNING, getCurrentMarkedPosition(), msg);
            problem.setUnusedSource(unusedTokensSB.toString());
            addProblem(problem, tableNode);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#getValidSchemaChildTypes()
     */
    @Override
    protected String[] getValidSchemaChildTypes() {
        return VALID_SCHEMA_CHILD_STMTS;
    }

    // ===========================================================================================================================
    // PARSE OBJECTS
    // ===========================================================================================================================

    /**
     * This class provides custom data type parsing for SqlServer-specific data types.
     */
    class SqlServerDataTypeParser extends DataTypeParser {

        /*
         * (non-Javadoc)
         * @see org.modeshape.sequencer.ddl.datatype.DataTypeParser#parseCustomType(org.modeshape.common.text.DdlTokenStream)
         */
        @SuppressWarnings("synthetic-access")
        @Override
        protected DataType parseCustomType( DdlTokenStream tokens ) throws ParsingException {
            DataType dataType = null;
            String typeName = null;

            if (tokens.matches(SqlServerDataTypes.DTYPE_BIGINT)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_BIGINT);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_MONEY)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_MONEY);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_SMALLMONEY)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_SMALLMONEY);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_TINYINT)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_TINYINT);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_DATETIMEOFFSET)) {
                // datetimeoffset [ (fractional seconds precision) ]
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_DATETIMEOFFSET);
                dataType.setName(typeName);
                if (tokens.matches(L_PAREN)) {
                    int precision = (int)parseBracketedLong(tokens, dataType);
                    dataType.setPrecision(precision);
                }
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_DATETIME2)) {
                //  datetime2 [ (fractional seconds precision) ]
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_DATETIME2);
                dataType.setName(typeName);
                if (tokens.matches(L_PAREN)) {
                    int precision = (int)parseBracketedLong(tokens, dataType);
                    dataType.setPrecision(precision);
                }
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_DATETIME)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_DATETIME);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_SMALLDATETIME)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_SMALLDATETIME);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_NTEXT)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_NTEXT);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_TEXT)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_TEXT);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_NVARCHAR)) {
                //  nvarchar [ ( n | max ) ] 
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_NVARCHAR);
                dataType.setName(typeName);
                if (tokens.matches(L_PAREN)) {
                    // FIXME support for (max)
                    long length = parseBracketedLong(tokens, dataType);
                    dataType.setLength(length);
                }
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_VARBINARY)) {
                //  varbinary [ ( n | max) ] 
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_VARBINARY);
                dataType.setName(typeName);
                if (tokens.matches(L_PAREN)) {
                    // FIXME support for (max)
                    long length = parseBracketedLong(tokens, dataType);
                    dataType.setLength(length);
                }
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_BINARY)) {
                //  binary [ ( n ) ] 
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_BINARY);
                dataType.setName(typeName);
                if (tokens.matches(L_PAREN)) {
                    long length = parseBracketedLong(tokens, dataType);
                    dataType.setLength(length);
                }
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_IMAGE)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_IMAGE);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_HIERARCHYID)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_HIERARCHYID);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_SQL_VARIANT)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_SQL_VARIANT);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_TIMESTAMP_SQLSERVER)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_TIMESTAMP_SQLSERVER);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_ROWVERSION)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_ROWVERSION);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_UNIQUEIDENTIFIER)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_UNIQUEIDENTIFIER);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_XML)) {
                // xml ( [ CONTENT | DOCUMENT ] xml_schema_collection )
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_XML);
                dataType.setName(typeName);
                if (tokens.canConsume(L_PAREN)) {
                    tokens.canConsume("CONTENT");
                    tokens.canConsume("DOCUMENT");
                    parseName(tokens);
                    tokens.consume(R_PAREN);
                }
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_GEOGRAPHY)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_GEOGRAPHY);
                dataType.setName(typeName);
            } else if (tokens.matches(SqlServerDataTypes.DTYPE_GEOMETRY)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, true, SqlServerDataTypes.DTYPE_GEOMETRY);
                dataType.setName(typeName);
            }

            if (dataType == null) {
                dataType = super.parseCustomType(tokens);
            }

            return dataType;
        }
        
        
        @Override
        protected DataType parseCharStringType( DdlTokenStream tokens ) throws ParsingException {
            // FIXME support for (max)

            return super.parseCharStringType(tokens);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.modeshape.sequencer.ddl.datatype.DataTypeParser#isCustomDataType(org.modeshape.sequencer.ddl.DdlTokenStream)
         */
        @Override
        protected boolean isCustomDataType( DdlTokenStream tokens ) throws ParsingException {
            for (String[] stmt : sqlServerDataTypeStrings) {
                if (tokens.matches(stmt)) return true;
            }
            return false;
        }
        
        @Override
        protected DataType parseBitStringType(DdlTokenStream tokens) throws ParsingException {
            DataType dataType = null;
            String typeName = null;

            if (tokens.matches(DataTypes.DTYPE_BIT)) {
                typeName = getStatementTypeName(DataTypes.DTYPE_BIT);
                dataType = new DataType(typeName);
                consume(tokens, dataType, false, DataTypes.DTYPE_BIT);
            }

            return dataType;
        }

        @Override
        protected DataType parseDateTimeType( DdlTokenStream tokens ) throws ParsingException {
            DataType dataType = null;
            String typeName = null;

            if (tokens.matches(DataTypes.DTYPE_DATE)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, false, DataTypes.DTYPE_DATE);
                dataType.setName(typeName);
            } else if (tokens.matches(DataTypes.DTYPE_TIME)) {
                dataType = new DataType();
                typeName = consume(tokens, dataType, false, DataTypes.DTYPE_TIME);
                dataType.setName(typeName);

                int precision = 0;
                if (tokens.matches(L_PAREN)) {
                    precision = (int)parseBracketedLong(tokens, dataType);
                }
                dataType.setPrecision(precision);

                canConsume(tokens, dataType, true, "WITH", "TIME", "ZONE");
                
            } else if (tokens.matches(DataTypes.DTYPE_TIMESTAMP)) {
                // SQL Server TIMESTAMP is NOT a DateTimeType
                dataType = new DataType();
                typeName = consume(tokens, dataType, false, DataTypes.DTYPE_TIMESTAMP);
                dataType.setName(typeName);
            }

            return dataType;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.sequencer.ddl.StandardDdlParser#getDataTypeStartWords()
     */
    @Override
    protected List<String> getCustomDataTypeStartWords() {
        return SqlServerDataTypes.CUSTOM_DATATYPE_START_WORDS;
    }
}
