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
import static org.modeshape.sequencer.ddl.dialect.oracle.OracleDdlLexicon.TYPE_CREATE_TABLE_INDEX_STATEMENT;

/**
 * Testy parsowania DDL tworzenia tabeli z nowymi atrybutami.
 *
 * Dokumentacja: https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-TABLE.html
 *
 * @author Marek Berkan
 */
public class OracleDdlParserCreateTableAttributesTest extends DdlParserTestHelper {

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
    public void shouldParseCreateTableWithSharingEqualsMetadata() {
        String sql = "CREATE TABLE my_table SHARING=METADATA (\n"
                + "   id integer  NOT NULL,\n"
                + "   CONSTRAINT my_table_pk PRIMARY KEY (id)\n"
                + ") ;";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "METADATA");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithSharingEqualsData() {
        String sql = "CREATE TABLE my_table SHARING=DATA (\n"
                + "   id integer  NOT NULL,\n"
                + "   CONSTRAINT my_table_pk PRIMARY KEY (id)\n"
                + ") ;";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "DATA");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithSharingEqualsExtendedData() {
        String sql = "CREATE TABLE my_table SHARING=EXTENDED DATA (\n"
                + "   id integer  NOT NULL,\n"
                + "   CONSTRAINT my_table_pk PRIMARY KEY (id)\n"
                + ") ;";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "EXTENDED DATA");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithCollation() {
        String sql = "CREATE TABLE my_table (\n"
                + "   id integer  NOT NULL,\n"
                + "   CONSTRAINT my_table_pk PRIMARY KEY (id)\n"
                + ") DEFAULT COLLATION USING_NLS_COMP;";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COLLATION, "USING_NLS_COMP");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithSharingEqualsNone() {
        String sql = "CREATE TABLE my_table SHARING=NONE (\n"
                + "   id integer  NOT NULL,\n"
                + "   CONSTRAINT my_table_pk PRIMARY KEY (id)\n"
                + ") ;";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "NONE");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithPartition() {
        String sql = "CREATE TABLE my_table (\n"
                + "   id integer  NOT NULL,\n"
                + "   CONSTRAINT my_table_pk PRIMARY KEY (id)\n"
                + ") PARTITION BY RANGE (id)\n"
                + " ( PARTITION id_up_to_1000 VALUES LESS THAN 1000\n"
                + "    TABLESPACE tsa\n"
                + " , PARTITION id_up_to_2000 VALUES LESS THAN 2000\n"
                + "    TABLESPACE tsb\n"
                + " );";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_PARTITIONING,
                       "PARTITION RANGE (id) ( PARTITION id_up_to_1000 VALUES LESS THAN 1000 TABLESPACE tsa, PARTITION id_up_to_2000 VALUES LESS THAN 2000 TABLESPACE tsb )");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertTrue(hasMixinType(columnNode, TYPE_COLUMN_DEFINITION));
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithSharingAndCollation() {
        String sql = "CREATE TABLE table_with_new_attributes SHARING=EXTENDED DATA (\n"
                + "    id integer  NOT NULL,\n"
                + "    name varchar2(255)  NOT NULL,\n"
                + "    CONSTRAINT table_with_new_attributes_pk PRIMARY KEY (id)\n"
                + ")  DEFAULT COLLATION USING_NLS_COMP;";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "EXTENDED DATA");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COLLATION, "USING_NLS_COMP");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithSharingAndCollationAndPartition() {
        String sql = "CREATE TABLE table_with_new_attributes SHARING=EXTENDED DATA (\n"
                + "    id integer  NOT NULL,\n"
                + "    name varchar2(255)  NOT NULL,\n"
                + "    CONSTRAINT table_with_new_attributes_pk PRIMARY KEY (id)\n"
                + ")  DEFAULT COLLATION USING_NLS_COMP\n"
                + " PARTITION BY RANGE (id)\n"
                + " ( PARTITION id_up_to_1000 VALUES LESS THAN 1000\n"
                + "    TABLESPACE tsa\n"
                + " , PARTITION id_up_to_2000 VALUES LESS THAN 2000\n"
                + "    TABLESPACE tsb\n"
                + " )\n"
                + ";";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "EXTENDED DATA");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COLLATION, "USING_NLS_COMP");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithCompressAndCollation() {
        String sql = "CREATE TABLE table_with_new_attributes (\n"
                + "   id integer  NOT NULL,\n"
                + "   name varchar2(255)  NOT NULL,\n"
                + "   CONSTRAINT table_with_new_attributes_pk PRIMARY KEY (id)\n"
                + ")  COMPRESS FOR ALL OPERATIONS\n"
                + "DEFAULT COLLATION USING_NLS_COMP\n"
                + ")\n"
                + ";";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COMPRESSION, "COMPRESS FOR ALL OPERATIONS");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COLLATION, "USING_NLS_COMP");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithAllPossibleAttributesWithMaxSizeValue() {
        String sql = "CREATE TABLE my_schema.table_with_new_attributes SHARING=EXTENDED DATA (\n"
                + "   id integer  NOT NULL,\n"
                + "   name varchar2(255)  NOT NULL,\n"
                + "   CONSTRAINT table_with_new_attributes_pk PRIMARY KEY (id)\n"
                + ")  ORGANIZATION HEAP\n"
                + "PCTFREE my_pctfree\n"
                + "PCTUSED my_pctused\n"
                + "INITRANS my_initrans\n"
                + "TABLESPACE my_tablespace\n"
                + "STORAGE (\n"
                + "INITIAL my_storage_initial\n"
                + "NEXT my_storage_next\n"
                + "MINEXTENTS my_storage_minextents\n"
                + "MAXEXTENTS my_storage_maxextents\n"
                //                + "MAXEXTENTS UNLIMITED\n"
                + "PCTINCREASE my_storage_pctincrease\n"
                + "FREELISTS my_storage_freelist\n"
                + "FREELIST GROUPS my_storage_freelist_groups\n"
                + "OPTIMAL my_storage_optimal_size\n"
                + "OPTIMAL NULL\n"
                + "MAXSIZE my_storage_maxsize\n"
                //                + "MAXSIZE UNLIMITED\n"
                + "BUFFER_POOL KEEP\n"
                + "ENCRYPT\n"
                + ") COMPRESS FOR ALL OPERATIONS\n"
                + "CACHE\n"
                + "ROWDEPENDENCIES\n"
                + "DEFAULT COLLATION USING_NLS_COMP\n"
                + "PARTITION BY RANGE (id)\n"
                + "( PARTITION id_up_to_1000 VALUES LESS THAN 1000\n"
                + "   TABLESPACE tsa\n"
                + ", PARTITION id_up_to_2000 VALUES LESS THAN 2000\n"
                + "   TABLESPACE tsb\n"
                + ");";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "EXTENDED DATA");
        assertProperty(createTableNode, OracleDdlLexicon.PHYSICAL_PCTFREE, "my_pctfree");
        assertProperty(createTableNode, OracleDdlLexicon.PHYSICAL_PCTUSED, "my_pctused");
        assertProperty(createTableNode, OracleDdlLexicon.PHYSICAL_INITRANS, "my_initrans");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_TABLESPACE, "my_tablespace");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_CLAUSE, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_INITIAL, "my_storage_initial");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_NEXT, "my_storage_next");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MINEXTENTS, "my_storage_minextents");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXEXTENTS, "my_storage_maxextents");
        // assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXEXTENTS_UNLIMITED, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_PCTINCREASE, "my_storage_pctincrease");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_FREELISTS, "my_storage_freelist");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_FREELIST_GROUPS, "my_storage_freelist_groups");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_OPTIMAL_SIZE, "my_storage_optimal_size");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_OPTIMAL, "NULL");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXSIZE, "my_storage_maxsize");
        //        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXSIZE_UNLIMITED, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_BUFFER_POOL, "KEEP");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_ENCRYPT, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COMPRESSION, "COMPRESS FOR ALL OPERATIONS");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_CACHE, "CACHE");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_ROWDEPENDENCIES, "ROWDEPENDENCIES");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COLLATION, "USING_NLS_COMP");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_PARTITIONING,
                       "PARTITION RANGE (id) ( PARTITION id_up_to_1000 VALUES LESS THAN 1000 TABLESPACE tsa, PARTITION id_up_to_2000 VALUES LESS THAN 2000 TABLESPACE tsb )");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithAllPossibleAttributesWithMaxSizeUnlimited() {
        String sql = "CREATE TABLE my_schema.table_with_new_attributes SHARING=EXTENDED DATA (\n"
                + "   id integer  NOT NULL,\n"
                + "   name varchar2(255)  NOT NULL,\n"
                + "   CONSTRAINT table_with_new_attributes_pk PRIMARY KEY (id)\n"
                + ")  ORGANIZATION HEAP\n"
                + "PCTFREE my_pctfree\n"
                + "PCTUSED my_pctused\n"
                + "INITRANS my_initrans\n"
                + "TABLESPACE my_tablespace\n"
                + "STORAGE (\n"
                + "INITIAL my_storage_initial\n"
                + "NEXT my_storage_next\n"
                + "MINEXTENTS my_storage_minextents\n"
                //                + "MAXEXTENTS my_storage_maxextents\n"
                + "MAXEXTENTS UNLIMITED\n"
                + "PCTINCREASE my_storage_pctincrease\n"
                + "FREELISTS my_storage_freelist\n"
                + "FREELIST GROUPS my_storage_freelist_groups\n"
                + "OPTIMAL my_storage_optimal_size\n"
                + "OPTIMAL NULL\n"
                //                + "MAXSIZE my_storage_maxsize\n"
                + "MAXSIZE UNLIMITED\n"
                + "BUFFER_POOL KEEP\n"
                + "ENCRYPT\n"
                + ") COMPRESS FOR ALL OPERATIONS\n"
                + "CACHE\n"
                + "ROWDEPENDENCIES\n"
                + "DEFAULT COLLATION USING_NLS_COMP\n"
                + "PARTITION BY RANGE (id)\n"
                + "( PARTITION id_up_to_1000 VALUES LESS THAN 1000\n"
                + "   TABLESPACE tsa\n"
                + ", PARTITION id_up_to_2000 VALUES LESS THAN 2000\n"
                + "   TABLESPACE tsb\n"
                + ");";

        assertScoreAndParse(sql, null, 1);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_SHARING, "EXTENDED DATA");
        assertProperty(createTableNode, OracleDdlLexicon.PHYSICAL_PCTFREE, "my_pctfree");
        assertProperty(createTableNode, OracleDdlLexicon.PHYSICAL_PCTUSED, "my_pctused");
        assertProperty(createTableNode, OracleDdlLexicon.PHYSICAL_INITRANS, "my_initrans");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_TABLESPACE, "my_tablespace");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_CLAUSE, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_INITIAL, "my_storage_initial");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_NEXT, "my_storage_next");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MINEXTENTS, "my_storage_minextents");
        //        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXEXTENTS, "my_storage_maxextents");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXEXTENTS_UNLIMITED, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_PCTINCREASE, "my_storage_pctincrease");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_FREELISTS, "my_storage_freelist");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_FREELIST_GROUPS, "my_storage_freelist_groups");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_OPTIMAL_SIZE, "my_storage_optimal_size");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_OPTIMAL, "NULL");
        //        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXSIZE, "my_storage_maxsize");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_MAXSIZE_UNLIMITED, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_BUFFER_POOL, "KEEP");
        assertProperty(createTableNode, OracleDdlLexicon.STORAGE_ENCRYPT, Boolean.TRUE);
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COMPRESSION, "COMPRESS FOR ALL OPERATIONS");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_CACHE, "CACHE");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_ROWDEPENDENCIES, "ROWDEPENDENCIES");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_COLLATION, "USING_NLS_COMP");
        assertProperty(createTableNode, OracleDdlLexicon.TABLE_PARTITIONING,
                       "PARTITION RANGE (id) ( PARTITION id_up_to_1000 VALUES LESS THAN 1000 TABLESPACE tsa, PARTITION id_up_to_2000 VALUES LESS THAN 2000 TABLESPACE tsb )");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }

    @Test
    public void shouldParseCreateTableWithIndexPartitionAttribute() {
        String sql = "CREATE TABLE table_with_new_attributes SHARING=EXTENDED DATA (\n"
                + "   id integer  NOT NULL,\n"
                + "   name varchar2(255)  NOT NULL,\n"
                + "   CONSTRAINT table_with_new_attributes_pk PRIMARY KEY (id)\n"
                + ");\n"
                + "CREATE INDEX my_table_idx_1 \n"
                + "on table_with_new_attributes \n"
                + "(id ASC)\n"
                + "PARTITION BY RANGE (id)\n"
                + "( PARTITION id_up_to_1000 VALUES LESS THAN 1000\n"
                + "   TABLESPACE tsa\n"
                + ", PARTITION id_up_to_2000 VALUES LESS THAN 2000\n"
                + "   TABLESPACE tsb\n"
                + ");";

        assertScoreAndParse(sql, null, 2);

        AstNode createTableNode = rootNode.getChildren().get(0);
        assertMixinType(createTableNode, TYPE_CREATE_TABLE_STATEMENT);

        AstNode createIndexNode = rootNode.getChildren().get(1);
        assertMixinType(createIndexNode, TYPE_CREATE_TABLE_INDEX_STATEMENT);
        assertProperty(createIndexNode, OracleDdlLexicon.INDEX_PARTITIONING,
                       "PARTITION RANGE (id) ( PARTITION id_up_to_1000 VALUES LESS THAN 1000 TABLESPACE tsa, PARTITION id_up_to_2000 VALUES LESS THAN 2000 TABLESPACE tsb )");

        AstNode columnNode = createTableNode.getChildren().get(0);
        assertMixinType(columnNode, TYPE_COLUMN_DEFINITION);
        assertEquals("integer", columnNode.getProperty(OracleDdlLexicon.DATATYPE_NAME));
    }


}
