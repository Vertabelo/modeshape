package org.modeshape.sequencer.ddl.dialect.oracle;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

import static org.junit.Assert.assertEquals;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT;

/**
 * Parsing tests the creation of materialized views with attributes.
 *
 * Documentation: https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-MATERIALIZED-VIEW.html
 *
 * @author Marek Berkan
 */
public class OracleDdlParserCreateMaterializedViewAttributesTest extends DdlParserTestHelper {

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
        String sql = "CREATE MATERIALIZED VIEW my_view \n"
                + "AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
    }

    @Test
    public void shouldParseCreateViewWithSchema() {
        String sql = "CREATE MATERIALIZED VIEW my_schema.my_view \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertEquals("my_schema.my_view", createNode.getName());
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
    }

    @Test
    public void shouldParseCreateViewWithCollation() {
        String sql = "CREATE MATERIALIZED VIEW my_view DEFAULT COLLATION my_collation \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_COLLATION, "my_collation");
    }

    @Test
    public void shouldParseCreateViewWithRefreshMethodFast() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH FAST \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "REFRESH FAST");
    }

    @Test
    public void shouldParseCreateViewWithRefreshMethodComplete() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH COMPLETE \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "REFRESH COMPLETE");
    }

    @Test
    public void shouldParseCreateViewWithRefreshMethodForce() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH FORCE \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "REFRESH FORCE");
    }

    @Test
    public void shouldParseCreateViewWithRefreshMethodNever() {
        String sql = "CREATE MATERIALIZED VIEW my_view NEVER REFRESH \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "NEVER REFRESH");
    }

    @Test
    public void shouldParseCreateViewWithRefreshOnDemand() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH ON DEMAND \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_ON, "ON DEMAND");
    }

    @Test
    public void shouldParseCreateViewWithRefreshOnCommit() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH ON COMMIT \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_ON, "ON COMMIT");
    }

    @Test
    public void shouldParseCreateViewWithRefreshOnStatement() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH ON STATEMENT \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_ON, "ON STATEMENT");
    }

    @Test
    public void shouldParseCreateViewWithRefreshWithPrimaryKey() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH WITH PRIMARY KEY \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_WITH, "WITH PRIMARY KEY");
    }

    @Test
    public void shouldParseCreateViewWithRefreshWithRowId() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH WITH ROWID \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_WITH, "WITH ROWID");
    }

    /**
     * Combination of 3 values to REFRESH in order "1"
     */
    @Test
    public void shouldParseCreateViewWithRefreshFastOnDemandWithPrimaryKey() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH FAST ON DEMAND WITH PRIMARY KEY \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "REFRESH FAST");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_ON, "ON DEMAND");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_WITH, "WITH PRIMARY KEY");
    }

    /**
     * Combination of 3 values to REFRESH in order "2"
     */
    @Test
    public void shouldParseCreateViewWithRefreshWithPrimaryKeyFastOnDemand() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH WITH PRIMARY KEY FAST ON DEMAND \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "REFRESH FAST");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_ON, "ON DEMAND");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_WITH, "WITH PRIMARY KEY");
    }

    /**
     * Combination of 3 values to REFRESH in order "3"
     */
    @Test
    public void shouldParseCreateViewWithRefreshOnDemandWithPrimaryKeyFast() {
        String sql = "CREATE MATERIALIZED VIEW my_view REFRESH ON DEMAND WITH PRIMARY KEY FAST  \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "REFRESH FAST");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_ON, "ON DEMAND");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_WITH, "WITH PRIMARY KEY");
    }

    @Test
    public void shouldParseCreateViewWithOnPrebuildTableWithReducedPrecision() {
        String sql = "CREATE MATERIALIZED VIEW my_view ON PREBUILT TABLE WITH REDUCED PRECISION \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_ON_PREBUILT_TABLE, "WITH REDUCED PRECISION");
    }

    @Test
    public void shouldParseCreateViewWithOnPrebuildTableWithoutReducedPrecision() {
        String sql = "CREATE MATERIALIZED VIEW my_view ON PREBUILT TABLE WITHOUT REDUCED PRECISION \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_ON_PREBUILT_TABLE, "WITHOUT REDUCED PRECISION");
    }

    @Test
    public void shouldParseCreateViewWithCache() {
        String sql = "CREATE MATERIALIZED VIEW my_view CACHE \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_CACHE, "CACHE");
    }

    @Test
    public void shouldParseCreateViewWithNoCache() {
        String sql = "CREATE MATERIALIZED VIEW my_view NOCACHE \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_CACHE, "NOCACHE");
    }

    @Test
    public void shouldParseCreateViewWithBuildImmediate() {
        String sql = "CREATE MATERIALIZED VIEW my_view BUILD IMMEDIATE \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_BUILD, "IMMEDIATE");
    }

    @Test
    public void shouldParseCreateViewWithBuildDeferred() {
        String sql = "CREATE MATERIALIZED VIEW my_view BUILD DEFERRED \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_BUILD, "DEFERRED");
    }

    @Test
    public void shouldParseCreateViewWithOnQueryComputationEnable() {
        String sql = "CREATE MATERIALIZED VIEW my_view ENABLE ON QUERY COMPUTATION \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_ON_QUERY_COMPUTATION, "ENABLE");
    }

    @Test
    public void shouldParseCreateViewWithOnQueryComputationDisable() {
        String sql = "CREATE MATERIALIZED VIEW my_view DISABLE ON QUERY COMPUTATION \n"
                + " AS select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_ON_QUERY_COMPUTATION, "DISABLE");
    }

    @Test
    public void shouldParseCreateViewWithAllAttributes() {
        String sql = "CREATE MATERIALIZED VIEW my_schema.my_view\n"
                + " DEFAULT COLLATION my_collation\n"
                + " my_physical_properties\n"
                + " ON PREBUILT TABLE WITH REDUCED PRECISION\n"
                + " my_partitioning\n"
                + " CACHE\n"
                + " BUILD IMMEDIATE\n"
                + " my_physical_properties\n"
                + " REFRESH FAST\n"
                + " ON DEMAND\n"
                + " START WITH my_refresh_start\n"
                + " NEXT my_refresh_next\n"
                + " WITH PRIMARY KEY\n"
                + " USING my_refresh_using\n"
                + " ENABLE ON QUERY COMPUTATION\n"
                + "\n"
                + "AS\n"
                + "select * from product;";

        assertScoreAndParse(sql, null, 1);

        AstNode createNode = rootNode.getChildren().get(0);
        assertMixinType(createNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT);
        assertEquals("my_schema.my_view", createNode.getName());
        assertProperty(createNode, CREATE_VIEW_QUERY_EXPRESSION, "select * from product");

        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_COLLATION, "my_collation");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_ON_PREBUILT_TABLE, "WITH REDUCED PRECISION");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_CACHE, "CACHE");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_BUILD, "IMMEDIATE");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_METHOD, "REFRESH FAST");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_ON, "ON DEMAND");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_REFRESH_WITH, "WITH PRIMARY KEY");
        assertProperty(createNode, OracleDdlLexicon.MATERIALIZED_VIEW_ON_QUERY_COMPUTATION, "ENABLE");
    }
}
