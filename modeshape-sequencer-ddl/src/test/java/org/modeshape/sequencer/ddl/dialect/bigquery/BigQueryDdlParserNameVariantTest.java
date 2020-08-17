package org.modeshape.sequencer.ddl.dialect.bigquery;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.DATASET_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.PROJECT_NAME;

public class BigQueryDdlParserNameVariantTest extends DdlParserTestHelper {
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
    public void shouldParseSimpleDatabaseName() {
        printTest("Create a Tables in database name variants");
        final String filename = "create_table_with_name_variants.ddl";

        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals("person_0", tableNode.getName());
        assertNull(tableNode.getProperty(DATASET_NAME));
        assertNull(null, tableNode.getProperty(PROJECT_NAME));

        tableNode = rootNode.getChildren().get(1);
        assertEquals("person_1", tableNode.getName());
        assertEquals("bigquery_test", tableNode.getProperty(DATASET_NAME));
        assertNull(null, tableNode.getProperty(PROJECT_NAME));

        tableNode = rootNode.getChildren().get(2);
        assertEquals("person_2", tableNode.getName());
        assertEquals("bigquery_test", tableNode.getProperty(DATASET_NAME));
        assertEquals("vertabelo-bigquery", tableNode.getProperty(PROJECT_NAME));
    }
}
