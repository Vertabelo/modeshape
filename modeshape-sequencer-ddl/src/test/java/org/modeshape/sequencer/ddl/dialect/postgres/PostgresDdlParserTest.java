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
package org.modeshape.sequencer.ddl.dialect.postgres;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_SCHEMA_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_DROP_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_ALTER_FOREIGN_DATA_WRAPPER_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_ALTER_TABLE_STATEMENT_POSTGRES;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_COMMENT_ON_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_CREATE_RULE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_CREATE_SEQUENCE_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_GRANT_ON_FUNCTION_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_LISTEN_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_RENAME_COLUMN;
import static org.modeshape.sequencer.ddl.dialect.postgres.PostgresDdlLexicon.TYPE_COLUMN_DEFINITION;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.common.FixFor;
import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.dialect.postgres10plus.Postgres10PlusDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;

import java.io.InputStream;
import java.util.Scanner;

/**
 *
 */
public class PostgresDdlParserTest extends DdlParserTestHelper {
    private static final String SPACE = DdlConstants.SPACE;

    public static final String DDL_FILE_PATH = "ddl/dialect/postgres/";

    @Before
    public void beforeEach() {
        parser = new PostgresDdlParser();
        setPrintToConsole(false);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }

    @Test
    public void shouldParseAlterTableMultipleAddColumns() {
        printTest("shouldParseAlterTableMultipleAddColumns()");
        String content = "ALTER TABLE distributors \n" + "        ADD COLUMN nick_name varchar(30), \n"
                         + "        ADD COLUMN address varchar(30);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));

        assertEquals(2, childNode.getChildCount());

    }

    @Test
    public void shouldParseAlterTableMultipleMixedActions() {
        printTest("shouldParseAlterTableMultipleAddColumns()");
        String content = "ALTER TABLE distributors \n" + "        ADD COLUMN nick_name varchar(30), \n"
                         + "        ALTER COLUMN address TYPE varchar(255), \n" + "        RENAME COLUMN address TO city, \n"
                         + "        DROP COLUMN address;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));

        assertEquals(4, childNode.getChildCount());
        assertTrue(hasMixinType(childNode.getChild(2), TYPE_RENAME_COLUMN));
        assertTrue(hasMixinType(childNode.getChild(3), TYPE_DROP_COLUMN_DEFINITION));

    }

    @Test
    public void shouldParseAlterTableMultipleAlterColumns() {
        printTest("shouldParseAlterTableMultipleAlterColumns()");
        String content = "ALTER TABLE distributors ALTER COLUMN address TYPE varchar(80), ALTER COLUMN name TYPE varchar(100);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));

    }

