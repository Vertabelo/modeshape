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

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DEFAULT_VALUE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NULLABLE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.COMMENT;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.COMPOUND_SORTKEY;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.DISTKEY;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.DISTSTYLE;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.IDENTITY;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.IDENTITY_SEED;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.IDENTITY_STEP;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.INTERLEAVED_SORTKEY;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.TARGET_OBJECT_TYPE;
import static org.modeshape.sequencer.ddl.dialect.amazon.RedshiftDdlLexicon.TYPE_COMMENT_ON_STATEMENT;

/**
 * Źródła testów:
 *  https://docs.aws.amazon.com/redshift/latest/dg/r_CREATE_TABLE_examples.html
 */
public class RedshiftDdlParserTest extends DdlParserTestHelper {
    private static final String SPACE = DdlConstants.SPACE;

    public static final String DDL_FILE_PATH = "ddl/dialect/amazon/";

    @Before
    public void beforeEach() {
        parser = new RedshiftDdlParser();
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
    public void shouldParseRedshiftCreate_1() {
        printTest("Create a Table with a Distribution Key, a Compound Sort Key, and Compression");
        final String filename = "create_table_dist_compount_sort_compresion.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(15, tableNode.getChildCount());
        assertEquals(tableNode.getProperty(DISTKEY), "listid");
        assertEquals(tableNode.getProperty(COMPOUND_SORTKEY), "listid , sellerid");
    }

    @Test
    public void shouldParseRedshiftCreate_2() {
        printTest("Create a Table Using an Interleaved Sort Key");
        final String filename = "create_table_interleaved_sort_key.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(8, tableNode.getChildCount());
        assertEquals(tableNode.getProperty(DISTSTYLE), "all");
        assertEquals(tableNode.getProperty(INTERLEAVED_SORTKEY), "c_custkey , c_city , c_mktsegment");
    }

    @Test
    public void shouldParseRedshiftCreate_3() {
        printTest("Create a Table Using IF NOT EXISTS");
        final String filename = "create_table_not_exists.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(3, tableNode.getChildCount());
    }

    @Test
    public void shouldParseRedshiftCreate_4() {
        printTest("Create a Table with ALL Distribution");
        final String filename = "create_table_all_distribution.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(6, tableNode.getChildCount());
        assertEquals(tableNode.getProperty(DISTSTYLE), "all");
    }

    @Test
    public void shouldParseRedshiftCreate_6() {
        printTest("Create a Temporary Table That Is LIKE Another Table");
        final String filename = "create_table_like_table.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 2);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(1, tableNode.getChildCount());
    }

    @Test
    public void shouldParseRedshiftCreate_7() {
        printTest("Create a Table with an IDENTITY Column");
        final String filename = "create_table_identity_column.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(6, tableNode.getChildCount());
        AstNode identityColumn = tableNode.getFirstChild();
        assertEquals(true,identityColumn.getProperty(IDENTITY));
        assertEquals(1,identityColumn.getProperty(IDENTITY_STEP));
        assertEquals(0,identityColumn.getProperty(IDENTITY_SEED));
    }

    @Test
    public void shouldParseRedshiftCreate_8() {
        printTest("Create a Table with DEFAULT Column Values");
        final String filename = "create_table_default_column.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(5, tableNode.getChildCount());
        AstNode identityColumn = tableNode.getFirstChild();
        assertEquals("0", tableNode.getChild(0).getProperty(DEFAULT_VALUE));
        assertEquals("NOT NULL", tableNode.getChild(0).getProperty(NULLABLE));
        assertEquals("'Special'", tableNode.getChild(1).getProperty(DEFAULT_VALUE));
        assertEquals("'Other'", tableNode.getChild(2).getProperty(DEFAULT_VALUE));
    }

    @Test
    public void shouldParseRedshiftCreate_9() {
        printTest("Create a Table with Many values");
        final String filename = "create_table_many_options.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(11, tableNode.getPropertyNames().size());
        assertEquals("KEY", tableNode.getProperty(DISTSTYLE));
        assertEquals("b , c", tableNode.getProperty(COMPOUND_SORTKEY));
    }

    @Test
    public void shouldParseRedshiftCreate_10() {
        printTest("Create a Table with Alter Reference");
        final String filename = "alter_table_add_reference.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);

    }

    @Test
    public void shouldParseRedshiftCreateView_1() {
        printTest("Create a view EVENT");
        final String filename = "create_view.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);
        AstNode viewNode = rootNode.getChildren().get(0);
        assertEquals(0, viewNode.getChildCount());
        assertEquals("myevent", viewNode.getName());
        assertEquals("select eventname from event where eventname = 'LeAnn Rimes'", viewNode.getProperty(CREATE_VIEW_QUERY_EXPRESSION));
    }

    @Test
    public void shouldParseRedshiftCreateTableWithComment() {
        printTest("Create table with comment");
        final String filename = "comment_on_objects.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 5);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue("Should be a table", tableNode.hasMixin(TYPE_CREATE_TABLE_STATEMENT));
        assertEquals("client", tableNode.getName());
        AstNode tableComment = rootNode.getChildren().get(2);
        assertTrue("Should be a comment on table", tableComment.hasMixin(TYPE_COMMENT_ON_STATEMENT));
        assertEquals("'Test client'", tableComment.getProperty(COMMENT));
        assertEquals("TABLE", tableComment.getProperty(TARGET_OBJECT_TYPE));
        AstNode columnComment = rootNode.getChildren().get(3);
        assertTrue("Comment on column", columnComment.hasMixin(TYPE_COMMENT_ON_STATEMENT));
        assertEquals("'Test client.full_name'", columnComment.getProperty(COMMENT));
        assertEquals("COLUMN", columnComment.getProperty(TARGET_OBJECT_TYPE));
        AstNode constraintComment = rootNode.getChildren().get(4);
        assertTrue("Comment on constraint", constraintComment.hasMixin(TYPE_COMMENT_ON_STATEMENT));
        assertEquals("'Test client_email constraint'", constraintComment.getProperty(COMMENT));
        assertEquals("CONSTRAINT", constraintComment.getProperty(TARGET_OBJECT_TYPE));
    }
}
