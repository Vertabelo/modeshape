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

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CONSTRAINT_TYPE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DATATYPE_NAME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_VALUE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NULLABLE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.COMPOUND_SORTKEY;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.DISTKEY;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.DISTSTYLE;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.IDENTITY;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.IDENTITY_SEED;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.IDENTITY_STEP;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.INTERLEAVED_SORTKEY;

/**
 * Źródła testów:
 *  https://docs.aws.amazon.com/redshift/latest/dg/r_CREATE_TABLE_examples.html
 */
public class SnowflakeDdlParserTest extends DdlParserTestHelper {
    private static final String SPACE = DdlConstants.SPACE;

    public static final String DDL_FILE_PATH = "ddl/dialect/snowflake/";

    @Before
    public void beforeEach() {
        parser = new SnowflakeDdlParser();
        setPrintToConsole(false);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }

    @Override
    protected String getFileContent(String filePath) {
        return super.getFileContent(DDL_FILE_PATH + filePath);
    }

    @Test
    public void shouldParseCreateTable_1() {
        printTest("Create a Table");
        final String filename = "create_collate_table.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(4, tableNode.getChildCount());
    }


    @Test
    public void shouldParseCreateTable_2() {
        printTest("Create a Table");
        final String filename = "create_additional_props_table.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(2, tableNode.getChildCount());
        assertEquals(true, tableNode.getProperty(SnowflakeDdlLexicon.CHANGE_TRACKING));
        assertEquals(true, tableNode.getProperty(SnowflakeDdlLexicon.TRANSIENT_TABLE));
        assertEquals("ON_ERROR = CONTINUE", tableNode.getProperty(SnowflakeDdlLexicon.STAGE_COPY_OPTIONS));
        assertEquals("10", tableNode.getProperty(SnowflakeDdlLexicon.DATA_RETENTION));
        assertEquals("'en'", tableNode.getProperty(SnowflakeDdlLexicon.DEFAULT_DDL_COLLATION));
        assertEquals("FORMAT_NAME = 'test'", tableNode.getProperty(SnowflakeDdlLexicon.STAGE_FILE_FORMAT));
    }

    @Test
    public void shouldParseCreateExternalTable_1() {
        printTest("Create external Table");
        final String filename = "create_external_table.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(0, tableNode.getChildCount());
        assertEquals("@namespace.mystage/daily/", tableNode.getProperty(SnowflakeDdlLexicon.LOCATION));
        assertEquals(true, tableNode.getProperty(SnowflakeDdlLexicon.AUTO_REFRESH));
        assertEquals("TYPE = PARQUET", tableNode.getProperty(SnowflakeDdlLexicon.FILE_FORMAT));
        assertEquals("'.*sales.*[.]parquet'", tableNode.getProperty(SnowflakeDdlLexicon.PATTERN));
    }


    @Test
    public void shouldParseCreateExternalTable_2() {
        printTest("Create external table 2");
        final String filename = "create_external_table_2.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(3, tableNode.getChildCount());
        assertEquals(true, tableNode.getProperty(SnowflakeDdlLexicon.AUTO_REFRESH));
        assertEquals("TYPE = PARQUET", tableNode.getProperty(SnowflakeDdlLexicon.FILE_FORMAT));
        assertEquals("@exttable_part_stage/logs/", tableNode.getProperty(SnowflakeDdlLexicon.LOCATION));
        assertEquals("date_part", tableNode.getProperty(SnowflakeDdlLexicon.PARTITION_BY));
    }

    @Test
    public void shouldParseCreateExternalTable_3() {
        printTest("Create external table 3");
        final String filename = "create_external_table_3.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);

        AstNode firstCol = tableNode.getChildren().get(0);
        assertEquals("value : \"b\".\"c\" :: varchar", firstCol.getProperty(SnowflakeDdlLexicon.AS_CLAUSE));
        assertEquals("constr", firstCol.getProperty(SnowflakeDdlLexicon.CONSTRAINT_NAME));
        AstNode secondCol = tableNode.getChildren().get(2);
        assertEquals("value : c1 :: varchar", secondCol.getProperty(SnowflakeDdlLexicon.AS_CLAUSE));
        AstNode thirdCol = tableNode.getChildren().get(3);
        assertEquals("lalala", thirdCol.getProperty(SnowflakeDdlLexicon.CONSTRAINT_NAME));
    }

