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
package org.modeshape.sequencer.ddl.dialect.mysql;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CONSTRAINT_TYPE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_INSERT_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_PROBLEM;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_TABLE_CONSTRAINT;
import static org.modeshape.sequencer.ddl.dialect.mysql.MySqlDdlLexicon.TYPE_CREATE_INDEX_STATEMENT;

import java.util.List;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.modeshape.common.FixFor;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;

/**
 *
 */
public class MySqlDdlParserTest extends DdlParserTestHelper {

    public static final String DDL_FILE_PATH = "ddl/dialect/mysql/";

    @Before
    public void beforeEach() {
        parser = new MySqlDdlParser();
        setPrintToConsole(true);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }

    @Test
    public void shouldParseCreateTable() {
        printTest("shouldParseCreateTable()");

        String content = "CREATE TABLE MY_TABLE_A (PARTID BLOB (255) NOT NULL DEFAULT (100), "
                         + " -- COLUMN 1 COMMENT with comma \n"
                         + "PARTCOLOR INTEGER NOT NULL) ON COMMIT DELETE ROWS;";
        assertScoreAndParse(content, null, 1);
    }

    @FixFor( "MODE-820" )
    @Test
    public void shouldParseCreateTableWithKilobyteInSize() {
        printTest("shouldParseCreateTableWithKilobyteInSize()");

        String content = "CREATE TABLE MY_TABLE_A (PARTID BLOB (2K) NOT NULL, "
                         + " -- COLUMN 1 COMMENT with comma \nPARTCOLOR CHAR(4M) NOT NULL) ON COMMIT DELETE ROWS;";

        assertScoreAndParse(content, null, 1);
    }

    @Test
    public void shouldParseCreateTableWithMySqlDataTypes() {
        printTest("shouldParseAlterTableAlterColumnDefaultRealNumber()");
        String content = "CREATE TABLE CS_EXT_FILES  (\n" + "     FILE_NAME        VARCHAR(255),\n"
                         + "     FILE_CONTENTS    LONGBLOB,\n" + "     CONFIG_CONTENTS	LONGTEXT);";

        assertScoreAndParse(content, null, 1);
        assertThat(rootNode.getChild(0).getChildCount(), is(3));
        assertThat(rootNode.getChild(0).getName(), is("CS_EXT_FILES"));
    }

