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
package org.modeshape.sequencer.ddl.dialect.amazon;

import org.modeshape.sequencer.ddl.StandardDdlLexicon;

import static org.modeshape.sequencer.ddl.StandardDdlLexicon.Namespace.PREFIX;

/**
 *
 */
public class RedshiftDdlLexicon extends StandardDdlLexicon {

    public static class Namespace {
        public static final String URI = "http://vertabelo.com/ddl/redshift/1.0";
        public static final String PREFIX = "redshiftddl";
    }

    public static final String TYPE_CREATE_DATABASE_STATEMENT = Namespace.PREFIX + ":createDatabaseStatement";
    public static final String TYPE_CREATE_FOREIGN_DATA_WRAPPER_STATEMENT = Namespace.PREFIX + ":createForeignDataWrapperStatement";
    public static final String TYPE_CREATE_FUNCTION_STATEMENT = Namespace.PREFIX + ":createFunctionStatement";
    public static final String TYPE_CREATE_GROUP_STATEMENT = Namespace.PREFIX + ":createGroupStatement";
    public static final String TYPE_CREATE_LIBRARY_STATEMENT = Namespace.PREFIX + ":createLibraryStatement";
    public static final String TYPE_CREATE_PROCEDURE_STATEMENT = Namespace.PREFIX + ":createProcedureStatement";
    public static final String TYPE_CREATE_USER_STATEMENT = Namespace.PREFIX + ":createUserStatement";


    public static final String TYPE_DROP_DATABASE_STATEMENT = Namespace.PREFIX + ":dropDatabaseStatement";
    public static final String TYPE_DROP_FUNCTION_STATEMENT = Namespace.PREFIX + ":dropFunctionStatement";
    public static final String TYPE_DROP_GROUP_STATEMENT = Namespace.PREFIX + ":dropGroupStatement";
    public static final String TYPE_DROP_LIBRARY_STATEMENT = Namespace.PREFIX + ":dropLanguageStatement";
    public static final String TYPE_DROP_PROCEDURE_STATEMENT = Namespace.PREFIX + ":dropProcedureStatement";
    public static final String TYPE_DROP_USER_STATEMENT = Namespace.PREFIX + ":dropUserStatement";

    public static final String TYPE_ALTER_DATABASE_STATEMENT = Namespace.PREFIX + ":alterDatabaseStatement";
    public static final String TYPE_ALTER_GROUP_STATEMENT = Namespace.PREFIX + ":alterGroupStatement";
    public static final String TYPE_ALTER_LIBRARY_STATEMENT = Namespace.PREFIX + ":alterLibraryStatement";
    public static final String TYPE_ALTER_PROCEDURE_STATEMENT = Namespace.PREFIX + ":alterProcedureStatement";
    public static final String TYPE_ALTER_SCHEMA_STATEMENT = Namespace.PREFIX + ":alterSchemaStatement";
    public static final String TYPE_ALTER_USER_STATEMENT = Namespace.PREFIX + ":alterUserStatement";
    public static final String TYPE_ALTER_VIEW_STATEMENT = Namespace.PREFIX + ":alterViewStatement";

    // This is required to attach additional properties
    public static final String TYPE_ALTER_TABLE_STATEMENT = Namespace.PREFIX + ":alterTableStatement";


    public static final String TYPE_ABORT_STATEMENT = Namespace.PREFIX + ":abortStatement";
    public static final String TYPE_ANALYZE_STATEMENT = Namespace.PREFIX + ":analyzeStatement";
    public static final String TYPE_COMMENT_ON_STATEMENT = Namespace.PREFIX + ":commentOnStatement";
    public static final String TYPE_COPY_STATEMENT = Namespace.PREFIX + ":copyStatement";
    public static final String TYPE_DEALLOCATE_STATEMENT = Namespace.PREFIX + ":deallocateStatement";
    public static final String TYPE_DECLARE_STATEMENT = Namespace.PREFIX + ":declareStatement";
    public static final String TYPE_EXPLAIN_STATEMENT = Namespace.PREFIX + ":explainStatement";
    public static final String TYPE_FETCH_STATEMENT = Namespace.PREFIX + ":fetchStatement";
    public static final String TYPE_LOCK_TABLE_STATEMENT = Namespace.PREFIX + ":lockStatement";
    public static final String TYPE_PREPARE_STATEMENT = Namespace.PREFIX + ":prepareStatement";
    public static final String TYPE_ROLLBACK_STATEMENT = Namespace.PREFIX + ":rollbackStatement";
    public static final String TYPE_SELECT_INTO_STATEMENT = Namespace.PREFIX + ":selectIntoStatement";
    public static final String TYPE_SHOW_STATEMENT = Namespace.PREFIX + ":showStatement";
    public static final String TYPE_TRUNCATE_STATEMENT = Namespace.PREFIX + ":truncateStatement";
    public static final String TYPE_UNLISTEN_STATEMENT = Namespace.PREFIX + ":unlistenStatement";
    public static final String TYPE_VACUUM_STATEMENT = Namespace.PREFIX + ":vacuumStatement";
    public static final String TYPE_COMMIT_STATEMENT = Namespace.PREFIX + ":commitStatement";

