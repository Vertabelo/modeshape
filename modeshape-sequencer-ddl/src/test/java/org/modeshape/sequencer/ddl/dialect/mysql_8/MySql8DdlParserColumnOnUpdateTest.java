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
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;

/**
 * Testy klauzuli "on update" do kolumny
 *
 * @author Marek Berkan
 */
public class MySql8DdlParserColumnOnUpdateTest extends DdlParserTestHelper {

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
    public void shouldParseOnUpdateWithCurrentTimestamp() {
        String content = "CREATE TABLE test (test DATETIME NOT NULL ON UPDATE CURRENT_TIMESTAMP);";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("DATETIME", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
        assertEquals("NOT NULL", columnNode.getProperty(MySql8DdlLexicon.NULLABLE));
        assertEquals("CURRENT_TIMESTAMP", columnNode.getProperty(MySql8DdlLexicon.ON_UPDATE));
    }

    @Test
    public void shouldParseOnUpdateWithCurrentTimestampWithPrecision() {
        String content = "CREATE TABLE test (test DATETIME NOT NULL "
                + "DEFAULT CURRENT_TIMESTAMP(6) "
                + "ON UPDATE CURRENT_TIMESTAMP(6));";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("DATETIME", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
        assertEquals("NOT NULL", columnNode.getProperty(MySql8DdlLexicon.NULLABLE));
        assertEquals("CURRENT_TIMESTAMP", columnNode.getProperty(MySql8DdlLexicon.DEFAULT_VALUE));
        assertEquals("CURRENT_TIMESTAMP(6)", columnNode.getProperty(MySql8DdlLexicon.ON_UPDATE));
    }

    @Test
    public void shouldParseOnUpdateWithCurrentTimestampWithPrecisionBeforeDefault() {
        String content = "CREATE TABLE test (test DATETIME NOT NULL "
                + "ON UPDATE CURRENT_TIMESTAMP(6) "
                + "DEFAULT CURRENT_TIMESTAMP(6));";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("DATETIME", columnNode.getProperty(MySql8DdlLexicon.DATATYPE_NAME));
        assertEquals("NOT NULL", columnNode.getProperty(MySql8DdlLexicon.NULLABLE));
        assertEquals("CURRENT_TIMESTAMP", columnNode.getProperty(MySql8DdlLexicon.DEFAULT_VALUE));
        assertEquals("CURRENT_TIMESTAMP(6)", columnNode.getProperty(MySql8DdlLexicon.ON_UPDATE));
    }
}
