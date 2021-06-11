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
 * 02110-1301 USA     = Namespace.PREFIX  + ":createExternalStatement"; or see the FSF site: http://www.fsf.org.
 */
package org.modeshape.sequencer.ddl.dialect.snowflake;

import org.apache.poi.ss.formula.functions.Na;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;

import static org.modeshape.sequencer.ddl.StandardDdlLexicon.Namespace.PREFIX;

/**
 *
 */
public class SnowflakeDdlLexicon extends StandardDdlLexicon {




    public static class Namespace {

        public static final String URI = "http://www.modeshape.org/ddl/snowflake/1.0";
        public static final String PREFIX = "snowflakeddl";
    }


    public static final String TYPE_CREATE_DATABASE_STATEMENT 			= Namespace.PREFIX + ":createDatabaseStatement";
    public static final String TYPE_CREATE_FUNCTION_STATEMENT 			= Namespace.PREFIX + ":createFunctionStatement";
    public static final String TYPE_CREATE_ROLE_STATEMENT 				= Namespace.PREFIX + ":createRoleStatement";
    public static final String TYPE_CREATE_SEQUENCE_STATEMENT 			= Namespace.PREFIX + ":createSequenceStatement";
    public static final String TYPE_CREATE_USER_STATEMENT 				= Namespace.PREFIX + ":createUserStatement";
    public static final String TYPE_CREATE_MATERIALIZED_VIEW_STATEMENT  = Namespace.PREFIX  + ":createMaterializedViewStatement";
    public static final String TYPE_CREATE_EXTERNAL_TABLE_STATEMENT     = Namespace.PREFIX  + ":createExternalStatement";
    public static final String TYPE_CREATE_MASKING_POLICY               = Namespace.PREFIX  + ":createMaskingPolicy";
    public static final String TYPE_CREATE_STAGE                        = Namespace.PREFIX  + ":createStage";
    public static final String TYPE_CREATE_PIPE                         = Namespace.PREFIX  + ":createPipe";
    public static final String TYPE_CREATE_STREAM                       = Namespace.PREFIX  + ":createStream";
    public static final String TYPE_CREATE_TASK                         = Namespace.PREFIX  + ":createTask";
    public static final String TYPE_CREATE_PROCEDURE                    = Namespace.PREFIX  + ":createProcedure";
    public static final String TYPE_CREATE_NETWORK_POLICY               = Namespace.PREFIX  + ":createNetworkPolicy";
    public static final String TYPE_CREATE_RESOURCE_MONITOR             = Namespace.PREFIX  + ":createResourceMonitor";
    public static final String TYPE_CREATE_SHARE                        = Namespace.PREFIX  + ":createShare";
    public static final String TYPE_CREATE_WAREHOUSE                    = Namespace.PREFIX  + ":createWarehouse";
    public static final String TYPE_CREATE_NOTIFICATION_INTEGRATION     = Namespace.PREFIX  + ":createNotificationIntegration";
    public static final String TYPE_CREATE_SECURITY_INTEGRATION         = Namespace.PREFIX  + ":createSecurityIntegration";
    public static final String TYPE_CREATE_STORAGE_INTEGRATION          = Namespace.PREFIX  + ":createStorageIntegration";

