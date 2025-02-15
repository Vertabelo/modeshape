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
package org.modeshape.sequencer.ddl.dialect.sqlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.sqlite.SqliteDdlLexicon.*;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.sequencer.ddl.DdlParserScorer;
import org.modeshape.sequencer.ddl.DdlParserTestHelper;
import org.modeshape.sequencer.ddl.node.AstNode;

public class SqliteDdlParserTest extends DdlParserTestHelper {

    public static final String DDL_FILE_PATH = "ddl/dialect/sqlite/";

    @Before
    public void beforeEach() {
        parser = new SqliteDdlParser();
//        setPrintToConsole(true);
        parser.setTestMode(isPrintToConsole());
        parser.setDoUseTerminator(true);
        rootNode = parser.nodeFactory().node("ddlRootNode");
        scorer = new DdlParserScorer();
    }

    @Test
    public void shouldParseCreateTable_1() {
        String content = "CREATE TABLE test (a int);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_2() {
        // column definition without datatype
        String content = "CREATE TABLE test (a);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_3() {
        // column definition with constraints
        String content = "create table test ("
                + "  abc1 int not null,"
                + "  abc2 integer constraint aaa null,"
                + "  abc3 integer primary key on conflict abort autoincrement,"
                + "  abc4 int constraint aaa unique on conflict abort deferrable initially deferred,"
                + "  abc5 int constraint aaa collate NOCASE,"
                + "  abc6 int constraint aaa default \"zsad asd dsa\","
                + "  abc7 int references test1 (abc),"
                + "  abc8 int constraint fk references test1 (abc));";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_4() {
        // table constraints
        String content = "create table ref (id integer primary key);"
                + "create table test ("
                + "  abc1 int,"
                + "  abc2 integer,"
                + "  abc3 integer,"
                + "  abc4 int,"
                + "  abc5 int,"
                + "  abc6 int,"
                + "  abc7 int,"
                + "  abc8 int,"
                + "  constraint aaa unique (abc4, abc5) on conflict abort deferrable initially deferred,"
                + "  constraint zzz primary key (abc1, abc2),"
                + "  constraint fk foreign key (abc3) references ref (id) on delete set null on update cascade match full deferrable initially deferred,"
                + "  constraint sss check (abc4 <> \"x\")"
                + ");";
        assertScoreAndParse(content, null, 2);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_5() {
        // table constraints
        String content = "create table ref (id integer primary key);"
                + "create table test ("
                + "  abc1 int,"
                + "  abc2 integer,"
                + "  abc3 integer,"
                + "  abc4 int,"
                + "  abc5 int,"
                + "  unique (abc4, abc5) on conflict abort deferrable initially deferred,"
                + "  primary key (abc1, abc2),"
                + "  foreign key (abc3) references ref (id) on delete set null on update cascade match full deferrable initially deferred,"
                + "  check (abc4 <> \"x\")"
                + ");";
        assertScoreAndParse(content, null, 2);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTable_6() {
        // table constraints - indexed-columns
        String content = "create table test ("
                + "  abc1 int,"
                + "  abc2 integer,"
                + "  abc3 integer,"
                + "  abc4 int,"
                + "  abc5 int,"
                + "  unique (abc4 collate NOCASE, abc5 DESC) on conflict abort deferrable initially deferred,"
                + "  primary key (abc1 ASC, abc2 DESC),"
                + "  check (abc4 <> \"x\")"
                + ");";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_TABLE_STATEMENT));
    }

    @Test
    public void shouldParseCreateTableWithColumnAtEnd() {
        // from the user model
        printTest("shouldParseCreateTableWithColumnAtEnd");
        String content = "CREATE TABLE TEST (" +
                " test int not null," +
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
    public void shouldParseCreateIndex_1() {
        // no table defined - it'll ommit columns
        String content = "CREATE INDEX idx1 ON test (a ASC);";
        assertScoreAndParse(content, null, 1);
        AstNode childNode = rootNode.getChildren().get(0);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_INDEX_STATEMENT));
    }

    @Test
    public void shouldParseCreateIndex_2() {
        String content = "CREATE TABLE test (a int);"
                + " CREATE INDEX idx1 ON test (a ASC);";
        assertScoreAndParse(content, null, 2);
        AstNode childNode = rootNode.getChildren().get(1);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_INDEX_STATEMENT));
    }

    @Test
    public void shouldParseCreateIndex_3() {
        String content = "CREATE TABLE test (a int, b int);"
                + " CREATE UNIQUE INDEX IF NOT EXISTS dat.idx1 ON test (b ASC, a COLLATE utf8 DESC);";
        assertScoreAndParse(content, null, 2);
        AstNode childNode = rootNode.getChildren().get(1);
        assertTrue(hasMixinType(childNode, TYPE_CREATE_INDEX_STATEMENT));
        
        for(AstNode ch : childNode) {
            printTest(ch.toString());
        }
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