    @Ignore
    @Test
    public void shouldParseTestCreate() {
        printTest("shouldParseTestCreate()");
        String content = getFileContent(DDL_FILE_PATH + "mysql_test_create.ddl");

        assertScoreAndParse(content, "mysql_test_create.ddl", 145);

        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        List<AstNode> createTables = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_TABLE_STATEMENT);
        assertThat(createTables.size(), is(57));
        List<AstNode> alterTables = parser.nodeFactory().getChildrenForType(rootNode, TYPE_ALTER_TABLE_STATEMENT);
        assertThat(alterTables.size(), is(31));
        List<AstNode> createViews = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_VIEW_STATEMENT);
        assertThat(createViews.size(), is(3));
        List<AstNode> createIndexes = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_INDEX_STATEMENT);
        assertThat(createIndexes.size(), is(53));
        List<AstNode> insertIntos = parser.nodeFactory().getChildrenForType(rootNode, TYPE_INSERT_STATEMENT);
        assertThat(insertIntos.size(), is(1));
    }

    @Ignore("MySql support not fully implemented yet")
    @Test
    public void shouldParseMySqlTestStatements() {
        printTest("shouldParseMySqlTestStatements()");
        String content = getFileContent(DDL_FILE_PATH + "mysql_test_statements.ddl");

        assertScoreAndParse(content, "mysql_test_statements.ddl", 106);

        printUnknownStatements(parser, rootNode);
        printProblems(parser, rootNode);

        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
    }
    
    @Test
    public void parseCreateTable() {
        printTest("parseCreateTable()");
        
        String content = "CREATE TABLE if not exists book (\n"
                        + " id INTEGER PRIMARY KEY,\n"
                        + " name VARCHAR(255) NULL,\n"
                        + " gid VARCHAR(65) UNIQUE,\n"
                        + " default_value BOOL DEFAULT true);\n";
        
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        List<AstNode> tables = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_TABLE_STATEMENT);
        assertThat(tables.size(), is(1));
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        assertThat(bookTableNode, IsNull.notNullValue(AstNode.class));
        
        List<AstNode> columns = parser.nodeFactory().getChildrenForType(bookTableNode, TYPE_COLUMN_DEFINITION);
        assertThat(columns.size(), is(4));
        
        {
            AstNode idColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "id", TYPE_COLUMN_DEFINITION);
            String idDatatype = (String)idColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            assertThat(idDatatype, is("INTEGER"));
        }
        
        {
            AstNode nameColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "name", TYPE_COLUMN_DEFINITION);
            String nameDatatype = (String)nameColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            Long nameLength = (Long)nameColumnNode.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
            assertThat(nameDatatype, is("VARCHAR"));
            assertThat(nameLength, is(255L));

            String nullable = (String)nameColumnNode.getProperty(StandardDdlLexicon.NULLABLE);
            assertThat(nullable, is("NULL"));
        }
        
        {
            AstNode gidColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "gid", TYPE_COLUMN_DEFINITION);
            String gidDatatype = (String)gidColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            Long gidLength = (Long)gidColumnNode.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
            assertThat(gidDatatype, is("VARCHAR"));
            assertThat(gidLength, is(65L));
        }

        {
            AstNode defaultValueColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "default_value", TYPE_COLUMN_DEFINITION);
            String defaultDatatype = (String)defaultValueColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            assertThat(defaultDatatype, is("BOOL"));
            
            String defaultValue = (String)defaultValueColumnNode.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
            assertThat(defaultValue, is("true"));
        }
        
        List<AstNode> constraints = parser.nodeFactory().getChildrenForType(bookTableNode, TYPE_TABLE_CONSTRAINT);
        assertThat(constraints.size(), is(2));
        
        AstNode pkConstraint = constraints.get(0);
        String constraintType = (String)pkConstraint.getProperty(CONSTRAINT_TYPE);
        assertThat(constraintType, is(DdlConstants.PRIMARY_KEY));
        
        AstNode uniqueConstraint = constraints.get(1);
        String constraintType2 = (String)uniqueConstraint.getProperty(CONSTRAINT_TYPE);
        assertThat(constraintType2, is(DdlConstants.UNIQUE));
    }
    
    @Test
    public void parseCreateTableLike() {
        printTest("parseCreateTableLike()");
        
        String content = "CREATE TABLE book like old_book;\n";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test
    public void parseCreateTableLikeInParens() {
        printTest("parseCreateTableLikeInParens()");
        
        String content = "CREATE TABLE book (like old_book);\n";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test
    public void parseCreateTableOptions() {
        printTest("parseCreateTableOptions()");
        
        String content = "CREATE TABLE book (id integer primary key)\n" +
        		"ENGINE InnoDB,\n" +
        		"CHARACTER SET utf_8,\n" +
        		"COLLATE yes,\n" +
        		"COMMENT 'multiword comment ',\n" +
        		"TABLESPACE a";
        
        assertScoreAndParse(content, null, 1);
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        String engineName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_ENGINE);
        assertThat(engineName, is("InnoDB"));
        
        String charsetName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_CHARACTER_SET);
        assertThat(charsetName, is("utf_8"));
        
        String collationName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_COLLATE);
        assertThat(collationName, is("yes"));
        
        String comment = (String)bookTableNode.getProperty(MySqlDdlLexicon.COMMENT);
        assertThat(comment, is("'multiword comment '"));
        
        String tablespaceName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_TABLESPACE);
        assertThat(tablespaceName, is("a"));
    }
    
    @Test
    public void parseCreateTableUnsupportedOptions() {
        printTest("parseCreateTableUnsupportedOptions()");
        
        String content = "CREATE TABLE book (id integer primary key)\n" +
                        "ENGINE InnoDB,\n" +
                        "CHARACTER SET utf_8,\n" +
                        "COLLATE yes,\n" +
                        "CHECKSUM = 0,\n" +
                        "COMMENT 'multiword comment ',\n" +
                        "DELAY_KEY_WRITE 1,\n" +
                        "TABLESPACE a\n" +
                        "ROW_FORMAT DYNAMIC;";
        
        assertScoreAndParse(content, null, 1);
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        String engineName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_ENGINE);
        assertThat(engineName, is("InnoDB"));
        
        String charsetName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_CHARACTER_SET);
        assertThat(charsetName, is("utf_8"));
        
        String collationName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_COLLATE);
        assertThat(collationName, is("yes"));
        
        String comment = (String)bookTableNode.getProperty(MySqlDdlLexicon.COMMENT);
        assertThat(comment, is("'multiword comment '"));
        
        String tablespaceName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_TABLESPACE);
        assertThat(tablespaceName, is("a"));
    }
    
    @Test
    public void parseCreateTablePartition() {
        printTest("parseCreateTableUnsupportedOptions()");
        
        String content = "CREATE TABLE book (id integer primary key)\n" +
                        "PARTITION BY LINEAR HASH(7);";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test
    public void parseCreateTableUnsupportedOptionsAndPartition() {
        printTest("parseCreateTableUnsupportedOptions()");
        
        String content = "CREATE TABLE book (id integer primary key)\n" +
                        "ENGINE InnoDB,\n" +
                        "CHARACTER SET utf_8,\n" +
                        "COLLATE yes,\n" +
                        "CHECKSUM = 0,\n" +
                        "COMMENT 'multiword comment ',\n" +
                        "DELAY_KEY_WRITE 1,\n" +
                        "TABLESPACE a\n" +
                        "ROW_FORMAT DYNAMIC\n" +
                        "PARTITION BY LINEAR HASH(7);";
        
        assertScoreAndParse(content, null, 1);
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        String engineName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_ENGINE);
        assertThat(engineName, is("InnoDB"));
        
        String charsetName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_CHARACTER_SET);
        assertThat(charsetName, is("utf_8"));
        
        String collationName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_COLLATE);
        assertThat(collationName, is("yes"));
        
        String comment = (String)bookTableNode.getProperty(MySqlDdlLexicon.COMMENT);
        assertThat(comment, is("'multiword comment '"));
        
        String tablespaceName = (String)bookTableNode.getProperty(MySqlDdlLexicon.TABLE_TABLESPACE);
        assertThat(tablespaceName, is("a"));
    }
    
    @Test
    public void parseCreateTableColumnOptions() {
        printTest("parseCreateTableColumnOptions()");
        
        String content = "CREATE TABLE book (id integer primary key,\n" +
        		"name integer auto_increment comment 'column comment' column_format fixed storage disk);";

        assertScoreAndParse(content, null, 1);
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        AstNode columnDefinition = parser.nodeFactory().getChildforNameAndType(bookTableNode, "name", StandardDdlLexicon.TYPE_COLUMN_DEFINITION);
        
        Boolean autoIncrement = (Boolean)columnDefinition.getProperty(MySqlDdlLexicon.COLUMN_AUTO_INCREMENT);
        assertThat(autoIncrement, is(true));
        
        String comment = (String)columnDefinition.getProperty(MySqlDdlLexicon.COMMENT);
        assertThat(comment, is("'column comment'"));
        
        String columnFormat = (String)columnDefinition.getProperty(MySqlDdlLexicon.COLUMN_FORMAT);
        assertThat(columnFormat, is("FIXED"));
        
        String columnStorage = (String)columnDefinition.getProperty(MySqlDdlLexicon.COLUMN_STORAGE);
        assertThat(columnStorage, is("DISK"));
    }
    
    @Test
    public void parseCreateTableColumnInvalidOptions() {
        printTest("parseCreateTableColumnOptions()");
        
        String content = "CREATE TABLE book (id integer primary key,\n" +
                        "name integer auto_increment comment 'column comment' column_format raw storage sth);";

        assertScoreAndParse(content, null, 2);
    }
    
    @Test
    public void parseCreateTableColumnReferences() {
        printTest("parseCreateTableColumnOptions()");
        
        String content = "CREATE TABLE book (id integer primary key,\n" +
                        "author_id integer references author ( id ) MATCH FULL ON DELETE CASCADE ON UPDATE SET NULL);";

        assertScoreAndParse(content, null, 1);
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        List<AstNode> constraints = parser.nodeFactory().getChildrenForType(bookTableNode, TYPE_TABLE_CONSTRAINT);
        assertThat(constraints.size(), is(2));
        
        
        AstNode fkConstraintNode = constraints.get(1);
        String fkConstraintType = (String)fkConstraintNode.getProperty(CONSTRAINT_TYPE);
        assertThat(fkConstraintType, is(DdlConstants.FOREIGN_KEY));
        
        assertThat(fkConstraintNode.getName(), is("FK_1"));
        
        String match = (String)fkConstraintNode.getProperty(MySqlDdlLexicon.MATCH);
        assertThat(match, is("FULL"));
        
        String onDeleteAction = (String)fkConstraintNode.getProperty(StandardDdlLexicon.ON_DELETE_ACTION);
        assertThat(onDeleteAction, is("CASCADE"));
        
        String onUpdateAction = (String)fkConstraintNode.getProperty(StandardDdlLexicon.ON_UPDATE_ACTION);
        assertThat(onUpdateAction, is("SET NULL"));
        
        AstNode tableReference = parser.nodeFactory().getChildforNameAndType(fkConstraintNode, "author", StandardDdlLexicon.TYPE_TABLE_REFERENCE);
        assertThat(tableReference, IsNull.notNullValue());
        
        List<AstNode> columnReferences = parser.nodeFactory().getChildrenForType(fkConstraintNode, StandardDdlLexicon.TYPE_FK_COLUMN_REFERENCE);
        assertThat(columnReferences.size(), is(1));
        assertThat(columnReferences.get(0).getName(), is("id"));
    }

    
    @Test
    public void parseCreateTableWithConstraints() {
        printTest("parseCreateTableWithConstraints()");
        
        String content = "CREATE TABLE book (\n"
                        + " id INTEGER,\n"
                        + " name VARCHAR(255) NULL,\n"
                        + " gid VARCHAR(65),\n"
                        + " default_value BOOL DEFAULT true,\n" 
                        + " PRIMARY KEY (id),\n"
                        + " UNIQUE KEY (gid));\n";
        
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        List<AstNode> tables = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_TABLE_STATEMENT);
        assertThat(tables.size(), is(1));
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        assertThat(bookTableNode, IsNull.notNullValue(AstNode.class));
        
        List<AstNode> columns = parser.nodeFactory().getChildrenForType(bookTableNode, TYPE_COLUMN_DEFINITION);
        assertThat(columns.size(), is(4));
        
        {
            AstNode idColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "id", TYPE_COLUMN_DEFINITION);
            String idDatatype = (String)idColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            assertThat(idDatatype, is("INTEGER"));
        }
        
        {
            AstNode nameColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "name", TYPE_COLUMN_DEFINITION);
            String nameDatatype = (String)nameColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            Long nameLength = (Long)nameColumnNode.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
            assertThat(nameDatatype, is("VARCHAR"));
            assertThat(nameLength, is(255L));

            String nullable = (String)nameColumnNode.getProperty(StandardDdlLexicon.NULLABLE);
            assertThat(nullable, is("NULL"));
        }
        
        {
            AstNode gidColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "gid", TYPE_COLUMN_DEFINITION);
            String gidDatatype = (String)gidColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            Long gidLength = (Long)gidColumnNode.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
            assertThat(gidDatatype, is("VARCHAR"));
            assertThat(gidLength, is(65L));
        }

        {
            AstNode defaultValueColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "default_value", TYPE_COLUMN_DEFINITION);
            String defaultDatatype = (String)defaultValueColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            assertThat(defaultDatatype, is("BOOL"));
            
            String defaultValue = (String)defaultValueColumnNode.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
            assertThat(defaultValue, is("true"));
        }
        
        List<AstNode> constraints = parser.nodeFactory().getChildrenForType(bookTableNode, TYPE_TABLE_CONSTRAINT);
        assertThat(constraints.size(), is(2));
        
        AstNode pkConstraint = constraints.get(0);
        String constraintType = (String)pkConstraint.getProperty(CONSTRAINT_TYPE);
        assertThat(constraintType, is(DdlConstants.PRIMARY_KEY));
        
        AstNode uniqueConstraint = constraints.get(1);
        String constraintType2 = (String)uniqueConstraint.getProperty(CONSTRAINT_TYPE);
        assertThat(constraintType2, is(DdlConstants.UNIQUE));
    }
    
    
    @Test
    public void parseCreateTableWithConstraints2() {
        printTest("parseCreateTableWithConstraints2()");
        
        String content = "CREATE TABLE book (\n"
                        + " id INTEGER,\n"
                        + " name VARCHAR(255) NULL,\n"
                        + " gid VARCHAR(65),\n"
                        + " default_value BOOL DEFAULT true,\n" 
                        + " CONSTRAINT PRIMARY KEY (id),\n"
                        + " CONSTRAINT UNIQUE unique_gid (gid),\n"
                        + " CHECK (id > 7));\n";
        
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        List<AstNode> tables = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_TABLE_STATEMENT);
        assertThat(tables.size(), is(1));
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        assertThat(bookTableNode, IsNull.notNullValue(AstNode.class));
        
        List<AstNode> columns = parser.nodeFactory().getChildrenForType(bookTableNode, TYPE_COLUMN_DEFINITION);
        assertThat(columns.size(), is(4));
        
        {
            AstNode idColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "id", TYPE_COLUMN_DEFINITION);
            String idDatatype = (String)idColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            assertThat(idDatatype, is("INTEGER"));
        }
        
        {
            AstNode nameColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "name", TYPE_COLUMN_DEFINITION);
            String nameDatatype = (String)nameColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            Long nameLength = (Long)nameColumnNode.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
            assertThat(nameDatatype, is("VARCHAR"));
            assertThat(nameLength, is(255L));

            String nullable = (String)nameColumnNode.getProperty(StandardDdlLexicon.NULLABLE);
            assertThat(nullable, is("NULL"));
        }
        
        {
            AstNode gidColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "gid", TYPE_COLUMN_DEFINITION);
            String gidDatatype = (String)gidColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            Long gidLength = (Long)gidColumnNode.getProperty(StandardDdlLexicon.DATATYPE_LENGTH);
            assertThat(gidDatatype, is("VARCHAR"));
            assertThat(gidLength, is(65L));
        }

        {
            AstNode defaultValueColumnNode = parser.nodeFactory().getChildforNameAndType(bookTableNode, "default_value", TYPE_COLUMN_DEFINITION);
            String defaultDatatype = (String)defaultValueColumnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
            assertThat(defaultDatatype, is("BOOL"));
            
            String defaultValue = (String)defaultValueColumnNode.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
            assertThat(defaultValue, is("true"));
        }
        
        List<AstNode> constraints = parser.nodeFactory().getChildrenForType(bookTableNode, TYPE_TABLE_CONSTRAINT);
        assertThat(constraints.size(), is(3));
        
        AstNode pkConstraint = constraints.get(0);
        String constraintType = (String)pkConstraint.getProperty(CONSTRAINT_TYPE);
        assertThat(constraintType, is(DdlConstants.PRIMARY_KEY));
        
        AstNode uniqueConstraint = constraints.get(1);
        String constraintType2 = (String)uniqueConstraint.getProperty(CONSTRAINT_TYPE);
        assertThat(constraintType2, is(DdlConstants.UNIQUE));
    }
    
    @Test
    public void parseCreateTableWithIndex() {
        printTest("parseCreateTableWithIndex()");
        
        String content = "CREATE TABLE book (\n"
                        + " id INTEGER,\n"
                        + " name VARCHAR(255) NULL,\n"
                        + " gid VARCHAR(65),\n"
                        + " default_value BOOL DEFAULT true,\n" 
                        + " FULLTEXT  KEY fin (id) KEY_BLOCK_SIZE = 7,\n"
                        + " INDEX USING HASH (gid) ,\n"
                        + " CHECK (id > 7));\n";
        
        assertScoreAndParse(content, null, 3);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        List<AstNode> tables = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_TABLE_STATEMENT);
        assertThat(tables.size(), is(1));
        
        List<AstNode> indexes = parser.nodeFactory().getChildrenForType(rootNode, MySqlDdlLexicon.TYPE_CREATE_INDEX_STATEMENT);
        assertThat(indexes.size(), is(2));
        
        AstNode fullTextIndex = indexes.get(0);
        assertProperty(fullTextIndex, MySqlDdlLexicon.TYPE, "FULLTEXT");
        assertThat(fullTextIndex.getName(), is("fin"));
        assertProperty(fullTextIndex, MySqlDdlLexicon.KEY_BLOCK_SIZE, "7");
        
        AstNode index2 = indexes.get(1);
        assertThat(index2.getName(), is("IDX"));
    }
    
    @Test
    public void parseCreateIndex() {
        printTest("parseCreateIndex()");
        
        String content = "CREATE INDEX book_idx \n"
                + " USING BTREE \n"
                + " ON book (id, name)\n"
                + " COMMENT 'comment c';";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode indexNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book_idx", TYPE_CREATE_INDEX_STATEMENT);
        assertThat(indexNode, IsNull.notNullValue());
        
        assertProperty(indexNode, MySqlDdlLexicon.TABLE_NAME, "book");
        assertProperty(indexNode, MySqlDdlLexicon.COMMENT, "'comment c'");
        assertProperty(indexNode, MySqlDdlLexicon.INDEX_TYPE, "BTREE");
        
        List<AstNode> columns = parser.nodeFactory().getChildrenForType(indexNode, StandardDdlLexicon.TYPE_COLUMN_REFERENCE);
        assertThat(columns.size(), is(2));
        
        assertThat(columns.get(0).getName(), is("id"));
        assertThat(columns.get(1).getName(), is("name"));
    }
    
    @Test
    public void parseCreateOnlineUniqueIndex() {
        printTest("parseCreateOnlineUniqueIndex()");
        
        String content = "CREATE ONLINE UNIQUE INDEX book_idx \n"
                + " USING BTREE \n"
                + " ON book (id, name)\n"
                + " COMMENT 'comment c';";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode indexNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book_idx", TYPE_CREATE_INDEX_STATEMENT);
        assertThat(indexNode, IsNull.notNullValue());
        
        assertProperty(indexNode, MySqlDdlLexicon.TABLE_NAME, "book");
        assertProperty(indexNode, MySqlDdlLexicon.COMMENT, "'comment c'");
        assertProperty(indexNode, MySqlDdlLexicon.INDEX_TYPE, "BTREE");
        assertProperty(indexNode, MySqlDdlLexicon.TYPE, "UNIQUE");
        assertProperty(indexNode, MySqlDdlLexicon.ONLINE, "ONLINE");
        
        List<AstNode> columns = parser.nodeFactory().getChildrenForType(indexNode, StandardDdlLexicon.TYPE_COLUMN_REFERENCE);
        assertThat(columns.size(), is(2));
        
        assertThat(columns.get(0).getName(), is("id"));
        assertThat(columns.get(1).getName(), is("name"));
    }
    
    @Test
    public void parseCreateSpatialIndex() {
        printTest("parseCreateSpatialIndex()");
        
        String content = "CREATE SPATIAL INDEX book_idx \n"
                + " ON book (id, name);";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode indexNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book_idx", TYPE_CREATE_INDEX_STATEMENT);
        assertThat(indexNode, IsNull.notNullValue());
        
        assertProperty(indexNode, MySqlDdlLexicon.TABLE_NAME, "book");
        assertProperty(indexNode, MySqlDdlLexicon.TYPE, "SPATIAL");
        
        List<AstNode> columns = parser.nodeFactory().getChildrenForType(indexNode, StandardDdlLexicon.TYPE_COLUMN_REFERENCE);
        assertThat(columns.size(), is(2));
        
        assertThat(columns.get(0).getName(), is("id"));
        assertThat(columns.get(1).getName(), is("name"));
    }
    
    @Test
    public void parseCreateView() {
        printTest("parseCreateView()");
        
        String content = "CREATE VIEW book_view \n"
                + " AS select * from book;";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode view = parser.nodeFactory().getChildforNameAndType(rootNode, "book_view", TYPE_CREATE_VIEW_STATEMENT);
        assertThat(view, IsNull.notNullValue());
        
        assertProperty(view, MySqlDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION, "select * from book");
    }
    
    @Test
    public void parseCreateView2() {
        printTest("parseCreateView()");
        
        String content = "CREATE VIEW book_view \n"
                + " AS select * from book" 
                + " WITH CHECK OPTION;";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode view = parser.nodeFactory().getChildforNameAndType(rootNode, "book_view", TYPE_CREATE_VIEW_STATEMENT);
        assertThat(view, IsNull.notNullValue());
        
        assertProperty(view, MySqlDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION, "select * from book");
    }
    
    @Test
    public void parseCreateView3() {
        printTest("parseCreateView()");
        
        String content = "CREATE DEFINER = CURRENT_USER SQL SECURITY DEFINER VIEW book_view \n"
                + " AS select * from book" 
                + " WITH CHECK OPTION;";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode view = parser.nodeFactory().getChildforNameAndType(rootNode, "book_view", TYPE_CREATE_VIEW_STATEMENT);
        assertThat(view, IsNull.notNullValue());
        
        assertProperty(view, MySqlDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION, "select * from book");
    }
    
    @Test
    public void parseCreateView4() {
        printTest("parseCreateView4()");
        
        String content = "CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW book_view \n"
                + " AS select * from book" 
                + " WITH CHECK OPTION;";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode view = parser.nodeFactory().getChildforNameAndType(rootNode, "book_view", TYPE_CREATE_VIEW_STATEMENT);
        assertThat(view, IsNull.notNullValue());
        
        assertProperty(view, MySqlDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION, "select * from book");
    }
    
    
    @Test
    public void parseCreateView5() {
        printTest("parseCreateView5()");
        
        String content = "CREATE SQL SECURITY DEFINER VIEW book_view \n"
                + " AS select * from book" 
                + " WITH CHECK OPTION;";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode view = parser.nodeFactory().getChildforNameAndType(rootNode, "book_view", TYPE_CREATE_VIEW_STATEMENT);
        assertThat(view, IsNull.notNullValue());
        
        assertProperty(view, MySqlDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION, "select * from book");
    }
    
    @Test
    public void parseCreateOrReplaceView4() {
        printTest("parseCreateOrReplaceView4()");
        
        String content = "CREATE OR REPLACE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW book_view \n"
                + " AS select * from book" 
                + " WITH CHECK OPTION;";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode view = parser.nodeFactory().getChildforNameAndType(rootNode, "book_view", TYPE_CREATE_VIEW_STATEMENT);
        assertThat(view, IsNull.notNullValue());
        
        assertProperty(view, MySqlDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION, "select * from book");
    }
    
    
    @Test
    public void parseCreateOrReplaceView5() {
        printTest("parseCreateOrReplaceView5()");
        
        String content = "CREATE OR REPLACE SQL SECURITY DEFINER VIEW book_view \n"
                + " AS select * from book" 
                + " WITH CHECK OPTION;";
     
        assertScoreAndParse(content, null, 1);
        
        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        
        AstNode view = parser.nodeFactory().getChildforNameAndType(rootNode, "book_view", TYPE_CREATE_VIEW_STATEMENT);
        assertThat(view, IsNull.notNullValue());
        
        assertProperty(view, MySqlDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION, "select * from book");
    }
    
    @Test    
    public void parseAlterTable1() {
        printTest("parseAlterTable1()");
        
        String content = "ALTER TABLE book \n"
                + " ADD COLUMN name varchar(15) AFTER id;";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTableAddColumns() {
        printTest("parseAlterTable1()");
        
        String content = "ALTER TABLE book \n"
                + " ADD COLUMN (name varchar(15), id integer);";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterAddPrimaryKey() {
        printTest("parseAlterTable1()");
        
        String content = "ALTER TABLE book \n"
                + " ADD CONSTRAINT pk PRIMARY KEY (id)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterAddPrimaryKey2() {
        printTest("parseAlterTable1()");
        
        String content = "ALTER TABLE book \n"
                + " ADD CONSTRAINT PRIMARY KEY (id)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterAddPrimaryKey3() {
        printTest("parseAlterTable1()");
        
        String content = "ALTER TABLE book \n"
                + " ADD PRIMARY KEY (id)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterUniqueKey() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " ADD UNIQUE KEY (id)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterUniqueIndex() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " ADD UNIQUE INDEX (id)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterConstraintUniqueKey() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " ADD CONSTRAINT uk UNIQUE KEY (id)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterConstraintUniqueKey2() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " ADD CONSTRAINT UNIQUE KEY (id)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTableAlterColumn() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " ALTER COLUMN name set default 'Ala'";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTableChangeColumn() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " CHANGE COLUMN name new_name varchar(15)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTableModifyColumn() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " MODIFY COLUMN new_name integer FIRST";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseTableAlterDropColumn() {
        printTest("parseTableAlterDropColumn()");
        
        String content = "ALTER TABLE book \n"
                + " DROP COLUMN new_name";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTableDropPrimaryKey() {
        printTest("parseTableAlterDropColumn()");
        
        String content = "ALTER TABLE book \n"
                + " DROP PRIMARY KEY";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTable() {
        printTest("parseAlterTable()");
        
        String content = "ALTER TABLE book \n"
                + " DROP PRIMARY KEY,"
                + " ADD COLUMN name varchar(12),"
                + " CHANGE COLUMN id id2 integer;";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test
    public void parseOracleAuthorization() {
        printTest("parseOracleAuthorization()");
        
        String content = "CREATE SCHEMA AUTHORIZATION oe "
                + " CREATE TABLE new_product "
                + " (color VARCHAR2(10)  PRIMARY KEY, quantity NUMBER) "
                + " CREATE VIEW new_product_view " 
                + " AS SELECT color, quantity FROM new_product WHERE color = 'RED' "
                + " GRANT select ON new_product_view TO hr; ";
        
        assertScoreAndParse(content, null, 2);
    }
    
    @Test
    public void parseAlterTableOption() {
        printTest("parseAlterTableOption()");
        
        String content = "ALTER TABLE book ENGINE = InnoDB;";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test
    public void parseAlterTableOption2() {
        printTest("parseAlterTableOption()");
        
        String content = "ALTER TABLE book ENGINE = InnoDB, KEY_BLOCK_SIZE = 7;";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTableChangeColumnTableOption() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " CHANGE COLUMN name new_name varchar(15),"
                + " ENGINE = InnoDB";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseAlterTableChangeColumnTableOptionRename() {
        printTest("parseAlterUniqueKey()");
        
        String content = "ALTER TABLE book \n"
                + " CHANGE COLUMN name new_name varchar(15),"
                + " ENGINE = InnoDB,"
                + " RENAME TO new_book";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test    
    public void parseFile1() {
        printTest("parseFile1()");
        String content = getFileContent(DDL_FILE_PATH + "file1.sql");

        assertScoreAndParse(content, "file1.sql", 7);

        List<AstNode> problems = parser.nodeFactory().getChildrenForType(rootNode, TYPE_PROBLEM);
        assertThat(problems.size(), is(0));
        List<AstNode> createTables = parser.nodeFactory().getChildrenForType(rootNode, TYPE_CREATE_TABLE_STATEMENT);
        assertThat(createTables.size(), is(1));
        
        AstNode skillsTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "skills", TYPE_CREATE_TABLE_STATEMENT);
        
        List<AstNode> columns = parser.nodeFactory().getChildrenForType(skillsTableNode, TYPE_COLUMN_DEFINITION);
        assertThat(columns.size(), is(5));
        
        {
            AstNode columnNode0 = columns.get(0);
            assertThat(columnNode0.getName(), is("skills_id"));
        }
        
        {
            AstNode columnNode1 = columns.get(1);
            assertThat(columnNode1.getName(), is("skills_category"));
        }
    }
    
    @Test
    public void parseCreateTableOnUpdateCurrentTimestamp() {
        printTest("parseCreateTableOnUpdateCurrentTimestamp()");
        
        String content = "CREATE TABLE book \n"
                + "( id integer primary key,\n"
                + " ts_create timestamp on update current_timestamp)";
        
        assertScoreAndParse(content, null, 1);
    }
    
    
    @Test
    public void parseCreateTableQuotedString() {
        printTest("parseCreateTableQuotedString()");
        
        String content = "CREATE TABLE book \n"
                + "( id integer primary key,\n"
                + " UNIQUE INDEX `unique combo idx` (id))";
        
        assertScoreAndParse(content, null, 1);
    }
    
    @Test
    public void parseCreateTableDoubleQuoteInComment() {
        printTest("parseCreateTableDoubleQuoteInComment()");
        
        String content = "CREATE TABLE book (id integer primary key)\n" +
                        "COMMENT 'multiword ''comment '\n";
        
        assertScoreAndParse(content, null, 1);
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "book", TYPE_CREATE_TABLE_STATEMENT);
        
        String comment = (String)bookTableNode.getProperty(MySqlDdlLexicon.COMMENT);
        assertThat(comment, is("'multiword ''comment '"));
    }
    
    @Test
    public void parseCreateTableForeignKey() {
        printTest("parseCreateForeignKey()");
        
        String content = "CREATE TABLE `pelamar` (" +
                    "`NISN` varchar(12) NOT NULL," +
                    "`BER_NISN` varchar(12) DEFAULT NULL," +
                    "`PASSWORD` varchar(25) NOT NULL," +
                    "`EMAIL` varchar(50) NOT NULL," +
                    "`NAMA` varchar(50) NOT NULL," +
                    "`ASAL_SEKOLAH` varchar(50) DEFAULT NULL," +
                    "`TEMPAT_LAHIR` varchar(30) DEFAULT NULL," +
                    "`TANGGAL_LAHIR` date DEFAULT NULL," +
                    "`CP_PELAMAR` varchar(20) DEFAULT NULL," +
                    "`NAMA_PEKERJA` varchar(50) NOT NULL," +
                    "`NO_PEKERJA` varchar(20) NOT NULL," +
                    "`UNIT_FUNGSI` varchar(50) DEFAULT NULL," +
                    "`ALAMAT_FUNGSI` mediumtext," +
                    "`EMAIL_PEKERJA` varchar(50) DEFAULT NULL," +
                    "`CP_PEKERJA` varchar(20) DEFAULT NULL," +
                    "`TANGGAL_REGISTRASI` varchar(200) DEFAULT NULL," +
                    "`KULIAH_DI` varchar(75) DEFAULT NULL," +
                    "PRIMARY KEY (`NISN`)," +
                    "KEY `FK_PELAMAR_BERKAS2_BERKAS_P` (`BER_NISN`)," +
                    "CONSTRAINT `pelamar_ibfk_1` FOREIGN KEY (`BER_NISN`) REFERENCES `berkas_pelamar` (`NISN`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
        
        assertScoreAndParse(content, null, 2);
        
        AstNode bookTableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "pelamar", TYPE_CREATE_TABLE_STATEMENT);
        
        List<AstNode> constraints = parser.nodeFactory().getChildrenForType(bookTableNode, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT);
        
        assertThat(constraints.size(), is(2));
        
        AstNode c2 = constraints.get(1);
        assertProperty(c2, StandardDdlLexicon.CONSTRAINT_TYPE, "FOREIGN KEY");
        
        List<AstNode> tableReferences = parser.nodeFactory().getChildrenForType(c2, StandardDdlLexicon.TYPE_TABLE_REFERENCE);
        AstNode tableReference = tableReferences.get(0);
        
        assertThat(tableReference.getName(), is("berkas_pelamar"));
    }
    


    @Test
    public void parseCreateTableForeignKey2() {
        printTest("parseCreateTableForeignKey2()");
        
        String content = "    CREATE TABLE `connections` ("+
            "`id` int(11) unsigned NOT NULL AUTO_INCREMENT,"+
            "`user_id` int(11) unsigned NOT NULL,"+
            "`username` varchar(255) DEFAULT NULL,"+
            "`network` int(11) DEFAULT NULL,"+
            "`updated` int(11) DEFAULT NULL,"+
            "`created` int(11) DEFAULT NULL,"+
            "PRIMARY KEY (`id`),"+
            "UNIQUE KEY `combo_index` (`user_id`,`network`),"+
            "CONSTRAINT `connections_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)"+
          ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
        
        assertScoreAndParse(content, null, 1);
        
        AstNode tableNode = parser.nodeFactory().getChildforNameAndType(rootNode, "connections", TYPE_CREATE_TABLE_STATEMENT);
        
        List<AstNode> constraints = parser.nodeFactory().getChildrenForType(tableNode, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT);
        
        assertThat(constraints.size(), is(3));
        
        AstNode c2 = constraints.get(2);
        assertProperty(c2, StandardDdlLexicon.CONSTRAINT_TYPE, "FOREIGN KEY");
        
        List<AstNode> tableReferences = parser.nodeFactory().getChildrenForType(c2, StandardDdlLexicon.TYPE_TABLE_REFERENCE);
        AstNode tableReference = tableReferences.get(0);
        
        assertThat(tableReference.getName(), is("users"));
    }
    
    @Test
    public void parseAlterTableAddForeignKey() {
        String content = "ALTER TABLE purchase ADD CONSTRAINT client_order FOREIGN KEY client_order (client_id) REFERENCES client (id);";
        
        assertScoreAndParse(content, null, 1);
    }
}