    public static final String TYPE_DROP_DATABASE_STATEMENT 				= Namespace.PREFIX + ":dropDatabaseStatement";
    public static final String TYPE_DROP_FUNCTION_STATEMENT 				= Namespace.PREFIX + ":dropFunctionStatement";
    public static final String TYPE_DROP_OWNED_BY_STATEMENT 				= Namespace.PREFIX + ":dropOwnedByStatement";
    public static final String TYPE_DROP_ROLE_STATEMENT 					= Namespace.PREFIX + ":dropRoleStatement";
    public static final String TYPE_DROP_SEQUENCE_STATEMENT 				= Namespace.PREFIX + ":dropSequenceStatement";
    public static final String TYPE_DROP_USER_STATEMENT 					= Namespace.PREFIX + ":dropUserStatement";
    public static final String TYPE_DROP_MATERIALIZED_VIEW_STATEMENT        = Namespace.PREFIX  + ":dropMaterializedViewStatement";
    public static final String TYPE_DROP_EXTERNAL_TABLE_STATEMENT           = Namespace.PREFIX  + ":dropExternalStatement";
    public static final String TYPE_DROP_MASKING_POLICY                     = Namespace.PREFIX  + ":dropMaskingPolicy";
    public static final String TYPE_DROP_STAGE                              = Namespace.PREFIX  + ":dropStage";
    public static final String TYPE_DROP_PIPE                               = Namespace.PREFIX  + ":dropPipe";
    public static final String TYPE_DROP_STREAM                             = Namespace.PREFIX  + ":dropStream";
    public static final String TYPE_DROP_TASK                               = Namespace.PREFIX  + ":dropTask";
    public static final String TYPE_DROP_PROCEDURE                          = Namespace.PREFIX  + ":dropProcedure";
    public static final String TYPE_DROP_NETWORK_POLICY                     = Namespace.PREFIX  + ":dropNetworkPolicy";
    public static final String TYPE_DROP_RESOURCE_MONITOR                   = Namespace.PREFIX  + ":dropResourceMonitor";
    public static final String TYPE_DROP_SHARE                              = Namespace.PREFIX  + ":dropShare";
    public static final String TYPE_DROP_WAREHOUSE                          = Namespace.PREFIX  + ":dropWarehouse";
    public static final String TYPE_DROP_NOTIFICATION_INTEGRATION           = Namespace.PREFIX  + ":dropNotificationIntegration";
    public static final String TYPE_DROP_SECURITY_INTEGRATION               = Namespace.PREFIX  + ":dropSecurityIntegration";
    public static final String TYPE_DROP_STORAGE_INTEGRATION                = Namespace.PREFIX  + ":dropStorageIntegration";

    public static final String TYPE_ALTER_DATABASE_STATEMENT 				= Namespace.PREFIX + ":alterDatabaseStatement";
    public static final String TYPE_ALTER_FUNCTION_STATEMENT 				= Namespace.PREFIX + ":alterFunctionStatement";
    public static final String TYPE_ALTER_ROLE_STATEMENT 					= Namespace.PREFIX + ":alterRoleStatement";
    public static final String TYPE_ALTER_SEQUENCE_STATEMENT 				= Namespace.PREFIX + ":alterSequenceStatement";
    public static final String TYPE_ALTER_SCHEMA_STATEMENT   				= Namespace.PREFIX + ":alterSchemaStatement";
    public static final String TYPE_ALTER_USER_STATEMENT 					= Namespace.PREFIX + ":alterUserStatement";
    public static final String TYPE_ALTER_VIEW_STATEMENT 					= Namespace.PREFIX + ":alterViewStatement";
    public static final String TYPE_ALTER_MATERIALIZED_VIEW_STATEMENT       = Namespace.PREFIX  + ":alterMaterializedViewStatement";
    public static final String TYPE_ALTER_EXTERNAL_TABLE_STATEMENT          = Namespace.PREFIX  + ":alterExternalStatement";



