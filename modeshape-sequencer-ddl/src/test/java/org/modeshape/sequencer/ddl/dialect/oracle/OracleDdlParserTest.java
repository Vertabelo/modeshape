/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org
 */
package org.modeshape.sequencer.ddl.dialect.oracle;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.*;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.COLUMN_ENCRYPT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.COLUMN_SALT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.COLUMN_USING_ENCRYPT_ALGORITHM;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_ATTRIBUTES;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_COMPRESS;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.PHYSICAL_INITRANS;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_LOGGING;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_ONLINE;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_PARALLEL;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.PHYSICAL_PCTFREE;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.PHYSICAL_PCTUSED;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_REVERSE;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_SORT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_BUFFER_POOL;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_CLAUSE;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_ENCRYPT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_FREELISTS;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_FREELIST_GROUPS;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_INITIAL;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_MAXEXTENTS;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_MAXSIZE_UNLIMITED;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_MINEXTENTS;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_NEXT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_OPTIMAL;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.STORAGE_PCTINCREASE;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.INDEX_TABLESPACE;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TABLE_ORGANIZATION;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TABLE_TABLESPACE;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_ALTER_INDEXTYPE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_ALTER_INDEX_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_ANALYZE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_COMMENT_ON_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_JAVA_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_MATERIALIZED_VIEW_LOG_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_PROCEDURE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_TABLE_INDEX_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_TRIGGER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_ROLLBACK_STATEMENT;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.common.FixFor;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;

public class OracleDdlParserTest extends DdlParserTestHelper {

    public static final String DDL_FILE_PATH = "ddl/dialect/oracle/";

    @Before
    public void beforeEach() {
        parser = new OracleDdlParser();
        setPrintToConsole(false);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }

    // @Test
    // public void shouldParseOracleDDL() {
    // String content = getFileContent(DDL_FILE_PATH + "oracle_test_create.ddl");
    //	  
    // List<Statement> stmts = parser.parse(content);
    //	  	
    // System.out.println("  END PARSING.  # Statements = " + stmts.size());
    //	  
    // }

