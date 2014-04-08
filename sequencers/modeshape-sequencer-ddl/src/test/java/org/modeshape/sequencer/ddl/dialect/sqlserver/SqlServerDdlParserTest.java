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
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.modeshape.sequencer.ddl.dialect.sqlserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.sqlserver.SqlServerDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

public class SqlServerDdlParserTest extends DdlParserTestHelper {
    private static final String SPACE = DdlConstants.SPACE;

    public static final String DDL_FILE_PATH = "ddl/dialect/sqlserver/";

    @Before
    public void beforeEach() {
        parser = new SqlServerDdlParser();
        setPrintToConsole(true);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }
    

    // CREATE TABLE films (
    // code char(5) CONSTRAINT firstkey PRIMARY KEY,
    // title varchar(40) NOT NULL,
    // did integer NOT NULL,
    // date_prod date,
    // kind varchar(10),
    // len interval hour to minute
    // );

    @Test
    public void shouldParseCreateTable_1() {
        printTest("shouldParseCreateTable_1()");
        String content = "CREATE TABLE films (" + SPACE + "code        char(5) CONSTRAINT firstkey PRIMARY KEY," + SPACE
                         + "title       varchar(40) NOT NULL," + SPACE + "did         integer NOT NULL," + SPACE
                         + "date_prod   date," + SPACE + "kind        varchar(10)," + SPACE
                         + "len         interval hour to minute" + SPACE + ");";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    // CREATE TABLE distributors (
    // did integer PRIMARY KEY DEFAULT nextval(’serial’),
    // name varchar(40) NOT NULL CHECK (name <> ”)
    //               
    // );

    @Test
    public void shouldParseCreateTable_2() {
        printTest("shouldParseCreateTable_2()");
        String content = "CREATE TABLE distributors (" + SPACE + "did    integer PRIMARY KEY DEFAULT 123," + SPACE
                         + "name      varchar(40) NOT NULL CHECK (name <> ”)" + SPACE + ");";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    // CREATE TABLE distributors (
    // name varchar(40) DEFAULT ’Luso Films’,
    // did integer DEFAULT nextval(’distributors_serial’),
    // modtime timestamp DEFAULT current_timestamp
    // );

    @Test
    public void shouldParseCreateTable_3() {
        printTest("shouldParseCreateTable_3()");
        String content = "CREATE TABLE distributors (" + SPACE + "name      varchar(40) DEFAULT 'xxxx yyyy'," + SPACE
                         + "name      varchar(40) DEFAULT ’Luso Films’," + SPACE
                         + "did       integer DEFAULT 123," + SPACE
                         + "modtime   timestamp" + SPACE + ");";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    // CREATE TABLE films_recent AS
    // SELECT * FROM films WHERE date_prod >= ’2002-01-01’;

    @Test
    public void shouldParseCreateTable_4() {
        printTest("shouldParseCreateTable_4()");
        String content = "CREATE TABLE films_recent AS" + SPACE + "SELECT * FROM films WHERE date_prod >= ’2002-01-01’;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }
    
    

    //CREATE TABLE products (
    //    id int  NOT NULL,
    //    name varchar(100)  NOT NULL,
    //    status varchar(12)  NOT NULL,
    //    aliasid int  NULL,
    //    producttypeid int  NOT NULL,
    //    CONSTRAINT XAK1Products UNIQUE (name),
    //    CONSTRAINT VRProductStatuses_Products CHECK (([Status]='Live' OR [Status]='NotLive' OR [Status]='Discontinued')),
    //    CONSTRAINT CC_AliasId CHECK (([AliasId]>(0) AND [AliasId]<(100000))),
    //    PRIMARY KEY (productid)
    //)
    //;

    @Test
    public void shouldParseCreateTable_5() {
        printTest("shouldParseCreateTable_5()");
        String content = "CREATE TABLE films_recent AS" + SPACE + "SELECT * FROM films WHERE date_prod >= ’2002-01-01’;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_6() {
        printTest("shouldParseCreateTable_6()");
        String content = "CREATE TABLE t6_ak (" +
        		"    id int  NOT NULL," +
        		"    ak1_a int  NOT NULL," +
        		"    ak1_b int  NOT NULL," +
        		"    ak2_a int  NOT NULL," +
        		"    ak2_b int  NOT NULL," +
        		"    CONSTRAINT t6_ak_1 UNIQUE CLUSTERED (ak1_a, ak1_b) WITH (PAD_INDEX=ON) ON 'default'," +
        		"    CONSTRAINT t6_ak_2 UNIQUE NONCLUSTERED (ak2_b, ak2_a)," +
        		"    PRIMARY KEY (id));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_7() {
        printTest("shouldParseCreateTable_7()");
        String content = "CREATE TABLE t7 (" +
                        "    id int  NOT NULL IDENTITY," +
                        "    small smallint  SPARSE NULL," +
                        "    txt varchar(50) COLLATE collation_name SPARSE NOT NULL," +
                        "    PRIMARY KEY (id));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_8() {
        printTest("shouldParseCreateTable_8()");
        String content = "CREATE TABLE DocumentStoreWithColumnSet" +
        		"    (DocID int PRIMARY KEY," +
        		"     Title varchar(200) NOT NULL," +
        		"     ProductionSpecification varchar(20) SPARSE NULL," +
        		"     ProductionLocation smallint SPARSE NULL," +
        		"     MarketingSurveyGroup varchar(20) SPARSE NULL," +
        		"     MarketingProgramID int SPARSE NULL," +
        		"     SpecialPurposeColumns XML COLUMN_SET FOR ALL_SPARSE_COLUMNS);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }
    

    @Test
    public void shouldParseCreateMisc() {
        printTest("shouldParseCreateMisc()");
        String content = "CREATE ASYMMETRIC KEY Asym_Key_Name;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_STATEMENT));
    }


    @Test
    public void shouldParseCreateSequence_1() {
        printTest("shouldParseCreateSequence_1()");
        String content = "CREATE SEQUENCE seq1" +
        		"    NO MINVALUE" +
        		"    NO MAXVALUE" +
        		"    NO CYCLE" +
        		"    NO CACHE;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_SEQUENCE_STATEMENT));
    }

    @Test
    public void shouldParseCreateSequence_2() {
        printTest("shouldParseCreateSequence_2()");
        String content = "CREATE SEQUENCE seq2" +
        		"    START WITH 1" +
        		"    INCREMENT BY 1" +
        		"    NO MINVALUE" +
        		"    NO MAXVALUE" +
        		"    NO CYCLE" +
        		"    NO CACHE;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_SEQUENCE_STATEMENT));
    }

    @Test
    public void shouldParseCreateSequence_3() {
        printTest("shouldParseCreateSequence_3()");
        String content = "CREATE SEQUENCE dbo.seq3" +
        		"    AS bigint" +
        		"    START WITH 10" +
        		"    INCREMENT BY 2" +
        		"    MINVALUE 10" +
        		"    MAXVALUE 10000" +
        		"    CYCLE" +
        		"    CACHE 10;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_SEQUENCE_STATEMENT));
    }

    @Test
    public void shouldParseCreateSequence_4() {
        printTest("shouldParseCreateSequence_4()");
        String content = "CREATE SEQUENCE seq4;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_SEQUENCE_STATEMENT));
    }

    @Test
    public void shouldParseCreateView_1() {
        printTest("shouldParseCreateView_1()");
        String content = "CREATE VIEW one_view" +
        		" AS" +
        		" select 1 as one;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_VIEW_STATEMENT));
    }

    @Test
    public void shouldParseCreateView_2() {
        printTest("shouldParseCreateView_2()");
        String content = "CREATE VIEW dbo.v_view " +
        		" WITH SCHEMABINDING, VIEW_METADATA, ENCRYPTION" +
        		" AS" +
        		" select  1 as one,  2 as two " +
        		" WITH   CHECK    option;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_VIEW_STATEMENT));
    }

    @Test
    public void shouldParseAlterTable_1() {
        printTest("shouldParseAlterTable_1()");
        String content = "ALTER TABLE t2" +
        		" ADD CONSTRAINT t2_t1" +
        		"     FOREIGN KEY (t1_id)" +
        		"     REFERENCES t1 (id)" +
        		"     ON DELETE  SET NULL" +
        		"     ON UPDATE  CASCADE" +
        		"     NOT FOR REPLICATION;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
    }
    
}