    public static final String TYPE_ABORT_STATEMENT 				= Namespace.PREFIX + ":abortStatement";
    public static final String TYPE_ANALYZE_STATEMENT 				= Namespace.PREFIX + ":analyzeStatement";
    public static final String TYPE_CLUSTER_STATEMENT 				= Namespace.PREFIX + ":clusterStatement";
    public static final String TYPE_COMMENT_ON_STATEMENT 			= Namespace.PREFIX + ":commentOnStatement";
    public static final String TYPE_COPY_STATEMENT 					= Namespace.PREFIX + ":copyStatement";
    public static final String TYPE_DEALLOCATE_STATEMENT			= Namespace.PREFIX + ":deallocateStatement";
    public static final String TYPE_DECLARE_STATEMENT 				= Namespace.PREFIX + ":declareStatement";
    public static final String TYPE_EXPLAIN_STATEMENT 				= Namespace.PREFIX + ":explainStatement";
    public static final String TYPE_FETCH_STATEMENT 				= Namespace.PREFIX + ":fetchStatement";
    public static final String TYPE_LISTEN_STATEMENT 				= Namespace.PREFIX + ":listenStatement";
    public static final String TYPE_LOAD_STATEMENT 					= Namespace.PREFIX + ":loadStatement";
    public static final String TYPE_LOCK_TABLE_STATEMENT 			= Namespace.PREFIX + ":lockStatement";
    public static final String TYPE_MOVE_STATEMENT 					= Namespace.PREFIX + ":moveStatement";
    public static final String TYPE_NOTIFY_STATEMENT 				= Namespace.PREFIX + ":notifyStatement";
    public static final String TYPE_PREPARE_STATEMENT 				= Namespace.PREFIX + ":prepareStatement";
    public static final String TYPE_REASSIGN_OWNED_STATEMENT 		= Namespace.PREFIX + ":reassignOwnedStatement";
    public static final String TYPE_RELEASE_SAVEPOINT_STATEMENT 	= Namespace.PREFIX + ":releaseSavepointStatement";
    public static final String TYPE_ROLLBACK_STATEMENT 				= Namespace.PREFIX + ":rollbackStatement";
    public static final String TYPE_SELECT_INTO_STATEMENT 			= Namespace.PREFIX + ":selectIntoStatement";
    public static final String TYPE_SHOW_STATEMENT 					= Namespace.PREFIX + ":showStatement";
    public static final String TYPE_TRUNCATE_STATEMENT 				= Namespace.PREFIX + ":truncateStatement";
    public static final String TYPE_UNLISTEN_STATEMENT 				= Namespace.PREFIX + ":unlistenStatement";
    public static final String TYPE_VACUUM_STATEMENT 				= Namespace.PREFIX + ":vacuumStatement";
    public static final String TYPE_COMMIT_STATEMENT 				= Namespace.PREFIX + ":commitStatement";

    public static final String TYPE_GRANT_ON_SEQUENCE_STATEMENT       = Namespace.PREFIX + ":grantOnSequenceStatement";
    public static final String TYPE_GRANT_ON_DATABASE_STATEMENT       = Namespace.PREFIX + ":grantOnDatabaseStatement";
    public static final String TYPE_GRANT_ON_FOREIGN_DATA_WRAPPER_STATEMENT  = Namespace.PREFIX + ":grantOnForeignDataWrapperStatement";
    public static final String TYPE_GRANT_ON_FOREIGN_SERVER_STATEMENT = Namespace.PREFIX + ":grantOnForeignServerStatement";
    public static final String TYPE_GRANT_ON_FUNCTION_STATEMENT       = Namespace.PREFIX + ":grantOnFunctionStatement";
    public static final String TYPE_GRANT_ON_LANGUAGE_STATEMENT       = Namespace.PREFIX + ":grantOnLanguageStatement";
    public static final String TYPE_GRANT_ON_SCHEMA_STATEMENT         = Namespace.PREFIX + ":grantOnSchemaStatement";
    public static final String TYPE_GRANT_ON_TABLESPACE_STATEMENT     = Namespace.PREFIX + ":grantOnTablespaceStatement";
    public static final String TYPE_GRANT_ROLES_STATEMENT             = Namespace.PREFIX + ":grantRolesStatement";

    public static final String TYPE_REVOKE_ON_SCHEMA_STATEMENT = PREFIX + ":revokeOnSchemaStatement";

    public static final String TYPE_RENAME_COLUMN 					  = Namespace.PREFIX + ":renamedColumn";

    public static final String SCHEMA_NAME 							  = Namespace.PREFIX + ":schemaName";
    public static final String TYPE_FUNCTION_PARAMETER                = Namespace.PREFIX + ":functionParameter";
    public static final String FUNCTION_PARAMETER_MODE                = Namespace.PREFIX + ":mode";
    public static final String ROLE                                   = Namespace.PREFIX + ":role";

    // PROPERTY NAMES
    public static final String TARGET_OBJECT_TYPE = Namespace.PREFIX + ":targetObjectType";
    public static final String TARGET_OBJECT_NAME = Namespace.PREFIX + ":targetObjectName";
    public static final String COMMENT            = Namespace.PREFIX + ":comment";

