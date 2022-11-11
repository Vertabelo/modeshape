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
import org.modeshape.common.text.ParsingException;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon;
import org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlLexicon.TYPE_CREATE_INDEX_STATEMENT;

/**
 * Testy atrybutów algorytmu indeksu dodanych dla MySQL 8.x
 *
 * @author Marek Berkan
 */
public class MySql8DdlParserIndexAlgorithmTest extends DdlParserTestHelper {

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
    public void shouldParseInplaceAlgorithmWithEqual() {
        String content = "CREATE INDEX test_index ON test_table ALGORITHM = INPLACE;";
        assertScoreAndParse(content, null, 1);
        AstNode indexNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(indexNode, TYPE_CREATE_INDEX_STATEMENT));
        assertEquals("INPLACE", indexNode.getProperty(MySql8DdlLexicon.INDEX_ALGORITHM));
    }

    @Test
    public void shouldParseInplaceAlgorithmWithoutEqual() {
        String content = "CREATE INDEX test_index ON test_table ALGORITHM INPLACE;";
        assertScoreAndParse(content, null, 1);
        AstNode indexNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(indexNode, TYPE_CREATE_INDEX_STATEMENT));
        assertEquals("INPLACE", indexNode.getProperty(MySql8DdlLexicon.INDEX_ALGORITHM));
    }

    @Test
    public void shouldParseDefaultAlgorithm() {
        String content = "CREATE INDEX test_index ON test_table ALGORITHM DEFAULT;";
        assertScoreAndParse(content, null, 1);
        AstNode indexNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(indexNode, TYPE_CREATE_INDEX_STATEMENT));
        assertEquals("DEFAULT", indexNode.getProperty(MySql8DdlLexicon.INDEX_ALGORITHM));
    }

    @Test
    public void shouldParseCopyAlgorithm() {
        String content = "CREATE INDEX test_index ON test_table ALGORITHM COPY;";
        assertScoreAndParse(content, null, 1);
        AstNode indexNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(indexNode, TYPE_CREATE_INDEX_STATEMENT));
        assertEquals("COPY", indexNode.getProperty(MySql8DdlLexicon.INDEX_ALGORITHM));
    }

    @Test(expected = ParsingException.class)
    public void shouldNotParseUnexpectedAlgorithm() {
        String content = "CREATE INDEX test_index ON test_table ALGORITHM UNEXPECTED;";
        assertScoreAndParse(content, null, 1);
    }
}
