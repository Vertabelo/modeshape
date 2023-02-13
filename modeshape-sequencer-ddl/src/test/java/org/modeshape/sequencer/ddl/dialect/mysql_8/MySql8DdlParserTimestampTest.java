package org.modeshape.sequencer.ddl.dialect.mysql_8;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.mysql8.MySql8DdlParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;

/**
 * Test parsowania kolumny z typem timestamp
 *
 * @author Marek Berkan
 */
public class MySql8DdlParserTimestampTest  extends DdlParserTestHelper {

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
    public void shouldParseDateTimeWithPrecision() {
        String content = "CREATE TABLE foo (\n"
                + "   field1 datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)\n"
                + ");";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        String columnDatatype = (String)columnNode.getProperty(StandardDdlLexicon.DATATYPE_NAME);
        assertThat(columnDatatype, is("datetime"));
        Integer columnPrecision = (Integer)columnNode.getProperty(StandardDdlLexicon.DATATYPE_PRECISION);
        assertThat(columnPrecision, is(6));

        String nullable = (String)columnNode.getProperty(StandardDdlLexicon.NULLABLE);
        assertThat(nullable, is("NOT NULL"));

        String defaultValue = (String)columnNode.getProperty(StandardDdlLexicon.DEFAULT_VALUE);
        assertThat(defaultValue, is("CURRENT_TIMESTAMP"));
        Integer defaultPrecision = (Integer)columnNode.getProperty(StandardDdlLexicon.DEFAULT_PRECISION);
        assertThat(defaultPrecision, is(6));
    }
}
