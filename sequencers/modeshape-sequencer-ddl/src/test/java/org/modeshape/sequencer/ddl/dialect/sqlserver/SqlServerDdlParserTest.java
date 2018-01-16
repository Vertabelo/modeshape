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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_REFERENCE;
import static org.modeshape.sequencer.ddl.dialect.sqlserver.SqlServerDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.sqlserver.SqlServerDdlLexicon.TYPE_CREATE_INDEX_STATEMENT;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.common.FixFor;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.dialect.sqlite.SqliteDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;

public class SqlServerDdlParserTest extends DdlParserTestHelper {
    @SuppressWarnings("hiding")
    private static final String SPACE = DdlConstants.SPACE;

    public static final String DDL_FILE_PATH = "ddl/dialect/sqlserver/";

    @Before
    public void beforeEach() {
        parser = new SqlServerDdlParser();
//        setPrintToConsole(true);
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
    public void shouldParseCreateTable_varchar_max() {
        printTest("shouldParseCreateTable_varchar_max()");
        String content = "CREATE TABLE client (" +
                        "    id int  NOT NULL," +
                        "    full_name varchar(max)  NOT NULL," +
                        "    qq char varying(max)  NOT NULL," +
                        "    ww character varying(max)  NOT NULL," +
                        "    ee nvarchar(max)  NOT NULL," +
                        "    rr national char varying(max)  NOT NULL," +
                        "    tt national character varying(max)  NOT NULL," +
                        "    yy varbinary(max)  NOT NULL," +
                        "    uu binary varying(max)  NOT NULL," +
                        "    num_full_name varchar(5)  NOT NULL," +
                        "    num_qq char varying(5)  NOT NULL," +
                        "    num_ww character varying(5)  NOT NULL," +
                        "    num_ee nvarchar(5)  NOT NULL," +
                        "    num_rr national char varying(5)  NOT NULL," +
                        "    num_tt national character varying(5)  NOT NULL," +
                        "    num_yy varbinary(5)  NOT NULL," +
                        "    num_uu binary varying(5)  NOT NULL," +
                        "    no_size_full_name varchar  NOT NULL," +
                        "    no_size_qq char varying  NOT NULL," +
                        "    no_size_ww character varying  NOT NULL," +
                        "    no_size_ee nvarchar  NOT NULL," +
                        "    no_size_rr national char varying  NOT NULL," +
                        "    no_size_tt national character varying  NOT NULL," +
                        "    no_size_yy varbinary  NOT NULL," +
                        "    no_size_uu binary varying  NOT NULL," +
                        "    PRIMARY KEY (id)" +
                        ");";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_9() {
        // zgłoszone przez klienta
        printTest("shouldParseCreateTable_9");
        String content = "CREATE TABLE [dbo].[tblApp](" +
                "    [AppID] [int] IDENTITY(1,1) NOT NULL," +
                "    [AppName] [varchar](50) NOT NULL," +
                "    [DeveloperID] [int] NOT NULL," +
                "    [CreatedOn] [datetime] NOT NULL," +
                "    [CreatedBy] [int] NOT NULL," +
                "    [UpdatedOn] [datetime] NOT NULL," +
                "    [UpdatedBy] [int] NOT NULL," +
                "    CONSTRAINT [PK_tblApp] PRIMARY KEY CLUSTERED" +
                "    (" +
                "    [AppID] ASC" +
                "    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]" +
                "    ) ON [PRIMARY]";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_10() {
        // zgłoszone przez klienta
        printTest("shouldParseCreateTable_10");
        String content = "CREATE TABLE [tbl_Search_Criteria](" +
        		"  [Search_Criteria_ID] [int]NOT NULL IDENTITY  ," +
        		"  [Payer_ID] [int] NULL," +
        		"  [Search_Criteria] [varchar](500) NULL," +
        		"  [VerificationType] [char](1) NULL," +
        		"  [IsDefault] [bit] NULL," +
        		"  [Created_On] [smalldatetime] NOT NULL default getdate()," +
        		"  [Created_By] [varchar](50) NOT NULL," +
        		"  [Updated_On] [smalldatetime] NULL," +
        		"  [Updated_By] [varchar](50) NULL," +
        		"  CONSTRAINT [PK_tbl_SearchCriteria_New] PRIMARY KEY ( [Search_Criteria_ID] ) )";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_11() {
        // z modelu klienta
        printTest("shouldParseCreateTable_11");
        String content = "CREATE TABLE [bap].[proje] (" +
        		"     [id] int NOT NULL, " +
        		"     [baska_kurum_destegi_durum] bit NULL DEFAULT ((0)) )";
        assertScoreAndParse(content, null, 1); // 1 oznacza brak błędów
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_12() {
        // z modelu klienta
        printTest("shouldParseCreateTable_12");
        String content = "CREATE TABLE [dbo].[ESTUDIANTES](" +
                        "   [EST_AUTO] [nchar](10) NULL," +
                        "   [EST_AUTO_2] [national char](10) NULL," +
                        "   [EST_AUTO_3] [national character](10) NULL" +
                        ") ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]";
        assertScoreAndParse(content, null, 1); // 1 oznacza brak błędów
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_13() {
        // z modelu klienta
        printTest("shouldParseCreateTable_13");
        String content = "CREATE TABLE [dbo].[INSCRIPCION DETALLE](" +
                        "   [IND_CODIGO] [int] NOT NULL" +
                        ")";
        assertScoreAndParse(content, null, 1); // 1 oznacza brak błędów
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTableWithColumnAtEnd() {
        // z modelu klienta
        printTest("shouldParseCreateTableWithColumnAtEnd");
        String content = "CREATE TABLE TEST (" +
                " test CHAR(20) NOT NULL," +
                ");";
        assertScoreAndParse(content, null, 1); // 1 oznacza brak błędów
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTableWithNoColumns() {
        // z modelu klienta
        printTest("shouldParseCreateTableWithNoColumns");
        String content = "CREATE TABLE TEST (" +
                ");";
        assertScoreAndParse(content, null, 1); // 1 oznacza brak błędów
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

    @Test
    public void shouldParseAlterTable_2() {
        printTest("shouldParseAlterTable_2()");
        String content = "ALTER TABLE [dbo].[tblApp] ADD CONSTRAINT [DF_tblApp_AppName] DEFAULT (space((0))) FOR [AppName]";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseAlterTable_3() {
        printTest("shouldParseAlterTable_3()");
        String content = "ALTER TABLE [dbo].[tblApp] ADD CONSTRAINT [DF_tblApp_DeveloperID] DEFAULT ((0)) FOR [DeveloperID]";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseAlterTable_4() {
        printTest("shouldParseAlterTable_4()");
        String content = "ALTER TABLE [dbo].[tblApp] ADD CONSTRAINT [DF_tblApp_CreatedOn] DEFAULT (getdate()) FOR [CreatedOn]";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT));
    }
    
    @Test
    public void shouldParseExecuteProcedure() {
        String content = "EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'BGN02' , " 
                    + "@level0type=N'SCHEMA',@level0name=N'dbo', " 
                    + "@level1type=N'TABLE',@level1name=N'DHCS834Inbound', @level2type=N'COLUMN',@level2name=N'BGN02'";
        
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        
        assertTrue(hasMixinType(childNode, SqlServerDdlLexicon.TYPE_EXECUTE_STATEMENT));
        assertTrue("sys.sp_addextendedproperty".equals(childNode.getName()));
        
        assertEquals(8, childNode.getChildren().size());
        
        AstNode parameter1Node = childNode.getChildren().get(0);
        assertTrue(hasMixinType(parameter1Node, SqlServerDdlLexicon.TYPE_EXECUTE_PARAMETER));
        assertEquals("@name", parameter1Node.getProperty(SqlServerDdlLexicon.PARAMETER));
        assertEquals("N'MS_Description'", parameter1Node.getProperty(SqlServerDdlLexicon.VALUE));
    }
    
    @Test
    public void shouldParseExecuteProcedure3() {
        String content = "EXEC sp_addextendedproperty "
                + "@name  = N'MS_Description', "
                + "@value = N'Product description', "
                + "@level0type = N'SCHEMA', "
                + "@level0name = N'dbo', "
                + "@level1type = N'TABLE', "
                + "@level1name = N'product'";
        
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        
        assertTrue(hasMixinType(childNode, SqlServerDdlLexicon.TYPE_EXECUTE_STATEMENT));
        assertTrue("sp_addextendedproperty".equals(childNode.getName()));
        
        assertEquals(6, childNode.getChildren().size());
        
        AstNode parameter1Node = childNode.getChildren().get(0);
        assertTrue(hasMixinType(parameter1Node, SqlServerDdlLexicon.TYPE_EXECUTE_PARAMETER));
        assertEquals("@name", parameter1Node.getProperty(SqlServerDdlLexicon.PARAMETER));
        assertEquals("N'MS_Description'", parameter1Node.getProperty(SqlServerDdlLexicon.VALUE));
        
        AstNode parameter2Node = childNode.getChildren().get(1);
        assertTrue(hasMixinType(parameter2Node, SqlServerDdlLexicon.TYPE_EXECUTE_PARAMETER));
        assertEquals("@value", parameter2Node.getProperty(SqlServerDdlLexicon.PARAMETER));
        assertEquals("N'Product description'", parameter2Node.getProperty(SqlServerDdlLexicon.VALUE));
    }
    
    @Test
    public void shouldParseExecuteProcedure2() {
        String content = "EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'BGN02' , " 
                    + "@level0type=N'SCHEMA',@level0name=N'dbo', " 
                    + "@level1type=N'TABLE',@level1name=N'DHCS834Inbound', @level2type=N'COLUMN',@level2name=N'BGN02'"
                    + " WITH RECOMPILE RESULT SET UNDEFINED ";
        
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        
        assertTrue(hasMixinType(childNode, SqlServerDdlLexicon.TYPE_EXECUTE_STATEMENT));
        assertTrue("sys.sp_addextendedproperty".equals(childNode.getName()));
        
        assertEquals(8, childNode.getChildren().size());
        
        AstNode parameter1Node = childNode.getChildren().get(0);
        assertTrue(hasMixinType(parameter1Node, SqlServerDdlLexicon.TYPE_EXECUTE_PARAMETER));
        assertEquals("@name", parameter1Node.getProperty(SqlServerDdlLexicon.PARAMETER));
        assertEquals("N'MS_Description'", parameter1Node.getProperty(SqlServerDdlLexicon.VALUE));
    }
    
    @Test
    public void shouldParseExecute() {
        String content = "EXEC (@variable) AS login = 'user'; "; 
        
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, SqlServerDdlLexicon.TYPE_EXECUTE_STATEMENT));
    }

    @Test
    public void shouldParseCreateIndex() {
        printTest("shouldParseCreateIndex()");
        String content = "CREATE TABLE [bap].[proje_anahtar_kelime] (" +
        		"    [id] int NOT NULL," +
        		"    [proje_id] int NULL," +
        		"    [anahtarkelime_id] int NULL); " +
        		"" +
        		"CREATE INDEX [proje_idx] " +
        		"    ON [bap].[proje_anahtar_kelime] ([proje_id] ASC) " +
        		"    WITH (STATISTICS_NORECOMPUTE = ON);";
        
        assertScoreAndParse(content, null, 2);
        
        // check create table
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
        
        // check create index
        childNode = rootNode.getChildren().get(1);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_INDEX_STATEMENT));
        
        assertEquals(1, childNode.getChildren().size());
        AstNode indexChildNode = childNode.getChildren().get(0);
        assertTrue(hasMixinType(indexChildNode, TYPE_COLUMN_REFERENCE));
        assertEquals("proje_id", indexChildNode.getName());
    }
    
    @Test
    public void shouldParseIdentity() {
        String content = "CREATE TABLE t (ID int IDENTITY(1,2));"; 
        
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT));
        
        AstNode columnNode = childNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, StandardDdlLexicon.TYPE_COLUMN_DEFINITION));
        assertNotNull(columnNode.getProperty(SqlServerDdlLexicon.COLUMN_IDENTITY));
        assertEquals("1", columnNode.getProperty(SqlServerDdlLexicon.COLUMN_IDENTITY_SEED));
        assertEquals("2", columnNode.getProperty(SqlServerDdlLexicon.COLUMN_IDENTITY_INCREMENT));
    }
    
    @Test
    public void shouldParseIdentity2() {
        String content = "CREATE TABLE t (ID int IDENTITY);"; 
        
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT));
        
