package org.modeshape.sequencer.ddl.dialect.oracle;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

/**
 * Testy parsowania sekwencji.
 *
 * Dokumentacja: https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-SEQUENCE.html
 *
 * @author Marek Berkan
 */
public class OracleDdlParserCreateSequenceAttributesTest extends DdlParserTestHelper {

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
    public void shouldParseCreateSequenceWithSharingEqualsMetadata() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     SHARING=METADATA\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SHARING, "METADATA");
    }

    @Test
    public void shouldParseCreateSequenceWithSharingEqualsData() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     SHARING=DATA\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SHARING, "DATA");
    }

    @Test
    public void shouldParseCreateSequenceWithSharingEqualsNone() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     SHARING=NONE\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SHARING, "NONE");
    }

    @Test
    public void shouldParseCreateSequenceWithKeep() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      KEEP;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_KEEP, Boolean.TRUE);
    }

    @Test
    public void shouldParseCreateSequenceWithNoKeep() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      NOKEEP;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_KEEP, Boolean.FALSE);
    }

    @Test
    public void shouldParseCreateSequenceWithScaleExtend() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      SCALE EXTEND;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SCALE, "SCALE EXTEND");
    }

    @Test
    public void shouldParseCreateSequenceWithScaleNoExtend() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      SCALE NOEXTEND;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SCALE, "SCALE NOEXTEND");
    }

    @Test
    public void shouldParseCreateSequenceWithNoScale() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      NOSCALE;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SCALE, "NOSCALE");
    }

    @Test
    public void shouldParseCreateSequenceWithShardExtend() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      SHARD EXTEND;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SHARD, "SHARD EXTEND");
    }

    @Test
    public void shouldParseCreateSequenceWithShardNoExtend() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      SHARD NOEXTEND;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SHARD, "SHARD NOEXTEND");
    }

    @Test
    public void shouldParseCreateSequenceWithNoShard() {
        String sql = "CREATE SEQUENCE my_sequence\n"
                + "     NOMINVALUE\n"
                + "     NOMAXVALUE\n"
                + "     NOCACHE\n"
                + "     NOCYCLE\n"
                + "      NOSHARD;";

        assertScoreAndParse(sql, null, 1);

        AstNode createSequenceNode = rootNode.getChildren().get(0);
        assertMixinType(createSequenceNode, OracleDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT);
        assertProperty(createSequenceNode, OracleDdlLexicon.SEQ_SHARD, "NOSHARD");
    }
}
