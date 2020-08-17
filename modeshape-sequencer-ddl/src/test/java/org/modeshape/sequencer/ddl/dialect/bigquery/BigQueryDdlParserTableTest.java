package org.modeshape.sequencer.ddl.dialect.bigquery;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DATATYPE_NAME;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.NULLABLE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.OR_REPLACE_CLAUSE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TEMPORARY;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.CLUSTER_BY;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.DESCRIPTION;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.EXPIRATION_TIMESTAMP;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.FRIENDLY_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.IF_NOT_EXISTS_CLAUSE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.KMS_KEY_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.LABELS;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.PARTITION_BY;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.PARTITION_EXPIRATION_DAYS;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.REQUIRE_PARTITION_FILTER;

public class BigQueryDdlParserTableTest extends DdlParserTestHelper {

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
        printTest("Create a Table in each possible replace/temp/exists combination");
        final String filename = "create_table.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 7);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(3, tableNode.getChildCount());
        assertFalse((Boolean) tableNode.getProperty(OR_REPLACE_CLAUSE));
        assertFalse((Boolean) tableNode.getProperty(TEMPORARY));
        assertFalse((Boolean) tableNode.getProperty(IF_NOT_EXISTS_CLAUSE));

        tableNode = rootNode.getChildren().get(1);
        assertEquals(1, tableNode.getChildCount());
        assertTrue((Boolean) tableNode.getProperty(OR_REPLACE_CLAUSE));
        assertFalse((Boolean) tableNode.getProperty(TEMPORARY));
        assertFalse((Boolean) tableNode.getProperty(IF_NOT_EXISTS_CLAUSE));

        tableNode = rootNode.getChildren().get(2);
        assertEquals(1, tableNode.getChildCount());
        assertFalse((Boolean) tableNode.getProperty(OR_REPLACE_CLAUSE));
        assertTrue((Boolean) tableNode.getProperty(TEMPORARY));
        assertFalse((Boolean) tableNode.getProperty(IF_NOT_EXISTS_CLAUSE));

        tableNode = rootNode.getChildren().get(3);
        assertEquals(1, tableNode.getChildCount());
        assertFalse((Boolean) tableNode.getProperty(OR_REPLACE_CLAUSE));
        assertTrue((Boolean) tableNode.getProperty(TEMPORARY));
        assertFalse((Boolean) tableNode.getProperty(IF_NOT_EXISTS_CLAUSE));

        tableNode = rootNode.getChildren().get(4);
        assertEquals(1, tableNode.getChildCount());
        assertTrue((Boolean) tableNode.getProperty(OR_REPLACE_CLAUSE));
        assertTrue((Boolean) tableNode.getProperty(TEMPORARY));
        assertFalse((Boolean) tableNode.getProperty(IF_NOT_EXISTS_CLAUSE));

        tableNode = rootNode.getChildren().get(5);
        assertEquals(1, tableNode.getChildCount());
        assertFalse((Boolean) tableNode.getProperty(OR_REPLACE_CLAUSE));
        assertFalse((Boolean) tableNode.getProperty(TEMPORARY));
        assertTrue((Boolean) tableNode.getProperty(IF_NOT_EXISTS_CLAUSE));

