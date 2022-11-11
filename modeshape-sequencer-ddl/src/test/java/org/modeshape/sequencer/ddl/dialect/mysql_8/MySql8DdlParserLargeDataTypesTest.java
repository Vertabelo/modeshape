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
package org.modeshape.sequencer.ddl.dialect.mysql_8;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon;
import org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;

/**
 * Testy typów danych binarnych dodanych dla MySQL 8.x
 *
 * @author Marek Berkan
 */
public class MySql8DdlParserLargeDataTypesTest extends DdlParserTestHelper {

    @Before
    public void beforeEach() {
        parser = new MySql8DdlParser();
        setPrintToConsole(false);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }

    @Test
    public void shouldParseBlobWithoutLength() {
        String content = "CREATE TABLE test (test BLOB);";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("BLOB", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
        assertNull(columnNode.getProperty(MySql8DdlLexicon.DATATYPE_LENGTH));
    }

    @Test
    public void shouldParseBlobWithLength() {
        String content = "CREATE TABLE test (test BLOB(10);";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("BLOB", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
        assertEquals(10L, columnNode.getProperty(MySql8DdlLexicon.DATATYPE_LENGTH));
    }

    @Test
    public void shouldParseTextWithoutLength() {
        String content = "CREATE TABLE test (test TEXT);";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("TEXT", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
        assertNull(columnNode.getProperty(MySql8DdlLexicon.DATATYPE_LENGTH));
    }

    @Test
    public void shouldParseTextWithLength() {
        String content = "CREATE TABLE test (test TEXT(10);";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("TEXT", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
        assertEquals(10L, columnNode.getProperty(MySql8DdlLexicon.DATATYPE_LENGTH));
    }

    @Test
    public void shouldParseBinary() {
        String content = "CREATE TABLE test (test BINARY);";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("BINARY", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseVarbinary() {
        String content = "CREATE TABLE test (test VARBINARY);";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("VARBINARY", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
    }
}