    @Test
    public void shouldParseAlterTableMultipeAlterColumns_2() {
        printTest("shouldParseAlterTableMultipeAlterColumns_2()");
        String content = "ALTER TABLE foo" + SPACE + "ALTER COLUMN foo_timestamp DROP DEFAULT," + SPACE
                         + "ALTER COLUMN foo_timestamp TYPE timestamp with time zone" + SPACE
                         + "USING timestamp with time zone ’epoch’ + foo_timestamp *GO interval ’1 second’," + SPACE
                         + "ALTER COLUMN foo_timestamp SET DEFAULT now();";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));
    }

    @Test
    public void shouldParseAlterTableMultipeColumns_3() {
        printTest("shouldParseAlterTableMultipeColumns_3()");
        String content = "ALTER TABLE foo" + SPACE + "ALTER COLUMN foo_timestamp SET DATA TYPE timestamp with time zone" + SPACE
                         + "USING timestamp with time zone ’epoch’ + foo_timestamp * interval ’1 second’;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));
    }

    @Test
    public void shouldParseAlterTableMultipeColumns_4() {
        printTest("shouldParseAlterTableMultipeColumns_4()");
        String content = "ALTER TABLE distributors ALTER COLUMN street DROP NOT NULL;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));
    }

    @Test
    public void shouldParseAlterTableMultipeColumns_5() {
        printTest("shouldParseAlterTableMultipeColumns_5()");
        String content = "ALTER TABLE distributors ADD CONSTRAINT zipchk CHECK (char_length(zipcode) = 5);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));
    }

    @Test
    public void shouldParseAlterTableMultipeColumns_6() {
        printTest("shouldParseAlterTableMultipeColumns_6()");
        String content = "ALTER TABLE distributors SET TABLESPACE fasttablespace;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_TABLE_STATEMENT_POSTGRES));
    }

    @Test
    public void shouldParseCreateSchema() {
        printTest("shouldParseCreateSchema()");
        String content = "CREATE SCHEMA hollywood" + SPACE + "CREATE TABLE films (title text, release date, awards text[])"
                         + SPACE + "CREATE VIEW winners AS SELECT title, release FROM films WHERE awards IS NOT NULL;";
        assertScoreAndParse(content, null, 1); // schema
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_SCHEMA_STATEMENT));
    }

    @Test
    public void shouldParseCreateSequence() {
        printTest("shouldParseCreateSequence()");
        String content = "CREATE SEQUENCE serial START 101;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_SEQUENCE_STATEMENT));
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
        String content = "CREATE TABLE distributors (" + SPACE + "did    integer PRIMARY KEY DEFAULT nextval(’serial’)," + SPACE
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
                         + "did       integer DEFAULT nextval(’distributors_serial’)," + SPACE
                         + "modtime   timestamp DEFAULT current_timestamp" + SPACE + ");";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_DEFAULT_VALUE_0() {
        String content = "CREATE TABLE foo (name varchar(40));";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));

        assertEquals(null, columnNode.getProperty(Postgres10PlusDdlLexicon.DEFAULT_VALUE));
    }

    @Test
    public void shouldParseCreateTable_DEFAULT_VALUE_1() {
        String content = "CREATE TABLE foo (name varchar(40) DEFAULT bar());";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));

        assertEquals("bar()", columnNode.getProperty(Postgres10PlusDdlLexicon.DEFAULT_VALUE));
    }



    @Test
    public void shouldParseCreateTable_DEFAULT_VALUE_2() {
        String content = "CREATE TABLE foo (name varchar(40) DEFAULT timezone('utc'::text, now()));";
        assertScoreAndParse(content, null, 1);
        AstNode tableNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(tableNode, TYPE_CREATE_TABLE_STATEMENT));

        AstNode columnNode = tableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));

        // FIXME SPACJE
        assertEquals("timezone( 'utc' :: text , now ( ))", columnNode.getProperty(Postgres10PlusDdlLexicon.DEFAULT_VALUE));
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

    @Test
    public void shouldParseCreateTable_5() {
        printTest("shouldParseCreateTable_5()");
        String content = "CREATE TABLE assets (" +
        		"    id integer NOT NULL," +
        		"    name text NOT NULL," +
        		"    display_name text NOT NULL," +
        		"    category_id integer," +
        		"    created_at timestamp without time zone NOT NULL," +
        		"    updated_at timestamp without time zone NOT NULL," +
        		"    last_seen_at timestamp without time zone," +
        		"    bytes_in bigint DEFAULT 0," +
        		"    bytes_out bigint DEFAULT 0," +
        		"    connection_attempts integer DEFAULT 0," +
        		"    list_status text DEFAULT 'none'::text," +
        		"    conviction_score integer DEFAULT 0," +
        		"    conviction_status text DEFAULT 'observed'::text," +
        		"    connection_status text DEFAULT 'none'::text," +
        		"    risk_factor integer DEFAULT 0," +
        		"    number_of_threats_risk_score integer DEFAULT 0," +
        		"    max_local_severity_risk_score integer DEFAULT 0," +
        		"    total_bytes_in_risk_score integer DEFAULT 0," +
        		"    total_bytes_out_risk_score integer DEFAULT 0," +
        		"    total_number_of_connection_attempts_risk_score integer DEFAULT 0," +
        		"    av_coverage_risk_score integer DEFAULT 0," +
        		"    asset_category_priority_risk_score integer DEFAULT 0," +
        		"    number_of_threats_risk_bucket integer," +
        		"    max_local_severity_risk_bucket integer," +
        		"    total_bytes_in_risk_bucket integer," +
        		"    total_bytes_out_risk_bucket integer," +
        		"    total_number_of_connection_attempts_risk_bucket integer," +
        		"    av_coverage_risk_bucket integer," +
        		"    asset_category_priority_risk_bucket integer," +
        		"    overall_risk_bucket integer," +
        		"    av_coverage integer DEFAULT 0," +
        		"    asset_status text," +
        		"    detected_os text," +
        		"    first_seen_at timestamp without time zone);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_character_varrying_no_length() {
        printTest("shouldParseCreateTable_character_varying_no_length()");
        String content = "create table foo (bar character varying);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
        
        AstNode columnNode = childNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        
    }

    
    @Test
    public void shouldParseCreateTable_tstzrange() {
        printTest("shouldParseCreateTable_tstzrange()");
        String content = "create table foo (bar tstzrange);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
        
        AstNode columnNode = childNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
    }

    @Test
    public void shouldParseCreateTable_TIMESTAMPTZ() { 
        
        printTest("shouldParseCreateTable_timestamptz()");
        String content = "create table foo (bar TIMESTAMPTZ);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
        
        AstNode columnNode = childNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
    }

    // LISTEN virtual;

    @Test
    public void shouldParseListen() {
        printTest("shouldParseListen()");
        String content = "LISTEN virtual;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_LISTEN_STATEMENT));
    }

    // CREATE TEMP TABLE films_recent WITH (OIDS) ON COMMIT DROP AS
    // EXECUTE recentfilms(’2002-01-01’);

    @Test
    public void shouldParseCreateTempTable() {
        printTest("shouldParseCreateTempTable()");
        String content = "CREATE TEMP TABLE films_recent WITH (OIDS) ON COMMIT DROP AS EXECUTE recentfilms(’2002-01-01’);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));

    }

    @Test
    public void shouldParseCreateUnloggedTable() {
        printTest("shouldParseCreateTempTable()");
        String content = "CREATE UNLOGGED TABLE product (" +
        		"    id int  NOT NULL," +
        		"    product_category_id int  NOT NULL," +
        		"    sku char(10)  NOT NULL," +
        		"    CONSTRAINT product_pk PRIMARY KEY (id))" +
        		" WITH (OIDS=FALSE) TABLESPACE pg_default;";
        assertScoreAndParse(content, null, 1);
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
    public void shouldCreate1Tables() {
        printTest("");
        String sql = "CREATE TABLE business (\n" +
                "  id SERIAL NOT NULL CONSTRAINT business_id_pk PRIMARY KEY,\n" +
                "  name VARCHAR NOT NULL CONSTRAINT business_name_key UNIQUE,\n" +
                "  zip_code   VARCHAR,\n" +
                "  created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,\n" +
                "  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL\n" +
                ");";
        assertScoreAndParse(sql, null, 1); // 1 oznacza brak błędów
        assertTrue(hasMixinType(rootNode.getChild(0), TYPE_CREATE_TABLE_STATEMENT));
    }

    // CREATE RULE notify_me AS ON UPDATE TO mytable DO ALSO NOTIFY mytable;
    @Test
    public void shouldParseCreateRule() {
        printTest("shouldParseCreateRule()");
        String content = "CREATE RULE notify_me AS ON UPDATE TO mytable DO ALSO NOTIFY mytable;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_RULE_STATEMENT));

    }
    
    // CREATE TABLE IF NOT EXISTS
    @Test
    public void shouldParseCreateTableIfNotExists() {
        printTest("shouldParseCreateTableIfNotExists()");
        String content = "CREATE TABLE IF NOT EXISTS product (" +
                "    id int NOT NULL," +
                "    product_category_id int NOT NULL," +
                "    name varchar(50));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }
    
    @Test
    public void shouldParseCreateTempTableIfNotExists() {
        printTest("shouldParseCreateTempTableIfNotExists()");
        String content = "CREATE TEMP TABLE IF NOT EXISTS product (" +
                "    id int NOT NULL," +
                "    product_category_id int NOT NULL," +
                "    name varchar(50));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }
    
    @Test
    public void shouldPareseCreateGlobalTempTableIfNotExists() {
        printTest("shouldPareseCreateGlobalTempTableIfNotExists()");
        String content = "CREATE GLOBAL TEMP TABLE IF NOT EXISTS product (" +
                "    id int NOT NULL," +
                "    product_category_id int NOT NULL," +
                "    name varchar(50));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }
    
    @Test
    public void shouldParseCreateLocalTempTableIfNotExists() {
        printTest("shouldParseCreateLocalTempTableIfNotExists()");
        String content = "CREATE LOCAL TEMP TABLE IF NOT EXISTS product (" +
                "    id int NOT NULL," +
                "    product_category_id int NOT NULL," +
                "    name varchar(50));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateGlobalTemporaryTableIfNotExists() {
        printTest("shouldParseCreateGlobalTemporaryTableIfNotExists()");
        String content = "CREATE GLOBAL TEMPORARY TABLE IF NOT EXISTS product (" +
                "    id int NOT NULL," +
                "    product_category_id int NOT NULL," +
                "    name varchar(50));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateLocalTemporaryTableIfNotExists() {
        printTest("shouldParseCreateLocalTemporaryTableIfNotExists()");
        String content = "CREATE LOCAL TEMPORARY TABLE IF NOT EXISTS product (" +
                "    id int NOT NULL," +
                "    product_category_id int NOT NULL," +
                "    name varchar(50));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }
    
    @Test
    public void shouldParseCreateUnloggedTableIfNotExists() {
        printTest("shouldParseCreateUnloggedTableIfNotExists()");
        String content = "CREATE UNLOGGED TABLE IF NOT EXISTS product (" +
                "    id int NOT NULL," +
                "    product_category_id int NOT NULL," +
                "    name varchar(50));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }
    
    @Test
    public void shouldParseAlterForeignDataWrapper() {
        printTest("");
        String content = "ALTER FOREIGN DATA WRAPPER dbi OPTIONS (ADD foo ’1’, DROP ’bar’);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_ALTER_FOREIGN_DATA_WRAPPER_STATEMENT));
    }

    @Test
    public void shouldParseCommentOn() {
        printTest("shouldParseCommentOn()");
        String content = "COMMENT ON TABLE mytable IS ’This is my table.’;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_COMMENT_ON_STATEMENT));
    }


    @Test
    public void shouldParseCommentOnExtenstion() {
        printTest("shouldParseCommentOnExtenstion()");
        String content = "COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_COMMENT_ON_STATEMENT));
    }

    
    @Test
    public void shouldParseCreateFunctionWithMultipleSemicolons() {
        printTest("shouldParseCreateFunctionWithMultipleSemicolons()");
        String content = "CREATE OR REPLACE FUNCTION increment(i integer) RETURNS integer AS $$ BEGIN RETURN i + 1; END;" + SPACE
                         + "CREATE TABLE tblName_A (col_1 varchar(255));";
        assertScoreAndParse(content, null, 2);
    }

    @Test
    public void shouldParseLockTable() {
        printTest("shouldParseLockTable()");
        String content = "LOCK TABLE films IN SHARE MODE;";
        assertScoreAndParse(content, null, 1);
    }

    @Test
    public void shouldParsePrepareStatement() {
        printTest("shouldParsePrepareStatement()");
        String content = "PREPARE fooplan (int, text, bool, numeric) AS INSERT INTO foo VALUES($1, $2, $3, $4);";
        assertScoreAndParse(content, null, 1);
    }

    @Test
    public void shouldParseDropDomain() {
        printTest("shouldParseDropDomain()");
        String content = "DROP DOMAIN IF EXISTS domain_name CASCADE;";
        assertScoreAndParse(content, null, 1);
    }

    @Test
    public void shouldParseDropTableMultiple() {
        printTest("shouldParseDropDomain()");
        String content = "DROP TABLE films, distributors;";
        assertScoreAndParse(content, null, 2);
    }

    @Test
    public void shouldParseGrantOnTable() {
        printTest("shouldParseGrantOnTable()");
        String content = "GRANT UPDATE, TRIGGER ON TABLE t TO anita,zhi;";
        assertScoreAndParse(content, null, 1);
    }

    @Test
    public void shouldParseGrantOnMultipleTables() {
        printTest("shouldParseGrantOnMultipleTables()");
        String content = "GRANT UPDATE, TRIGGER ON TABLE t1, t2, t3 TO anita,zhi;";
        assertScoreAndParse(content, null, 3);
    }

    @Test
    public void shouldParseGrantExecuteOnFunction() {
        printTest("shouldParseGrantExecuteOnFunction()");
        String content = "GRANT EXECUTE ON FUNCTION divideByTwo(numerator int, IN demoninator int) TO george;";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_GRANT_ON_FUNCTION_STATEMENT));
    }

    @Test
    public void shouldParseGrantExecuteAndUpdateOnMultipleFunctions() {
        printTest("shouldParseGrantExecuteOnMultipleFunctions()");
        String content = "GRANT EXECUTE, UPDATE ON FUNCTION cos(), sin(b double precision) TO peter;";
        assertScoreAndParse(content, null, 2);
        AstNode childNode = rootNode.getChild(0);
        assertTrue(hasMixinType(childNode, TYPE_GRANT_ON_FUNCTION_STATEMENT));
        assertEquals(3, childNode.getChildCount());
        childNode = rootNode.getChild(1);
        assertEquals(4, childNode.getChildCount());
    }

    @Test
    public void shouldParsePostgresStatements_1() {
        printTest("shouldParsePostgresStatements_1()");
        String content = getFileContent(DDL_FILE_PATH + "postgres_test_statements_1.ddl");
        assertScoreAndParse(content, "postgres_test_statements_1.ddl", 82);
    }

    @Test
    public void shouldParsePostgresStatements_2() {
        printTest("shouldParsePostgresStatements_2()");
        String content = getFileContent(DDL_FILE_PATH + "postgres_test_statements_2.ddl");
        assertScoreAndParse(content, "postgres_test_statements_2.ddl", 101);
    }

    @Test
    public void shouldParsePostgresStatements_3() {
        printTest("shouldParsePostgresStatements_3()");
        String content = getFileContent(DDL_FILE_PATH + "postgres_test_statements_3.ddl");
        assertScoreAndParse(content, "postgres_test_statements_3.ddl", 143);
    }

    @Test
    public void shouldParsePostgresStatements_4() {
        printTest("shouldParsePostgresStatements_4()");
        String content = getFileContent(DDL_FILE_PATH + "postgres_test_statements_4.ddl");
        assertScoreAndParse(content, "postgres_test_statements_4.ddl", 34);
    }

    @Test
    public void shouldParseCreateTableFloat4() {
        printTest("shouldParseCreateTableFloat4()");
        String content = "CREATE TABLE \"public\".\"phy_communesecondaire\" ("
                + " \"id\"            int8 NOT NULL DEFAULT nextval('phy_communesecondaire_id_seq'::regclass),"
                + " \"jhpotentiel\"   float4 NOT NULL,"
                + " PRIMARY KEY(\"id\")"
                + ");";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateIndexUsingBtree() {
        printTest("shouldParseCreateIndexUsingBtree()");
        String content = "CREATE INDEX  \"index_reports_on_report_url\"" +
        		" ON \"public\".\"reports\"" +
        		" USING btree(report_url COLLATE \"default\" ASC NULLS LAST);";
        assertScoreAndParse(content, null, 1);
        System.out.println(rootNode.getChildren().get(0).getChildren().toString());
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, PostgresDdlLexicon.TYPE_CREATE_INDEX_STATEMENT));
    }
    
    @Test
    public void shouldParseRevokeOnSchema() {
        printTest("shouldParseRevokeOnSchema()");
        String content = "REVOKE ALL ON SCHEMA public FROM public;";
        assertScoreAndParse(content, null, 1);
        
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, PostgresDdlLexicon.TYPE_REVOKE_ON_SCHEMA_STATEMENT));

        assertEquals(1, childNode.getChildCount());

    }
    
    @Test
    public void shouldParseGrantAllOnSchema() {
        printTest("shouldParseGrantAllOnSchema()");
        String content = "GRANT ALL ON SCHEMA public TO postgres;";
        assertScoreAndParse(content, null, 1);
        
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, PostgresDdlLexicon.TYPE_GRANT_ON_SCHEMA_STATEMENT));

        assertEquals(1, childNode.getChildCount());

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


    @Test
    public void shouldParseCreateMaterializedView() {
        printTest("shouldParseCreateViewAndReturnOriginalQueryExpression");
        String viewQuery =  "SELECT \n" +
                "pua.user_profile_id, " +
                "au.user_profile_id IS NOT NULL as is_anonymous, \n" +
                "FROM poll_user_answer pua \n"+
                "LEFT JOIN anonymous_user au ON au.user_profile_id = pua.user_profile_id";

        String content = "CREATE MATERIALIZED VIEW poll_view as " + viewQuery;
        assertScoreAndParse(content, null, 1);

        AstNode viewNode = rootNode.getChildren().get(0);
        String returnedQuery = viewNode.getProperty(CREATE_VIEW_QUERY_EXPRESSION).toString();

        assertEquals("poll_view", viewNode.getName());
        assertTrue((Boolean)viewNode.getProperty(PostgresDdlLexicon.MATERIALIZED));
        assertEquals(replaceMultipleWhiteSpaces(viewQuery), replaceMultipleWhiteSpaces(returnedQuery));
    }

    @Test
    public void shouldParseCreateViewWithWith() {
        printTest("shouldParseCreateViewAndReturnOriginalQueryExpression");
        String viewQuery = "WITH regional_sales AS (\n" +
                "        SELECT region, SUM(amount) AS total_sales\n" +
                "        FROM orders\n" +
                "        GROUP BY region\n" +
                "     ), top_regions AS (\n" +
                "        SELECT region\n" +
                "        FROM regional_sales\n" +
                "        WHERE total_sales > (SELECT SUM(total_sales)/10 FROM regional_sales)\n" +
                "     )\n" +
                "SELECT region,\n" +
                "       product,\n" +
                "       SUM(quantity) AS product_units,\n" +
                "       SUM(amount) AS product_sales\n" +
                "FROM orders\n" +
                "WHERE region IN (SELECT region FROM top_regions)\n" +
                "GROUP BY region, product";

        String content = "CREATE VIEW test as " + viewQuery;
        assertScoreAndParse(content, null, 1);

        AstNode viewNode = rootNode.getChildren().get(0);
        String returnedQuery = viewNode.getProperty(CREATE_VIEW_QUERY_EXPRESSION).toString();

        assertEquals("test", viewNode.getName());
        assertEquals(replaceMultipleWhiteSpaces(viewQuery), replaceMultipleWhiteSpaces(returnedQuery));
    }




    @Test
    public void parseFromFileTestExample() {
        printTest("testParse");
        InputStream inputStream = this.getClass().getResourceAsStream("/test-sql/postgres.sql");
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String sql = s.hasNext() ? s.next() : "";
        assertScoreAndParse(sql, null, 1);
    }

    @Test
    public void shouldParseChar10() {
        String createTableQuery = "CREATE TABLE entity_3 (\n" +
                "    attribute_1 Char(10)  NOT NULL,\n" +
                "    attribute_2 int  NOT NULL\n" +
                ");";

        // Do it again, but this time without scoring first ...
        setRootNode(parser.nodeFactory().node("ddlRootNode"));
        parser.setRootNode(getRootNode());
        parser.parse(createTableQuery, getRootNode(), null);

        assertEquals(1, rootNode.getChildCount());
        AstNode createTable = rootNode.getChildren().get(0);
        assertEquals(2, createTable.getChildCount());
        assertEquals(7, createTable.getPropertyNames().size());
    }

    @Test
    public void shouldParseScriptEdwn3868() {
        printTest("testParse");
        InputStream inputStream = this.getClass().getResourceAsStream("/ddl/dialect/postgres/postgres_EDWM_3868.ddl");
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String sql = s.hasNext() ? s.next() : "";
        assertScoreAndParse(sql, null, 17);
    }

    private static String replaceMultipleWhiteSpaces(String a) {
    	return a.replaceAll("\\s*", "").trim();
    }

}