    @Test
    public void shouldParseCreateView_1() {
        printTest("Create a View");
        final String filename = "create_view.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode view = rootNode.getChildren().get(0);
        assertEquals(3, view.getChildCount());
        assertEquals("'test'", view.getProperty(SnowflakeDdlLexicon.COMMENT));
        assertEquals(true, view.getProperty(SnowflakeDdlLexicon.SECURE));
        assertEquals(true, view.getProperty(SnowflakeDdlLexicon.RECURSIVE));
    }

    @Test
    public void shouldParseAlterTable1() {
        printTest("Alter table test");
        final String filename = "alter_table_1.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 2);
        AstNode createTable = rootNode.getChildren().get(0);
        AstNode alterTable = rootNode.getChildren().get(1);
        assertMixinType(createTable, TYPE_CREATE_TABLE_STATEMENT);
        assertMixinType(alterTable, TYPE_ALTER_TABLE_STATEMENT);
    }

    @Test
    public void shouldParseAlterTable2() {
        printTest("Alter table test");
        final String filename = "alter_table_2.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);
        AstNode createTable1 = rootNode.getChildren().get(0);
        AstNode createTable2 = rootNode.getChildren().get(1);
        AstNode alterTable = rootNode.getChildren().get(2);
        assertMixinType(createTable1, TYPE_CREATE_TABLE_STATEMENT);
        assertMixinType(createTable2, TYPE_CREATE_TABLE_STATEMENT);
        assertMixinType(alterTable, TYPE_ALTER_TABLE_STATEMENT);
    }

    @Test
    public void shouldParseAlterTable3() {
        printTest("Alter table test");
        final String filename = "alter_table_3.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 6);
        AstNode createTable = rootNode.getChildren().get(0);
        AstNode alterTable1 = rootNode.getChildren().get(1);
        AstNode alterTable2 = rootNode.getChildren().get(2);
        AstNode alterTable3 = rootNode.getChildren().get(3);
        AstNode alterTable4 = rootNode.getChildren().get(4);
        AstNode alterTable5 = rootNode.getChildren().get(5);
        assertMixinType(createTable, TYPE_CREATE_TABLE_STATEMENT);
        assertMixinType(alterTable1, TYPE_ALTER_TABLE_STATEMENT);
        assertMixinType(alterTable2, TYPE_ALTER_TABLE_STATEMENT);
        assertMixinType(alterTable3, TYPE_ALTER_TABLE_STATEMENT);
        assertMixinType(alterTable4, TYPE_ALTER_TABLE_STATEMENT);
        assertMixinType(alterTable5, TYPE_ALTER_TABLE_STATEMENT);
    }

    @Test
    public void shouldParseAlterTable4() {
        printTest("Alter table test");
        final String filename = "alter_table_4.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);
        AstNode createTable = rootNode.getChildren().get(0);
        AstNode alterTable = rootNode.getChildren().get(1);
        assertMixinType(createTable, TYPE_CREATE_TABLE_STATEMENT);
        assertMixinType(alterTable, TYPE_ALTER_TABLE_STATEMENT);
    }

    @Test
    public void shouldParseCreateTable() {
        printTest("Alter create table");
        final String filename = "create_table_EDWM_3865.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode createTable = rootNode.getChildren().get(0);
        assertMixinType(createTable, TYPE_CREATE_TABLE_STATEMENT);
        AstNode id = createTable.getChildren().get(0);
        AstNode number = createTable.getChildren().get(1);
        assertProperty(number, DATATYPE_NAME, "number");
        AstNode string = createTable.getChildren().get(2);
        assertProperty(string, DATATYPE_NAME, "string");
        AstNode text = createTable.getChildren().get(3);
        assertProperty(text, DATATYPE_NAME, "text");
    }

}
