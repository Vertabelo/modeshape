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
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;

/**
 * Testy atrybutów partycjonowania dodanych dla MySQL 8.x
 *
 * @author Marek Berkan
 */
public class MySql8DdlParserTablePartitionsTest extends DdlParserTestHelper {

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
    public void shouldParseTablePartitionBySimple() {
        String content = "CREATE TABLE test (test CHAR) PARTITION BY partition_by_value;";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));
        assertEquals("partition_by_value", tableNode.getProperty(MySql8DdlLexicon.TABLE_PARTITION_BY));
    }

    @Test
    public void shouldParseTablePartitionByComplex() {
        String content = "CREATE TABLE test (test CHAR) PARTITION BY RANGE( YEAR(purchased) ) "
                + "    SUBPARTITION BY HASH( TO_DAYS(purchased) ) "
                + "    SUBPARTITIONS 2 ( "
                + "        PARTITION p0 VALUES LESS THAN (1990), "
                + "        PARTITION p1 VALUES LESS THAN (2000), "
                + "        PARTITION p2 VALUES LESS THAN MAXVALUE "
                + "    );";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));
        assertEquals("RANGE( YEAR(purchased) ) SUBPARTITION BY HASH( TO_DAYS(purchased) ) "
                             + "SUBPARTITIONS 2 ( PARTITION p0 VALUES LESS THAN (1990), PARTITION p1 VALUES LESS THAN (2000), "
                             + "PARTITION p2 VALUES LESS THAN MAXVALUE )", tableNode.getProperty(MySql8DdlLexicon.TABLE_PARTITION_BY));
    }
}