    public static final String TYPE_GRANT_ON_DATABASE_STATEMENT = Namespace.PREFIX + ":grantOnDatabaseStatement";
    public static final String TYPE_GRANT_ON_FOREIGN_DATA_WRAPPER_STATEMENT = Namespace.PREFIX + ":grantOnForeignDataWrapperStatement";
    public static final String TYPE_GRANT_ON_FOREIGN_SERVER_STATEMENT = Namespace.PREFIX + ":grantOnForeignServerStatement";
    public static final String TYPE_GRANT_ON_FUNCTION_STATEMENT = Namespace.PREFIX + ":grantOnFunctionStatement";
    public static final String TYPE_GRANT_ON_LANGUAGE_STATEMENT = Namespace.PREFIX + ":grantOnLanguageStatement";
    public static final String TYPE_GRANT_ON_SCHEMA_STATEMENT = Namespace.PREFIX + ":grantOnSchemaStatement";
    public static final String TYPE_GRANT_ROLES_STATEMENT = Namespace.PREFIX + ":grantRolesStatement";

    public static final String TYPE_REVOKE_ON_SCHEMA_STATEMENT = PREFIX + ":revokeOnSchemaStatement";

    public static final String TYPE_RENAME_COLUMN = Namespace.PREFIX + ":renamedColumn";

    public static final String SCHEMA_NAME = Namespace.PREFIX + ":schemaName";
    public static final String TYPE_FUNCTION_PARAMETER = Namespace.PREFIX + ":functionParameter";
    public static final String FUNCTION_PARAMETER_MODE = Namespace.PREFIX + ":mode";
    public static final String ROLE = Namespace.PREFIX + ":role";

    // PROPERTY NAMES
    public static final String TARGET_OBJECT_TYPE = Namespace.PREFIX + ":targetObjectType";
    public static final String TARGET_OBJECT_NAME = Namespace.PREFIX + ":targetObjectName";
    public static final String COMMENT = Namespace.PREFIX + ":comment";

    // table property
    public static final String DISTSTYLE = Namespace.PREFIX + ":diststyle";
    public static final String DISTKEY = Namespace.PREFIX + ":distkey";
    public static final String COMPOUND_SORTKEY = Namespace.PREFIX + ":compoundSortKey";
    public static final String INTERLEAVED_SORTKEY = Namespace.PREFIX + ":interleavedSortKey";
    public static final String BACKUP = Namespace.PREFIX + ":backup";

    // column property
    public static final String IDENTITY = Namespace.PREFIX + ":identity";
    public static final String IDENTITY_SEED = Namespace.PREFIX + ":identitySeed";
    public static final String IDENTITY_STEP = Namespace.PREFIX + ":identityStep";
    public static final String ENCODING = Namespace.PREFIX + ":encoding";


    public static final String UNIQUE_INDEX = Namespace.PREFIX + ":unique";
    public static final String USING = Namespace.PREFIX + ":using";
    public static final String TABLE_NAME = Namespace.PREFIX + ":tableName";
    public static final String TABLESPACE = Namespace.PREFIX + ":tablespace";
    public static final String INDEX_ORDER = Namespace.PREFIX + ":order";

    public static final String MATERIALIZED = Namespace.PREFIX + ":materialized";
    public static final String WITH_CHECK_OPTION = Namespace.PREFIX + ":withCheckOption";
}


