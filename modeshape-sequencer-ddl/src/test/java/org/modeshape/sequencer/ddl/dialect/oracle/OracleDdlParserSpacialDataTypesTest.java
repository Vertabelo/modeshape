package org.modeshape.sequencer.ddl.dialect.oracle;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;

/**
 * Testy parsowania DDL tworzenia tabeli z nowymi typami danych.
 *
 * Dokumentacja: https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-TABLE.html
 *
 * @author Marek Berkan
 */
public class OracleDdlParserSpacialDataTypesTest extends DdlParserTestHelper {

    @Before
    public void beforeEach() {
        parser = new OracleDdlParser();
        setPrintToConsole(false);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }

    @Test
    public void shouldParseCreateTableWithTypeSdoGeometry() {
        String sql = "CREATE TABLE my_table (my_sdo_geometry SDO_GEOMETRY NOT NULL);";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("SDO_GEOMETRY", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithTypeSdoTopoGeometry() {
        String sql = "CREATE TABLE my_table (my_sdo_geometry SDO_TOPO_GEOMETRY NOT NULL);";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("SDO_TOPO_GEOMETRY", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithTypeSdoGeoraster() {
        String sql = "CREATE TABLE my_table (my_sdo_geometry SDO_GEORASTER NOT NULL);";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("SDO_GEORASTER", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }
}