        AstNode columnNode = childNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, StandardDdlLexicon.TYPE_COLUMN_DEFINITION));
        assertNotNull(columnNode.getProperty(SqlServerDdlLexicon.COLUMN_IDENTITY));
    }


    @Test
    public void shoudParseWithDot() {
        String content = "CREATE TABLE [dbo].[Blade]("
        + "[Id] [uniqueidentifier] NOT NULL CONSTRAINT [DF_dbo.Blade_Id]  DEFAULT (newsequentialid()),"
        + "[Position] [nvarchar](max) NULL,"
        + "[SerialNumber] [nvarchar](max) NULL,"
        + "[NumberConfidence] [nvarchar](max) NULL,"
        + "[Length] [decimal](25, 10) NULL,"
        + "[Width] [decimal](25, 10) NULL,"
        + "[Manufacturer] [nvarchar](max) NULL,"
        + "[Type] [nvarchar](max) NULL,"
        + "[TurbineId] [uniqueidentifier] NOT NULL DEFAULT ('00000000-0000-0000-0000-000000000000'),"
        + "        CONSTRAINT [PK_dbo.Blade] PRIMARY KEY CLUSTERED"
        + "        ("
        + "        [Id] ASC"
        + ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]"
        + ") ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]"
        + ");";
        assertScoreAndParse(content, null, 1);
        AstNode createTableName = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(createTableName, StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT));
        assertEquals(10, createTableName.getChildCount());
        AstNode pk = createTableName.getChildren().get(9);
        assertTrue(hasMixinType(pk, StandardDdlLexicon.TYPE_TABLE_CONSTRAINT));
    }


    @Test
    public void shoudParseWithTwoDots() {
        String content = "CREATE TABLE [dbo].[Inspection](\n" +
                "\t[Id] [uniqueidentifier] NOT NULL CONSTRAINT [DF_dbo.Inspection_Id]  DEFAULT (newsequentialid()),\n" +
                /*"\t[Inspectors] [nvarchar](max) NULL,\n" +
                "\t[Company] [nvarchar](max) NULL,\n" +
                "\t[Type] [int] NULL,\n" +
                "\t[Date] [datetime2](7) NULL,\n" +
                "\t[IsDeleted] [bit] NOT NULL DEFAULT ((0)),\n" +*/
                " CONSTRAINT [PK_dbo.Inspection] PRIMARY KEY CLUSTERED \n" +
                "(\n" +
                "\t[Id] ASC\n" +
                ")WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]\n" +
                ") ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]";
        assertScoreAndParse(content, null, 1);
        AstNode createTableName = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(createTableName, StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void alterTableConstraintWithDots() {
        String content = "ALTER TABLE [dbo].[Blade]  WITH CHECK ADD  CONSTRAINT [FK_dbo.Blade_dbo.Node_Id] FOREIGN KEY([Id])\n" +
                "REFERENCES [dbo].[Node] ([Id])";
        assertScoreAndParse(content, null, 1);
        AstNode createTableName = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(createTableName, StandardDdlLexicon.TYPE_ALTER_TABLE_STATEMENT));
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
    
    
    private static String replaceMultipleWhiteSpaces(String a) {
    	return a.replaceAll("\\s+", " ").trim();
    }
    
}