    @Test
    public void shouldParseCreateOrReplaceTrigger() {
        printTest("shouldParseCreateOrReplaceTrigger()");
        String content = "CREATE OR REPLACE TRIGGER drop_trigger" + DdlConstants.SPACE + "BEFORE DROP ON hr.SCHEMA"
                         + DdlConstants.SPACE + "BEGIN" + DdlConstants.SPACE
                         + "RAISE_APPLICATION_ERROR ( num => -20000,msg => 'Cannot drop object');" + DdlConstants.SPACE + "END;"
                         + DdlConstants.SPACE + "/";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TRIGGER_STATEMENT));

    }

    @Test
    public void shouldParseAnalyze() {
        printTest("shouldParseAnalyze()");
        String content = "ANALYZE TABLE customers VALIDATE STRUCTURE ONLINE;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ANALYZE_STATEMENT));
    }

    @Test
    public void shouldParseRollbackToSavepoint() {
        printTest("shouldParseRollbackToSavepoint()");
        String content = "ROLLBACK TO SAVEPOINT banda_sal;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ROLLBACK_STATEMENT));
    }

    @Test
    public void shouldParseAlterTableAddREF() {
        printTest("shouldParseAlterTableAddREF()");
        String content = "ALTER TABLE staff ADD (REF(dept) WITH ROWID);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseAlterTableADDWithNESTED_TABLE() {
        // This is a one-off case where there is a custom datatype (i.e. skill_table_type)
        printTest("shouldParseAlterTableADDWithNESTED_TABLE()");
        String content = "ALTER TABLE employees ADD (skills skill_table_type) NESTED TABLE skills STORE AS nested_skill_table;";
        assertScoreAndParse(content, null, 2); // ALTER TABLE + 1 PROBLEM
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseAlterIndexRename() {
        printTest("shouldParseAlterIndexRename()");
        String content = "ALTER INDEX upper_ix RENAME TO upper_name_ix;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_INDEX_STATEMENT));
    }

    @Test
    public void shouldParseAlterIndexMODIFY() {
        printTest("shouldParseAlterIndexMODIFY()");
        String content = "ALTER INDEX cost_ix MODIFY PARTITION p3 STORAGE(MAXEXTENTS 30) LOGGING;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_INDEX_STATEMENT));
    }

    @Test
    public void shouldParseAlterIndexDROP() {
        printTest("shouldParseAlterIndexDROP()");
        String content = "ALTER INDEX cost_ix DROP PARTITION p1;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_INDEX_STATEMENT));
    }

    @Test
    public void shouldParseAlterIndexTypeADD() {
        printTest("shouldParseAlterIndexTypeADD()");
        String content = "ALTER INDEXTYPE position_indextype ADD lob_contains(CLOB, CLOB);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_INDEXTYPE_STATEMENT));
    }

    @Test
    public void shouldParseTEMP_TEST() {
        printTest("shouldParseTEMP_TEST()");
        String content = "COMMENT ON COLUMN employees.job_id IS 'abbreviated job title';";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_COMMENT_ON_STATEMENT));
    }

    // GRANT ALL ON bonuses TO hr WITH GRANT OPTION;
    @Test
    public void shouldParseGrantAllOn() {
        printTest("shouldParseGrant()");
        String content = "GRANT ALL ON bonuses TO hr WITH GRANT OPTION;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_GRANT_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable() {
        printTest("shouldParseCreateTable()");

        String content = "CREATE TABLE MY_TABLE_A (PARTID BLOB (255) NOT NULL DEFAULT (100), "
                         + " -- COLUMN 1 COMMENT with comma \nPARTCOLOR INTEGER NOT NULL) ON COMMIT DELETE ROWS;";
        assertScoreAndParse(content, null, 2);
    }

    @FixFor( "MODE-820" )
    @Test
    public void shouldParseCreateTableWithKilobyteInSize() {
        printTest("shouldParseCreateTableWithKilobyteInSize()");

        String content = "CREATE TABLE MY_TABLE_A (PARTID BLOB (2K) NOT NULL, "
                         + " -- COLUMN 1 COMMENT with comma \nPARTCOLOR CHAR(4M) NOT NULL) ON COMMIT DELETE ROWS;";

        assertScoreAndParse(content, null, 2);
    }

    @FixFor("EDWM-4426")
    @Test
    public void testParseCreateTableWithImplicitColumnReferences() {
        printTest("testParseCreateTableWithImplicitColumnReferences()");

        String content = "CREATE TABLE person (\n" +
                "  id NUMBER(6) NOT NULL,\n" +
                "  first_name VARCHAR2(30) NOT NULL,\n" +
                "  last_name VARCHAR2(30) NOT NULL,\n" +
                "  changed_by NUMBER(6) NOT NULL,\n" +
                "  created_by NUMBER(6) NOT NULL REFERENCES person,\n" +
                "  CONSTRAINT person#PK\n" +
                "    PRIMARY KEY (id),\n" +
                "  CONSTRAINT person#person#FK\n" +
                "    FOREIGN KEY (changed_by)\n" +
                "    REFERENCES person\n" +
                ");";

        assertScoreAndParse(content, null, 1);
    }

    @Test
    public void shouldParseAlterTableWithModifyClause() {
        printTest("shouldParseAlterTableWithModifyClause()");

        String content = "ALTER TABLE employees MODIFY LOB (resume) (CACHE);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseAlterTableWithAddColumns() {
        printTest("shouldParseAlterTableWithModifyClause()");

        String content = "ALTER TABLE countries \n" + "     ADD (duty_pct     NUMBER(2,2)  CHECK (duty_pct < 10.5),\n"
                         + "     visa_needed  VARCHAR2(3));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
        assertEquals(3, childNode.getChildCount()); // 2 columns + CHECK constraint
    }

    @Test
    public void shouldParseJava() {
        printTest("shouldParseJava()");

        String content = "CREATE JAVA SOURCE NAMED \"Hello\" AS public class Hello { public static String hello() {return \"Hello World\";   } };";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_JAVA_STATEMENT));
    }

    @Test
    public void shouldParseCreateOrReplaceTriggerWithEmbeddedStatements() {
        printTest("shouldParseCreateOrReplaceTriggerWithEmbeddedStatements()");

        String content = "CREATE OR REPLACE TRIGGER order_info_insert" + " INSTEAD OF INSERT ON order_info" + " DECLARE"
                         + "   duplicate_info EXCEPTION;" + "   PRAGMA EXCEPTION_INIT (duplicate_info, -00001);" + " BEGIN"
                         + "   INSERT INTO customers" + "     (customer_id, cust_last_name, cust_first_name)" + "   VALUES ("
                         + "   :new.customer_id, " + "   :new.cust_last_name," + "   :new.cust_first_name);"
                         + " INSERT INTO orders (order_id, order_date, customer_id)" + " VALUES (" + "   :new.order_id,"
                         + "   :new.order_date," + "   :new.customer_id);" + " EXCEPTION" + "   WHEN duplicate_info THEN"
                         + "    RAISE_APPLICATION_ERROR (" + "       num=> -20107,"
                         + "       msg=> 'Duplicate customer or order ID');" + " END order_info_insert;" + " /";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TRIGGER_STATEMENT));

    }

    @Test
    public void shouldParseGrantReadOnDirectory() {
        printTest("shouldParseGrantReadOnDirectory()");

        String content = "GRANT READ ON DIRECTORY bfile_dir TO hr \n" + "     WITH GRANT OPTION;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_GRANT_STATEMENT));
    }

    @Test
    public void shouldParseCreateFunction_1() {
        printTest("shouldParseCreateFunction_1()");
        String content = "CREATE OR REPLACE FUNCTION text_length(a CLOB)"
                         + " RETURN NUMBER DETERMINISTIC IS BEGIN RETURN DBMS_LOB.GETLENGTH(a); END; /";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_FUNCTION_STATEMENT));
    }

    @Test
    public void shouldParseCreateProcedure_1() {
        printTest("shouldParseCreateProcedure_1()");
        String content = "CREATE PROCEDURE remove_emp (employee_id NUMBER) AS tot_emps NUMBER;" + NEWLINE + "BEGIN" + NEWLINE
                         + "   DELETE FROM employees" + NEWLINE + "   WHERE employees.employee_id = remove_emp.employee_id;"
                         + NEWLINE + "tot_emps := tot_emps - 1;" + NEWLINE + "END;" + NEWLINE + "/";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_PROCEDURE_STATEMENT));
    }

    @Test
    public void shouldParseCreateProcedure_2() {
        printTest("shouldParseCreateProcedure_2()");
        String content = "CREATE OR REPLACE PROCEDURE add_emp (employee_id NUMBER, employee_age NUMBER) AS tot_emps NUMBER;"
                         + NEWLINE + "BEGIN" + NEWLINE + "   INSERT INTO employees" + NEWLINE
                         + "   WHERE employees.employee_id = remove_emp.employee_id;" + NEWLINE + "tot_emps := tot_emps + 1;"
                         + NEWLINE + "END;" + NEWLINE + "/";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_PROCEDURE_STATEMENT));
        assertEquals(2, childNode.getChildCount());
    }

    @Test
    public void shouldParseOracleProceduresAndFunctions() {
        printTest("shouldParseOracleProceduresAndFunctions()");
        String content = getFileContent(DDL_FILE_PATH + "create_procedure_statements.ddl");
        assertScoreAndParse(content, "create_procedure_statements.ddl", 4);
    }

    @Test
    public void shouldParseCreateMaterializedView() {
        printTest("shouldParseCreateMaterializedView()");
        String content = " CREATE MATERIALIZED VIEW sales_mv" + NEWLINE + "BUILD IMMEDIATE" + NEWLINE + "REFRESH FAST ON COMMIT"
                         + NEWLINE + "AS SELECT t.calendar_year, p.prod_id, " + NEWLINE + "   SUM(s.amount_sold) AS sum_sales"
                         + NEWLINE + "   FROM times t, products p, sales s" + NEWLINE
                         + "   WHERE t.time_id = s.time_id AND p.prod_id = s.prod_id" + NEWLINE
                         + "   GROUP BY t.calendar_year, p.prod_id;" + NEWLINE;
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT));
    }

    @Test
    public void shouldParseOracleStatements_1() {
        printTest("shouldParseOracleStatements_1()");
        String content = getFileContent(DDL_FILE_PATH + "oracle_test_statements_1.ddl");
        assertScoreAndParse(content, "oracle_test_statements_1", 50);
    }

    @Test
    public void shouldParseOracleStatements_2() {
        printTest("shouldParseOracleStatements_2()");
        String content = getFileContent(DDL_FILE_PATH + "oracle_test_statements_2.ddl");
        assertScoreAndParse(content, "oracle_test_statements_2", 48);
    }

    @Test
    public void shouldParseOracleStatements_3() {
        printTest("shouldParseOracleStatements_3()");
        String content = getFileContent(DDL_FILE_PATH + "oracle_test_statements_3.ddl");
        assertScoreAndParse(content, "oracle_test_statements_3", 50);
    }

    @Test
    public void shouldParseOracleStatements_4() {
        printTest("shouldParseOracleStatements_4()");
        String content = getFileContent(DDL_FILE_PATH + "oracle_test_statements_4.ddl");
        assertScoreAndParse(content, "oracle_test_statements_4", 48);
    }

    @FixFor("MODE-1326")
    @Test
    public void shouldSequenceCreateIndexStatements() throws Exception {
        String content = getFileContent(DDL_FILE_PATH + "mode_1326.ddl");
        assertScoreAndParse(content, "mode_1326", 477);
    }

    @Test
    public void shouldParseCreateTableIndexStatementOrderedWithVariations() {
        final String content = "CREATE TABLE BB_TEST_GROUP\n" //$NON-NLS-1$
                               + "(\n" //$NON-NLS-1$
                               + "BB_TEST_GROUP_ID NUMBER DEFAULT 0 NOT NULL,\n" //$NON-NLS-1$
                               + "TEST_GROUP_DISPLAY CHAR(15 BYTE) DEFAULT ' ' NOT NULL,\n" //$NON-NLS-1$
                               + "TEST_GROUP_DESCRIPTION  VARCHAR2(50 BYTE),\n" //$NON-NLS-1$
                               + "ACTIVE_IND NUMBER DEFAULT 0 NOT NULL,\n" //$NON-NLS-1$
                               + "ACTIVE_STATUS_CD NUMBER DEFAULT 0 NOT NULL,\n" //$NON-NLS-1$
// this includes PL/SQL and does not parse        + "ACTIVE_STATUS_DT_TM DATE DEFAULT TO_DATE ( '01/01/190000:00:00' , 'MM/DD/YYYYHH24:MI:SS' ) NOT NULL,\n" //$NON-NLS-1$
                               + "ACTIVE_STATUS_PRSNL_ID NUMBER DEFAULT 0 NOT NULL,\n" //$NON-NLS-1$
                               + "UPDT_CNT NUMBER DEFAULT 0 NOT NULL,\n" //$NON-NLS-1$
                               + "UPDT_DT_TM DATE DEFAULT SYSDATE NOT NULL,\n" //$NON-NLS-1$
                               + "UPDT_ID NUMBER DEFAULT 0 NOT NULL,\n" //$NON-NLS-1$
                               + "UPDT_TASK NUMBER DEFAULT 0 NOT NULL,\n" //$NON-NLS-1$
                               + "UPDT_APPLCTX NUMBER DEFAULT 0 NOT NULL\n" //$NON-NLS-1$
                               + ")\n" //$NON-NLS-1$
                               + "LOGGING \n" //$NON-NLS-1$
                               + "NOCOMPRESS \n" //$NON-NLS-1$
                               + "NOCACHE\n" //$NON-NLS-1$
                               + "NOPARALLEL\n" //$NON-NLS-1$
                               + "MONITORING;\n" //$NON-NLS-1$
                               + "" //$NON-NLS-1$
                               + "CREATE UNIQUE INDEX XAK1BB_TEST_GROUP ON BB_TEST_GROUP\n" //$NON-NLS-1$
                               + "(TEST_GROUP_DISPLAY ASC, UPDT_CNT DESC)\n" //$NON-NLS-1$
                               + "LOGGING\n" //$NON-NLS-1$
                               + "COMPRESS 4\n" //$NON-NLS-1$
                               + "NOPARALLEL\n" //$NON-NLS-1$
                               + "UNUSABLE;"; //$NON-NLS-1$
        this.parser.parse(content, this.rootNode, null);
        assertThat(this.rootNode.getChildCount(), is(2)); // table & index

        final List<AstNode> nodes = this.rootNode.childrenWithName("XAK1BB_TEST_GROUP");
        assertThat(nodes.size(), is(1));

        final AstNode indexNode = nodes.get(0);
        assertMixinType(indexNode, OracleDdlLexicon.TYPE_CREATE_TABLE_INDEX_STATEMENT);
        assertProperty(indexNode, OracleDdlLexicon.INDEX_TYPE, OracleDdlConstants.IndexTypes.TABLE);
        assertProperty(indexNode, OracleDdlLexicon.TABLE_NAME, "BB_TEST_GROUP");
        assertProperty(indexNode, OracleDdlLexicon.UNIQUE_INDEX, true);
        assertProperty(indexNode, OracleDdlLexicon.BITMAP_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.UNUSABLE_INDEX, true);
        assertThat(indexNode.getProperty(OracleDdlLexicon.TABLE_ALIAS), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.OTHER_INDEX_REFS), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.WHERE_CLAUSE), is(nullValue()));

        assertNull(indexNode.getProperty(INDEX_ATTRIBUTES));
        assertEquals(Boolean.TRUE, indexNode.getProperty(INDEX_LOGGING));
        assertEquals("4", indexNode.getProperty(INDEX_COMPRESS));
        assertEquals("NOPARALLEL", indexNode.getProperty(INDEX_PARALLEL));


        // column references
        assertThat(indexNode.getChildCount(), is(2));

        { // TEST_GROUP_DISPLAY column
            final AstNode colRefNode = indexNode.getChild(0);
            assertThat(colRefNode.getName(), is("TEST_GROUP_DISPLAY"));
            assertProperty(colRefNode, OracleDdlLexicon.INDEX_ORDER, "ASC");
            assertMixinType(colRefNode, StandardDdlLexicon.TYPE_COLUMN_REFERENCE);
        }

        { // UPDT_CNT column
            final AstNode colRefNode = indexNode.getChild(1);
            assertThat(colRefNode.getName(), is("UPDT_CNT"));
            assertProperty(colRefNode, OracleDdlLexicon.INDEX_ORDER, "DESC");
            assertMixinType(colRefNode, StandardDdlLexicon.TYPE_COLUMN_REFERENCE);
        }
    }

    @Test
    public void shouldParseCreateTableIndexStatement() throws Exception {
        final String content = "CREATE TABLE CUST_MPAGE(\n" //$NON-NLS-1$
                                   + "MPAGE_ID INTEGER,\n" //$NON-NLS-1$
                                   + "NAME VARCHAR2(50 BYTE),\n" //$NON-NLS-1$
                                   + "DESCRIPTION VARCHAR2(200 BYTE)\n" //$NON-NLS-1$
                                   + ")\n" //$NON-NLS-1$
                                   + "LOGGING\n" //$NON-NLS-1$
                                   + "NOCOMPRESS\n" //$NON-NLS-1$
                                   + "NOCACHE\n" //$NON-NLS-1$
                                   + "NOPARALLEL\n" //$NON-NLS-1$
                                   + "MONITORING;\n" //$NON-NLS-1$
                                   + "" //$NON-NLS-1$
                                   + "CREATE INDEX CUST_MPAGE_PK ON CUST_MPAGE (MPAGE_ID) LOGGING NOPARALLEL;";
        this.parser.parse(content, this.rootNode, null);
        assertThat(this.rootNode.getChildCount(), is(2)); // table & index

        final List<AstNode> nodes = this.rootNode.childrenWithName("CUST_MPAGE_PK");
        assertThat(nodes.size(), is(1));

        final AstNode indexNode = nodes.get(0);
        assertMixinType(indexNode, OracleDdlLexicon.TYPE_CREATE_TABLE_INDEX_STATEMENT);
        assertProperty(indexNode, OracleDdlLexicon.INDEX_TYPE, OracleDdlConstants.IndexTypes.TABLE);
        assertProperty(indexNode, OracleDdlLexicon.TABLE_NAME, "CUST_MPAGE");
        assertProperty(indexNode, OracleDdlLexicon.UNIQUE_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.BITMAP_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.UNUSABLE_INDEX, false);
        assertThat(indexNode.getProperty(OracleDdlLexicon.TABLE_ALIAS), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.OTHER_INDEX_REFS), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.WHERE_CLAUSE), is(nullValue()));


        assertNull(indexNode.getProperty(OracleDdlLexicon.INDEX_ATTRIBUTES));
        assertEquals(Boolean.TRUE, indexNode.getProperty(INDEX_LOGGING));
        assertEquals("NOPARALLEL", indexNode.getProperty(INDEX_PARALLEL));

        // column reference
        assertThat(indexNode.getChildCount(), is(1));
        assertThat(indexNode.getFirstChild().getName(), is("MPAGE_ID"));
        assertMixinType(indexNode.getFirstChild(), StandardDdlLexicon.TYPE_COLUMN_REFERENCE);
    }

    @Test
    public void shouleParseCreateTableIndexWithFunctions() {
        final String content = "CREATE TABLE Weatherdata_tab(\n" //$NON-NLS-1$
                               + "Maxtemp INTEGER,\n" //$NON-NLS-1$
                               + "Mintemp INTEGER\n" //$NON-NLS-1$
                               + ");\n" //$NON-NLS-1$
                               + "" //$NON-NLS-1$
                               + "CREATE BITMAP INDEX Compare_index\n" //$NON-NLS-1$
                               + "ON Weatherdata_tab ((Maxtemp - Mintemp) DESC, Maxtemp);";
        this.parser.parse(content, this.rootNode, null);
        assertThat(this.rootNode.getChildCount(), is(2)); // table & index

        final List<AstNode> nodes = this.rootNode.childrenWithName("Compare_index");
        assertThat(nodes.size(), is(1));

        final AstNode indexNode = nodes.get(0);
        assertMixinType(indexNode, OracleDdlLexicon.TYPE_CREATE_TABLE_INDEX_STATEMENT);
        assertProperty(indexNode, OracleDdlLexicon.INDEX_TYPE, OracleDdlConstants.IndexTypes.TABLE);
        assertProperty(indexNode, OracleDdlLexicon.TABLE_NAME, "Weatherdata_tab");
        assertProperty(indexNode, OracleDdlLexicon.UNIQUE_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.BITMAP_INDEX, true);
        assertProperty(indexNode, OracleDdlLexicon.UNUSABLE_INDEX, false);
        assertThat(indexNode.getProperty(OracleDdlLexicon.TABLE_ALIAS), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.WHERE_CLAUSE), is(nullValue()));

        // functions
        @SuppressWarnings( "unchecked" )
        final List<String> functionRefs = (List<String>)indexNode.getProperty(OracleDdlLexicon.OTHER_INDEX_REFS);
        assertThat(functionRefs.size(), is(1));
        assertThat(functionRefs, hasItems("(Maxtemp-Mintemp) DESC")); // parsing takes out internal spaces in the function

        // column reference
        assertThat(indexNode.getChildCount(), is(1));
        assertThat(indexNode.getFirstChild().getName(), is("Maxtemp"));
        assertMixinType(indexNode.getFirstChild(), StandardDdlLexicon.TYPE_COLUMN_REFERENCE);
    }

    @Test
    public void shouldParseCreateTableWithColumnAtEnd() {
        // from the user model
        printTest("shouldParseCreateTableWithColumnAtEnd");
        String content = "CREATE TABLE TEST (" +
                " test CHAR(20) NOT NULL," +
                ");";
        assertScoreAndParse(content, null, 1); // "1" means no errors
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTableWithNoColumns() {
        // from the user model
        printTest("shouldParseCreateTableWithNoColumns");
        String content = "CREATE TABLE TEST (" +
                ");";
        assertScoreAndParse(content, null, 1); // "1" means no errors
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateClusterIndexStatement() throws Exception {
        final String content = "CREATE INDEX idx_personnel ON CLUSTER personnel;"; //$NON-NLS-1$
        this.parser.parse(content, this.rootNode, null);
        assertThat(this.rootNode.getChildCount(), is(1)); // index

        final List<AstNode> nodes = this.rootNode.childrenWithName("idx_personnel");
        assertThat(nodes.size(), is(1)); // table & index

        final AstNode indexNode = nodes.get(0);
        assertMixinType(indexNode, OracleDdlLexicon.TYPE_CREATE_CLUSTER_INDEX_STATEMENT);
        assertProperty(indexNode, OracleDdlLexicon.INDEX_TYPE, OracleDdlConstants.IndexTypes.CLUSTER);
        assertProperty(indexNode, OracleDdlLexicon.CLUSTER_NAME, "personnel");
        assertThat(indexNode.getProperty(OracleDdlLexicon.TABLE_NAME), is(nullValue()));
        assertProperty(indexNode, OracleDdlLexicon.UNIQUE_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.BITMAP_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.UNUSABLE_INDEX, false);
        assertThat(indexNode.getProperty(OracleDdlLexicon.TABLE_ALIAS), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.OTHER_INDEX_REFS), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.INDEX_ATTRIBUTES), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.WHERE_CLAUSE), is(nullValue()));
    }

    @Test
    public void shouldParseCreateBitmapJoinIndexStatement() throws Exception {
        final String content = "CREATE TABLE sales(\n" //$NON-NLS-1$
                               + "cust_id INTEGER,\n" //$NON-NLS-1$
                               + "cust_name VARCHAR2(50 BYTE)\n" //$NON-NLS-1$
                               + ");\n" //$NON-NLS-1$
                               + "" //$NON-NLS-1$
                               + "CREATE TABLE customers(\n" //$NON-NLS-1$
                               + "cust_id INTEGER,\n" //$NON-NLS-1$
                               + "cust_name VARCHAR2(50 BYTE)\n" //$NON-NLS-1$
                               + ");\n" //$NON-NLS-1$
                               + "" //$NON-NLS-1$
                               + "CREATE BITMAP INDEX sales_cust_gender_bjix\n" //$NON-NLS-1$
                               + "ON sales(customers.cust_gender)\n" //$NON-NLS-1$
                               + "FROM sales, customers\n" //$NON-NLS-1$
                               + "WHERE sales.cust_id = customers.cust_id\n" //$NON-NLS-1$
                               + "LOCAL;"; //$NON-NLS-1$
        this.parser.parse(content, this.rootNode, null);
        assertThat(this.rootNode.getChildCount(), is(3)); // 2 tables & index

        final List<AstNode> nodes = this.rootNode.childrenWithName("sales_cust_gender_bjix");
        assertThat(nodes.size(), is(1));

        final AstNode indexNode = nodes.get(0);
        assertMixinType(indexNode, OracleDdlLexicon.TYPE_CREATE_BITMAP_JOIN_INDEX_STATEMENT);
        assertProperty(indexNode, OracleDdlLexicon.INDEX_TYPE, OracleDdlConstants.IndexTypes.BITMAP_JOIN);
        assertProperty(indexNode, OracleDdlLexicon.TABLE_NAME, "sales");
        assertProperty(indexNode, OracleDdlLexicon.UNIQUE_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.BITMAP_INDEX, true);
        assertProperty(indexNode, OracleDdlLexicon.UNUSABLE_INDEX, false);
        assertProperty(indexNode, OracleDdlLexicon.WHERE_CLAUSE, "sales.cust_id = customers.cust_id LOCAL"); // index attributes
                                                                                                             // included
        assertThat(indexNode.getProperty(OracleDdlLexicon.INDEX_ATTRIBUTES), is(nullValue()));
        assertThat(indexNode.getProperty(OracleDdlLexicon.TABLE_ALIAS), is(nullValue()));
        assertThat(indexNode.getChildCount(), is(3)); // 1 column references, 2 table references

        { // 1 column reference
            final List<AstNode> colRefs = indexNode.getChildren(OracleDdlLexicon.TYPE_COLUMN_REFERENCE);
            assertThat(colRefs.size(), is(1));

            final AstNode colRefNode = colRefs.get(0);
            assertThat(colRefNode.getName(), is("customers.cust_gender"));
        }

        { // 2 table references
            final List<AstNode> tableRefs = indexNode.getChildren(OracleDdlLexicon.TYPE_TABLE_REFERENCE);
            assertThat(tableRefs.size(), is(2));

            { // sales table
                final AstNode tableRefNode = tableRefs.get(0);
                assertThat(tableRefNode.getName(), is("sales"));
            }

            { // customers table
                final AstNode tableRefNode = tableRefs.get(1);
                assertThat(tableRefNode.getName(), is("customers"));
            }
        }
    }
    
    @Test
    public void shouldParseDbObjectNameWithValidSymbols() {
        final String content = "CREATE TABLE EL$VIS (\n" //$NON-NLS-1$
                               + "COL_A VARCHAR2(20) NOT NULL,\n" //$NON-NLS-1$
                               + "COL@B VARCHAR2(10) NOT NULL,\n" //$NON-NLS-1$
                               + "COL#C NUMBER(10));"; //$NON-NLS-1$
        this.parser.parse(content, this.rootNode, null);
        assertThat(this.rootNode.getChildCount(), is(1));

        final AstNode tableNode = this.rootNode.getChildren().get(0);
        assertThat(tableNode.getName(), is("EL$VIS"));
        assertThat(tableNode.getChildCount(), is(3)); // 3 columns

        assertThat(tableNode.childrenWithName("COL_A").size(), is(1));
        assertThat(tableNode.childrenWithName("COL@B").size(), is(1));
        assertThat(tableNode.childrenWithName("COL#C").size(), is(1));
    }

    @Test
    public void shouldParseCreateIndexTest() {
        final String content = "  CREATE TABLE \"HAWAII_APPLICATION_T\"     (" +
        		"   \"PK_APPLICATION_ID\" VARCHAR2(75 CHAR)," +
        		"        \"APPLICANT_PERSON_ID\" VARCHAR2(75 CHAR)," +
        		"      \"SCREEN_NAME\" VARCHAR2(75 CHAR)," +
        		"      \"MASTER_CASE_ID\" VARCHAR2(75 CHAR)," +
        		"   \"MEDICAID_CASE_ID\" VARCHAR2(75 CHAR)," +
        		"         \"CASE_TYPE\" VARCHAR2(75 CHAR)," +
        		"        \"CONFIRMATION_NUMBER\" VARCHAR2(75 CHAR)," +
        		"      \"STATUS\" VARCHAR2(75 CHAR)," +
        		"   \"APPLICATION_DATA\" BLOB," +
        		"      \"CATEGORY\" VARCHAR2(75 CHAR)," +
        		"         \"CREATE_DATE\" TIMESTAMP (6)," +
        		"          \"MODIFIED_DATE\" TIMESTAMP (6)," +
        		"        \"CREATED_BY\" VARCHAR2(75 CHAR)," +
        		"       \"MODIFIED_BY\" VARCHAR2(75 CHAR)    );" +
        		"" +
        		"CREATE INDEX \"IX_1631FA9\" ON \"HAWAII_APPLICATION_T\" (\"APPLICANT_PERSON_ID\");";

        this.parser.parse(content, this.rootNode, null);
        assertThat(this.rootNode.getChildCount(), is(2));
    }
    
    @Test 
    public void shouldTestNumericWithPercentInsteadOfNumber() {
        
        final String content = "create table FOO(BAR NUMBER(%,%));";
        this.parser.parse(content, this.rootNode, null);
        
        final AstNode tableNode = this.rootNode.getChildren().get(0);
        assertThat(tableNode.getName(), is("FOO"));
        assertThat(tableNode.getChildCount(), is(1)); // 1 columns
        
        System.out.println(this.rootNode);
        assertThat(this.rootNode.getChildCount(), is(3));
        
    }
    
    @Test
    public void shouldParseCreateViewAndReturnOriginalQueryExpression() {
    	printTest("shouldParseCreateViewAndReturnOriginalQueryExpression");
    	String viewQuery = "SELECT \n" + 
    						"pua.user_profile_id, " + 
    						"au.user_profile_id IS NOT NULL as is_anonymous, \n" + 
    						"FROM poll_user_answer pua \n"+
    						"LEFT JOIN anonymous_user au ON au.user_profile_id = pua.user_profile_id";
    	
    	String content = "CREATE VIEW poll_view as " + viewQuery;
    	assertScoreAndParse(content, null, 1);
    	
    	AstNode viewNode = rootNode.getChildren().get(0);
    	String returnedQuery = viewNode.getProperty(CREATE_VIEW_QUERY_EXPRESSION).toString();
    	
    	assertEquals("poll_view", viewNode.getName());
    	assertEquals(replaceMultipleWhiteSpaces(viewQuery), replaceMultipleWhiteSpaces(returnedQuery));
    }

    // Indexy
    @Test
    public void shouldParseCreateIndexTableSpaceStorageAndPctFree() {
        String createIndexStmt = "CREATE INDEX emp_ename ON emp(ename)\n" +
                "      TABLESPACE users\n" +
                "      STORAGE (INITIAL 20k\n" +
                "      NEXT 20k\n" +
                "      PCTINCREASE 75)\n" +
                "      PCTFREE 0;";

        assertScoreAndParse(createIndexStmt, null, 1);

        AstNode createIndex = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(createIndex, TYPE_CREATE_TABLE_INDEX_STATEMENT));
        assertEquals("emp_ename", createIndex.getName());
        assertEquals("users", createIndex.getProperty(INDEX_TABLESPACE));
        assertEquals(Boolean.TRUE, createIndex.getProperty(STORAGE_CLAUSE));
        assertEquals("20k", createIndex.getProperty(STORAGE_INITIAL));
        assertEquals("20k", createIndex.getProperty(STORAGE_NEXT));
        assertEquals("75", createIndex.getProperty(STORAGE_PCTINCREASE));
        assertEquals("0", createIndex.getProperty(PHYSICAL_PCTFREE));
    }

    @Test
    public void shouldParseCreateIndexManyOptions() {
        String createIndexStmt = "CREATE INDEX idx ON emp(ename)\n" +
                "TABLESPACE tbls\n" +
                "PCTFREE 10\n" +
                "PCTUSED 20\n" +
                "INITRANS 30\n" +
                "STORAGE ( \n" +
                "    INITIAL 10K\n" +
                "    NEXT 20K\n" +
                "    MINEXTENTS 40\n" +
                "    MAXEXTENTS 45\n" +
                "    MAXSIZE UNLIMITED\n" +
                "    PCTINCREASE 50\n" +
                "    FREELISTS 60\n" +
                "    FREELIST GROUPS 70\n" +
                "    OPTIMAL NULL\n" +
                "    BUFFER_POOL KEEP    \n" +
                "    ENCRYPT \n" +
                ")\n" +
                "REVERSE\n" +
                "ONLINE\n" +
                "LOGGING\n" +
                "COMPUTE STATISTICS\n" +
                "NOSORT\n" +
                "COMPRESS 80\n" +
                "PARALLEL 90\n";
        assertScoreAndParse(createIndexStmt, null, 1);

        AstNode createIndex = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(createIndex, TYPE_CREATE_TABLE_INDEX_STATEMENT));
        assertEquals("idx", createIndex.getName());
        assertEquals("tbls", createIndex.getProperty(INDEX_TABLESPACE));
        assertEquals(true, createIndex.getProperty(INDEX_ONLINE));
        assertEquals(true, createIndex.getProperty(INDEX_LOGGING));
        assertEquals(false, createIndex.getProperty(INDEX_SORT));
        assertEquals("80", createIndex.getProperty(INDEX_COMPRESS));
        assertEquals("30", createIndex.getProperty(PHYSICAL_INITRANS));
        assertEquals("10", createIndex.getProperty(PHYSICAL_PCTFREE));
        assertEquals("20", createIndex.getProperty(PHYSICAL_PCTUSED));
        assertEquals("90", createIndex.getProperty(INDEX_PARALLEL));
        assertEquals(Boolean.TRUE, createIndex.getProperty(INDEX_REVERSE));
        assertEquals(Boolean.TRUE, createIndex.getProperty(INDEX_REVERSE));
        assertEquals(Boolean.TRUE, createIndex.getProperty(STORAGE_CLAUSE));
        assertEquals("10K", createIndex.getProperty(STORAGE_INITIAL));
        assertEquals("20K", createIndex.getProperty(STORAGE_NEXT));
        assertEquals("50", createIndex.getProperty(STORAGE_PCTINCREASE));
        assertEquals(Boolean.TRUE, createIndex.getProperty(STORAGE_ENCRYPT));
        assertEquals("KEEP", createIndex.getProperty(STORAGE_BUFFER_POOL));
        assertEquals(Boolean.TRUE, createIndex.getProperty(STORAGE_MAXSIZE_UNLIMITED));
        assertEquals("NULL", createIndex.getProperty(STORAGE_OPTIMAL));
        assertEquals("70", createIndex.getProperty(STORAGE_FREELIST_GROUPS));
        assertEquals("60", createIndex.getProperty(STORAGE_FREELISTS));
        assertEquals("40", createIndex.getProperty(STORAGE_MINEXTENTS));
        assertEquals("45", createIndex.getProperty(STORAGE_MAXEXTENTS));
    }

    @Test
    public void testParseCreateTableEncryptAndSort() {
        String createTableStmt = "CREATE TABLE employee (\n" +
                "     first_name VARCHAR2(128),\n" +
                "     last_name VARCHAR2(128),\n" +
                "     empID NUMBER ENCRYPT NO SALT,\n" +
                "     salary NUMBER(6) ENCRYPT USING '3DES168'\n" +
                ");";
        assertScoreAndParse(createTableStmt, null, 1);

        AstNode createTable = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(createTable, TYPE_CREATE_TABLE_STATEMENT));
        assertEquals("employee", createTable.getName());
        assertEquals(4, createTable.getChildCount());
        AstNode empIdColumn = createTable.childrenWithName("empID").get(0);
        assertEquals("ENCRYPT", empIdColumn.getProperty(COLUMN_ENCRYPT));
        assertEquals("NO SALT", empIdColumn.getProperty(COLUMN_SALT));
        AstNode salaryColumn = createTable.childrenWithName("salary").get(0);
        assertEquals("ENCRYPT", salaryColumn.getProperty(COLUMN_ENCRYPT));
        assertEquals("'3DES168'", salaryColumn.getProperty(COLUMN_USING_ENCRYPT_ALGORITHM));

    }

    @Test
    public void testParseCreateTableWithOrganization() {
        String createTableStmt = "CREATE TABLE locations\n" +
                "(id           NUMBER(10),\n" +
                " description  VARCHAR2(50)  NOT NULL,\n" +
                " map          BLOB,\n" +
                " CONSTRAINT pk_locations PRIMARY KEY (id)\n" +
                ")\n" +
                "ORGANIZATION INDEX \n" +
                "TABLESPACE iot_tablespace\n" +
                "PCTTHRESHOLD 20";
        assertScoreAndParse(createTableStmt, null, 1);

        AstNode createTable = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(createTable, TYPE_CREATE_TABLE_STATEMENT));
        assertEquals("locations", createTable.getName());
        assertEquals(5, createTable.getChildCount());
        assertEquals("iot_tablespace", createTable.getProperty(TABLE_TABLESPACE));
        assertEquals("INDEX",  createTable.getProperty(TABLE_ORGANIZATION));
    }

    @Test
    public void testParseAlterTableReference() {
        String alterStmt = "-- Created by Vertabelo (http://vertabelo.com)\n" +
                "-- Last modification date: 2021-01-25 11:16:31.794\n" +
                "-- foreign keys\n" +
                "-- Reference: seat_car (table: seat)\n" +
                "ALTER TABLE seat ADD CONSTRAINT seat_car\n" +
                "   FOREIGN KEY (car_id)\n" +
                "   REFERENCES car (id)\n" +
                "   NOT DEFERRABLE\n" +
                "    INITIALLY DEFERRED\n" +
                "    NO RELY\n" +
                "    ENABLE\n" +
                "    VALIDATE\n" +
                "    USING INDEX test;\n" +
                "-- End of file.\n";

        assertScoreAndParse(alterStmt, null, 1);

        AstNode alterTable = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(alterTable, TYPE_ALTER_TABLE_STATEMENT));
        assertEquals("seat", alterTable.getName());
        assertEquals(1, alterTable.getChildCount());
        AstNode constraintNode = alterTable.getChild(0);
        assertEquals("seat_car", constraintNode.getName());
        assertEquals("NO RELY", constraintNode.getProperty(OracleDdlLexicon.REFERENCE_RELY));
        assertEquals("VALIDATE", constraintNode.getProperty(OracleDdlLexicon.REFERENCE_VALIDATE));
        assertEquals("test",  constraintNode.getProperty(OracleDdlLexicon.REFERENCE_USING_INDEX));
    }
    
    private static String replaceMultipleWhiteSpaces(String a) {
    	return a.replaceAll("\\s+", " ").trim();
    }
}
