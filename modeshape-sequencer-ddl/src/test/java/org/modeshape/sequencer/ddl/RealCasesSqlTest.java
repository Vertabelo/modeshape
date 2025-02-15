package org.modeshape.sequencer.ddl;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlParser;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.modeshape.sequencer.ddl.node.AstNodeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

/**
 * Testing for cases that cause errors in production.
 *
 * @author Adam Mościcki
 */
public class RealCasesSqlTest extends DdlParserTestHelper {

    private AstNodeFactory nodeFactory;

    @Before
    public void createNodeFactory() {
        nodeFactory = new AstNodeFactory();
    }

    private AstNode createDdlStatementsContainer( final String parserId ) {
        final AstNode node = this.nodeFactory.node(StandardDdlLexicon.STATEMENTS_CONTAINER);
        node.setProperty(JcrConstants.JCR_PRIMARY_TYPE, JcrConstants.NT_UNSTRUCTURED);
        node.setProperty(StandardDdlLexicon.PARSER_ID, parserId);
        return node;
    }

    @Test
    public void postgresKillerTestSimple() throws IOException {
        StandardDdlParser parser = new PostgresDdlParser();
        InputStream inputStream = this.getClass().getResourceAsStream("/test-sql/mysql_as_postgres.sql");
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        inputStream.close();
        boolean illegalState = false;
        try {
            parser.parse(result, createDdlStatementsContainer(PostgresDdlParser.ID), null);
        } catch (IllegalStateException exception) {
            illegalState = true;
        }
        // ends successfully
        assertTrue(illegalState == true);
    }



    @Test
    public void postgresKillerTestFull() throws IOException {
        StandardDdlParser parser = new PostgresDdlParser();
        InputStream inputStream = this.getClass().getResourceAsStream("/test-sql/mysql_as_postgres_full.sql");
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        System.out.println(result);
        inputStream.close();
        boolean illegalState = false;
        try {
            parser.parse(result, createDdlStatementsContainer(PostgresDdlParser.ID), null);
        } catch (IllegalStateException exception) {
            illegalState = true;
        }
        // ends successfully
        assertTrue(illegalState == true);

    }
}