    // table
    public static final String TRANSIENT_TABLE       = Namespace.PREFIX + ":transient";
    public static final String STAGE_FILE_FORMAT     = Namespace.PREFIX + ":stageFileFormat";
    public static final String STAGE_COPY_OPTIONS    = Namespace.PREFIX + ":stageCopyOptions";
    public static final String DATA_RETENTION        = Namespace.PREFIX + ":dataRetention";
    public static final String MAX_DATA_EXTENSION    = Namespace.PREFIX + ":maxDataExtension";
    public static final String CHANGE_TRACKING       = Namespace.PREFIX + ":changeTracking";
    public static final String DEFAULT_DDL_COLLATION = Namespace.PREFIX + ":defaultDdlCollation";
    public static final String CLUSTER_BY            = Namespace.PREFIX + ":clusterBy";
    public static final String RENAME_TO             = Namespace.PREFIX + ":renameTo";
    public static final String SWAP_WITH             = Namespace.PREFIX + ":swapWith";

    // extenal table
    public static final String INTEGRATION       = Namespace.PREFIX + ":integration";
    public static final String PARTITION_BY      = Namespace.PREFIX + ":partitionBy";
    public static final String LOCATION          = Namespace.PREFIX + ":location";
    public static final String REFRESH_ON_CREATE = Namespace.PREFIX + ":refreshOnCreate";
    public static final String AUTO_REFRESH      = Namespace.PREFIX + ":autoRefresh";
    public static final String PATTERN           = Namespace.PREFIX + ":pattern";
    public static final String FILE_FORMAT       = Namespace.PREFIX + ":fileFormat";
    public static final String AWS_SNS_TOPIC     = Namespace.PREFIX + ":awsSnsTopic";

    // view
    public static final String SECURE    = Namespace.PREFIX + ":secure";
    public static final String RECURSIVE = Namespace.PREFIX + ":recursive";

    public static final String UNIQUE_INDEX = Namespace.PREFIX + ":unique";
    public static final String CONCURRENTLY = Namespace.PREFIX + ":concurrently";
    public static final String USING        = Namespace.PREFIX + ":using";
    public static final String TABLE_NAME   = Namespace.PREFIX + ":tableName";
    public static final String OTHER_REFS   = Namespace.PREFIX + ":otherRefs";
    public static final String TABLESPACE   = Namespace.PREFIX + ":tablespace";
    public static final String INDEX_ORDER  = Namespace.PREFIX + ":order";
    public static final String UNLOGGED     = Namespace.PREFIX + ":unlogged";
    public static final String MATERIALIZED = Namespace.PREFIX + ":materialized";

    // sequence properties
    public static final String SEQ_INCREMENT_BY   = Namespace.PREFIX + ":incrementBy";
    public static final String SEQ_START_WITH     = Namespace.PREFIX + ":startWith";
    public static final String SEQ_MAX_VALUE      = Namespace.PREFIX + ":maxValue";
    public static final String SEQ_NO_MAX_VALUE   = Namespace.PREFIX + ":noMaxValue";
    public static final String SEQ_MIN_VALUE      = Namespace.PREFIX + ":minValue";
    public static final String SEQ_NO_MIN_VALUE   = Namespace.PREFIX + ":noMinValue";
    public static final String SEQ_CYCLE          = Namespace.PREFIX + ":cycle";
    public static final String SEQ_CACHE          = Namespace.PREFIX + ":cache";
    public static final String SEQ_OWNED_BY       = Namespace.PREFIX + ":ownedBy";

    // column
    public static final String STORAGE               = Namespace.PREFIX + ":storage";
    public static final String DROP_DEFAULT          = Namespace.PREFIX + ":dropDefault";
    public static final String SET_DEFAULT           = Namespace.PREFIX + ":setDefault";
    public static final String SET_NOT_NULL          = Namespace.PREFIX + ":setNotNull";
    public static final String DROP_NOT_NULL         = Namespace.PREFIX + ":dropNotNull";
    public static final String USING_EXPRESSION      = Namespace.PREFIX + ":usingExpression";
    public static final String STATISTICS_TABLESPACE = Namespace.PREFIX + ":statisticsTablespace";
    public static final String STATISTICS_VALUE      = Namespace.PREFIX + ":statisticsValue";
}
