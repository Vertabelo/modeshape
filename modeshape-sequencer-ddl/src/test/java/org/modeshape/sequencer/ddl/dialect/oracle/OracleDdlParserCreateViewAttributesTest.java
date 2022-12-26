package org.modeshape.sequencer.ddl.dialect.oracle;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;

/**
 * Testy parsowania tworzenia widoków. Wydzielone do osobnej klasy, bo w wersjach 12c/18c/19c/21c przybyło sporo
 * możliwych kombinacji.
 *
 * Dokumentacja: https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-VIEW.html
 *
 * @author Marek Berkan
 */
public class OracleDdlParserCreateViewAttributesTest extends DdlParserTestHelper {

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
    public void shouldParseSimpleCreateView() {
        String sql = "CREATE VIEW my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertEquals("my_view", createViewNode.getName());
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
    }

    @Test
    public void shouldParseCreateViewWithSchema() {
        String sql = "CREATE VIEW my_schema.my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertEquals("my_schema.my_view", createViewNode.getName());
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
    }

    @Test
    public void shouldParseCreateViewWithForce() {
        String sql = "CREATE FORCE VIEW my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_FORCE, Boolean.TRUE);
    }

    @Test
    public void shouldParseCreateViewWithNoForce() {
        String sql = "CREATE NO FORCE VIEW my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_FORCE, null);
    }

    @Test
    public void shouldParseCreateViewWithEditioning() {
        String sql = "CREATE EDITIONING VIEW my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_EDITIONING, "EDITIONABLE EDITIONING");
    }

    @Test
    public void shouldParseCreateViewWithEditionableEditioning() {
        String sql = "CREATE EDITIONABLE EDITIONING VIEW my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_EDITIONING, "EDITIONABLE EDITIONING");
    }

    @Test
    public void shouldParseCreateViewWithNoneditionable() {
        String sql = "CREATE NONEDITIONABLE VIEW my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_EDITIONING, "NONEDITIONABLE");
    }

    @Test
    public void shouldParseCreateViewWithSharingEqualsMetadata() {
        String sql = "CREATE VIEW my_view SHARING=METADATA \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_SHARING, "METADATA");
    }

    @Test
    public void shouldParseCreateViewWithSharingEqualsData() {
        String sql = "CREATE VIEW my_view SHARING=DATA \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_SHARING, "DATA");
    }

    @Test
    public void shouldParseCreateViewWithSharingEqualsExtendedData() {
        String sql = "CREATE VIEW my_view SHARING=EXTENDED DATA \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_SHARING, "EXTENDED DATA");
    }

    @Test
    public void shouldParseCreateViewWithSharingEqualsNone() {
        String sql = "CREATE VIEW my_view SHARING=NONE \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_SHARING, "NONE");
    }

    @Test
    public void shouldParseCreateViewWithAllParts() {
        String sql = "CREATE OR REPLACE FORCE EDITIONABLE EDITIONING VIEW my_view SHARING=EXTENDED DATA \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createViewNode = rootNode.getChildren().get(0);
        assertMixinType(createViewNode, TYPE_CREATE_VIEW_STATEMENT);
        assertProperty(createViewNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_FORCE, Boolean.TRUE);
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_EDITIONING, "EDITIONABLE EDITIONING");
        assertProperty(createViewNode, OracleDdlLexicon.VIEW_SHARING, "EXTENDED DATA");
    }
}