        tableNode = rootNode.getChildren().get(6);
        assertEquals(1, tableNode.getChildCount());
        assertFalse((Boolean) tableNode.getProperty(OR_REPLACE_CLAUSE));
        assertTrue((Boolean) tableNode.getProperty(TEMPORARY));
        assertTrue((Boolean) tableNode.getProperty(IF_NOT_EXISTS_CLAUSE));
    }

    @Test
    public void shouldParseTableColumnTypes() {
        printTest("Create a Table with PARTITION BY statement");
        final String filename = "create_table.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 7);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(3, tableNode.getChildCount());

        String[] expectedName = {"id", "name", "age"};
        String[] expectedType = {"INT64",
                                 "ARRAY<STRUCT<part STRING, pos INT64>>",
                                 "INT64"};
        String[] expectedNullability = {"NOT NULL", null, null};

        Iterator<AstNode> iterator = tableNode.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            AstNode column = iterator.next();
            assertEquals(expectedName[i], column.getName());
            assertEquals(expectedType[i], column.getProperty(DATATYPE_NAME));
            assertEquals(expectedNullability[i], column.getProperty(NULLABLE));
        }
    }

    @Test
    public void shouldParseColumnDescription() {
        printTest("Create a Table with column with DESCRIPTION");
        final String filename = "create_table_and_column_with_description.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 1);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(3, tableNode.getChildCount());

        String[] expectedDescription = {"Person id", null, "An optional INT64 field"};

        Iterator<AstNode> iterator = tableNode.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            AstNode column = iterator.next();
            assertEquals(expectedDescription[i], column.getProperty(DESCRIPTION));
        }
    }

    @Test
    public void shouldParseTableOptionList() {
        printTest("Create a Table with OPTION LIST");
        final String filename = "create_table_with_option_list.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 2);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(3, tableNode.getChildCount());

        assertEquals("true", tableNode.getProperty(REQUIRE_PARTITION_FILTER));
        assertEquals("12", tableNode.getProperty(PARTITION_EXPIRATION_DAYS).toString());
        assertEquals("TIMESTAMP \"2025-01-01 00:00:00 UTC\"", tableNode.getProperty(EXPIRATION_TIMESTAMP));
        assertEquals("projects/project_id/key", tableNode.getProperty(KMS_KEY_NAME));
        assertEquals("my_table", tableNode.getProperty(FRIENDLY_NAME));
        assertEquals("any description", tableNode.getProperty(DESCRIPTION));
        assertEquals("[(\"org_unit\", \"development\")]", tableNode.getProperty(LABELS));

        tableNode = rootNode.getChildren().get(1);
        assertEquals("TIMESTAMP_ADD(CURRENT_TIMESTAMP(), INTERVAL 48 HOUR)",
                     tableNode.getProperty(EXPIRATION_TIMESTAMP));
    }

    @Test
    public void shouldParsePartitionByStatement() {
        printTest("Create a Table with PARTITION BY statement");
        final String filename = "create_table_with_partition_by.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 5);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(2, tableNode.getChildCount());
        assertEquals("DATE(_PARTITIONTIME)", tableNode.getProperty(PARTITION_BY));
        for (AstNode column : tableNode) {
            if (column.hasMixin(TYPE_COLUMN_DEFINITION)) {
                String name = column.getName();
                System.out.println("name = " + name);
            }
        }
        tableNode = rootNode.getChildren().get(1);
        assertEquals(2, tableNode.getChildCount());
        assertEquals("_PARTITIONDATE", tableNode.getProperty(PARTITION_BY));

        tableNode = rootNode.getChildren().get(2);
        assertEquals(2, tableNode.getChildCount());
        assertEquals("DATE(birthday)", tableNode.getProperty(PARTITION_BY));

        tableNode = rootNode.getChildren().get(3);
        assertEquals(0, tableNode.getChildCount());
        assertEquals("RANGE_BUCKET(customer_id , GENERATE_ARRAY ( 0 , 100 , 10 ))",
                     tableNode.getProperty(PARTITION_BY));
    }

    @Test
    public void shouldParseClusterByStatement() {
        printTest("Create a Table with CLUSTER BY statement");
        final String filename = "create_table_with_cluster_by.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(3, tableNode.getChildCount());
        assertEquals("id, name, age", tableNode.getProperty(CLUSTER_BY));

        tableNode = rootNode.getChildren().get(1);
        assertEquals(0, tableNode.getChildCount());
        assertEquals("id, name", tableNode.getProperty(CLUSTER_BY));
    }

    @Test
    public void shouldParseCreateTableFromResultOfQuery() {
        printTest("Create a Table from result of query");
        final String filename = "create_table_as_select.ddl";
        String content = getFileContent(filename);
        assertScoreAndParse(content, filename, 3);

        AstNode tableNode = rootNode.getChildren().get(0);
        assertEquals(1, tableNode.getChildCount());

        tableNode = rootNode.getChildren().get(1);
        assertEquals(3, tableNode.getChildCount());
    }
}
