package org.modeshape.sequencer.ddl.dialect.bigquery;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.OR_REPLACE_CLAUSE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.ENABLE_REFRESH;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.EXPIRATION_TIMESTAMP;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.FRIENDLY_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.IF_NOT_EXISTS_CLAUSE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.LABELS;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.MATERIALIZED;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.REFRESH_INTERVAL_MINUTES;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.DESCRIPTION;

public class BigQueryDdlParserViewTest extends DdlParserTestHelper {

    public static final String DDL_FILE_PATH = "ddl/dialect/bigquery/";

    @Before
    public void beforeEach() {
        parser = new BigQueryDdlParser();
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
    public void shouldParseCreateViewStatement() {
        printTest("Create a View in each possible combination");
        final String filename = "create_view.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 6);

        AstNode viewNode = rootNode.getChildren().get(1);
        assertEquals(0, viewNode.getChildCount());
        assertFalse((Boolean) viewNode.getProperty(OR_REPLACE_CLAUSE));
        assertFalse((Boolean) viewNode.getProperty(IF_NOT_EXISTS_CLAUSE));
        assertFalse((Boolean) viewNode.getProperty(MATERIALIZED));

        viewNode = rootNode.getChildren().get(2);
        assertEquals(0, viewNode.getChildCount());
        assertTrue((Boolean) viewNode.getProperty(OR_REPLACE_CLAUSE));
        assertFalse((Boolean) viewNode.getProperty(IF_NOT_EXISTS_CLAUSE));
        assertFalse((Boolean) viewNode.getProperty(MATERIALIZED));

        viewNode = rootNode.getChildren().get(3);
        assertEquals(0, viewNode.getChildCount());
        assertFalse((Boolean) viewNode.getProperty(OR_REPLACE_CLAUSE));
        assertTrue((Boolean) viewNode.getProperty(IF_NOT_EXISTS_CLAUSE));
        assertFalse((Boolean) viewNode.getProperty(MATERIALIZED));

        viewNode = rootNode.getChildren().get(4);
        assertEquals(0, viewNode.getChildCount());
        assertFalse((Boolean) viewNode.getProperty(OR_REPLACE_CLAUSE));
        assertFalse((Boolean) viewNode.getProperty(IF_NOT_EXISTS_CLAUSE));
        assertTrue((Boolean) viewNode.getProperty(MATERIALIZED));

        viewNode = rootNode.getChildren().get(5);
        assertEquals(0, viewNode.getChildCount());
        assertFalse((Boolean) viewNode.getProperty(OR_REPLACE_CLAUSE));
        assertTrue((Boolean) viewNode.getProperty(IF_NOT_EXISTS_CLAUSE));
        assertTrue((Boolean) viewNode.getProperty(MATERIALIZED));
    }

    @Test
    public void shouldParseViewOptions() {
        printTest("Create a View with OPTION LIST");
        final String filename = "create_view_with_option_list.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);

        AstNode viewNode = rootNode.getChildren().get(1);
        assertEquals(0, viewNode.getChildCount());

        assertEquals("TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR)", viewNode.getProperty(EXPIRATION_TIMESTAMP));
        assertEquals("new_view", viewNode.getProperty(FRIENDLY_NAME));
        assertEquals("a view that expires in 2 days", viewNode.getProperty(DESCRIPTION));
        assertEquals("[(\"org_unit\", \"development_view\")]", viewNode.getProperty(LABELS));
    }

    @Test
    public void shouldParseMaterializedViewOptions() {
        printTest("Create a View with OPTION LIST");
        final String filename = "create_view_with_option_list.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);

        AstNode viewNode = rootNode.getChildren().get(2);
        assertEquals(0, viewNode.getChildCount());

        assertEquals("TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR)", viewNode.getProperty(EXPIRATION_TIMESTAMP));
        assertEquals("new_materialized_view", viewNode.getProperty(FRIENDLY_NAME));
        assertEquals("a materialized view that expires in 2 days", viewNode.getProperty(DESCRIPTION));
        assertEquals("[(\"org_unit\", \"development_m_view\")]", viewNode.getProperty(LABELS));
        assertEquals("true", viewNode.getProperty(ENABLE_REFRESH));
        assertEquals("60", viewNode.getProperty(REFRESH_INTERVAL_MINUTES));
    }
}
