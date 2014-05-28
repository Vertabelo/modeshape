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

import java.util.Arrays;
import java.util.List;

import org.modeshape.sequencer.ddl.DdlConstants;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;

/**
 * SqlServer-specific constants including key words and statement start phrases.
 * 
 * @author blafond
 * @author Michał Kołodziejski
 */
public interface SqlServerDdlConstants extends DdlConstants {
    
    // the list taken from: http://technet.microsoft.com/en-us/library/ms189822.aspx
    public static final String[] CUSTOM_KEYWORDS = {
        "ADD", "EXTERNAL", "PROCEDURE", "ALL", "FETCH", "PUBLIC", "ALTER", "FILE", "RAISERROR", "AND", "FILLFACTOR", 
        "READ", "ANY", "FOR", "READTEXT", "AS", "FOREIGN", "RECONFIGURE", "ASC", "FREETEXT", "REFERENCES", 
        "AUTHORIZATION", "FREETEXTTABLE", "REPLICATION", "BACKUP", "FROM", "RESTORE", "BEGIN", "FULL", "RESTRICT", 
        "BETWEEN", "FUNCTION", "RETURN", "BREAK", "GOTO", "REVERT", "BROWSE", "GRANT", "REVOKE", "BULK", "GROUP", 
        "RIGHT", "BY", "HAVING", "ROLLBACK", "CASCADE", "HOLDLOCK", "ROWCOUNT", "CASE", "IDENTITY", "ROWGUIDCOL", 
        "CHECK", "IDENTITY_INSERT", "RULE", "CHECKPOINT", "IDENTITYCOL", "SAVE", "CLOSE", "IF", "SCHEMA", "CLUSTERED",
        "IN", "SECURITYAUDIT", "COALESCE", "INDEX", "SELECT", "COLLATE", "INNER", "SEMANTICKEYPHRASETABLE", "COLUMN", 
        "INSERT", "SEMANTICSIMILARITYDETAILSTABLE", "COMMIT", "INTERSECT", "SEMANTICSIMILARITYTABLE", "COMPUTE", "INTO",
        "SESSION_USER", "CONSTRAINT", "IS", "SET", "CONTAINS", "JOIN", "SETUSER", "CONTAINSTABLE", "KEY", "SHUTDOWN", 
        "CONTINUE", "KILL", "SOME", "CONVERT", "LEFT", "STATISTICS", "CREATE", "LIKE", "SYSTEM_USER", "CROSS", "LINENO",
        "TABLE", "CURRENT", "LOAD", "TABLESAMPLE", "CURRENT_DATE", "MERGE", "TEXTSIZE", "CURRENT_TIME", "NATIONAL",
        "THEN", "CURRENT_TIMESTAMP", "NOCHECK", "TO", "CURRENT_USER", "NONCLUSTERED", "TOP", "CURSOR", "NOT", "TRAN",
        "DATABASE", "NULL", "TRANSACTION", "DBCC", "NULLIF", "TRIGGER", "DEALLOCATE", "OF", "TRUNCATE", "DECLARE", 
        "OFF", "TRY_CONVERT", "DEFAULT", "OFFSETS", "TSEQUAL", "DELETE", "ON", "UNION", "DENY", "OPEN", "UNIQUE", 
        "DESC", "OPENDATASOURCE", "UNPIVOT", "DISK", "OPENQUERY", "UPDATE", "DISTINCT", "OPENROWSET", "UPDATETEXT", 
        "DISTRIBUTED", "OPENXML", "USE", "DOUBLE", "OPTION", "USER", "DROP", "OR", "VALUES", "DUMP", "ORDER", "VARYING",
        "ELSE", "OUTER", "VIEW", "END", "OVER", "WAITFOR", "ERRLVL", "PERCENT", "WHEN", "ESCAPE", "PIVOT", "WHERE", 
        "EXCEPT", "PLAN", "WHILE", "EXEC", "PRECISION", "WITH", "EXECUTE", "PRIMARY", "WITHIN GROUP", "EXISTS", "PRINT",
        "WRITETEXT", "EXIT", "PROC"
    };
    
    interface SqlServerStatementStartPhrases {
        static final String[] STMT_ALTER_APPLICATION_ROLE = {ALTER, "APPLICATION", "ROLE"};
        static final String[] STMT_ALTER_ASSEMBLY = {ALTER, "ASSEMBLY"};
        static final String[] STMT_ALTER_ASYMMETRIC_KEY = {ALTER, "ASYMMETRIC", "KEY"};
        static final String[] STMT_ALTER_AUTHORIZATION = {ALTER, "AUTHORIZATION"};
        static final String[] STMT_ALTER_BROKER_PRIORITY = {ALTER, "BROKER", "PRIORITY"};
        static final String[] STMT_ALTER_CERTIFICATE = {ALTER, "CERTIFICATE"};
        static final String[] STMT_ALTER_CREDENTIAL = {ALTER, "CREDENTIAL"};
        static final String[] STMT_ALTER_CRYPTOGRAPHIC_PROVIDER = {ALTER, "CRYPTOGRAPHIC", "PROVIDER"};
        static final String[] STMT_ALTER_DATABASE = {ALTER, "DATABASE"};
        static final String[] STMT_ALTER_ENDPOINT = {ALTER, "ENDPOINT"};
        static final String[] STMT_ALTER_EVENT_SESSION = {ALTER, "EVENT", "SESSION"};
        static final String[] STMT_ALTER_FULLTEXT_CATALOG = {ALTER, "FULLTEXT", "CATALOG"};
        static final String[] STMT_ALTER_FULLTEXT_INDEX = {ALTER, "FULLTEXT", "INDEX"};
        static final String[] STMT_ALTER_FULLTEXT_STOPLIST = {ALTER, "FULLTEXT", "STOPLIST"};
        static final String[] STMT_ALTER_FUNCTION = {ALTER, "FUNCTION"};
        static final String[] STMT_ALTER_INDEX = {ALTER, "INDEX"};
        static final String[] STMT_ALTER_LOGIN = {ALTER, "LOGIN"};
        static final String[] STMT_ALTER_MASTER_KEY = {ALTER, "MASTER", "KEY"};
        static final String[] STMT_ALTER_MESSAGE_TYPE = {ALTER, "MESSAGE", "TYPE"};
        static final String[] STMT_ALTER_PARTITION_FUNCTION = {ALTER, "PARTITION", "FUNCTION"};
        static final String[] STMT_ALTER_PARTITION_SCHEME = {ALTER, "PARTITION", "SCHEME"};
        // ALTER { PROC | PROCEDURE }
        static final String[] STMT_ALTER_PROCEDURE = {ALTER, "PROCEDURE"};
        static final String[] STMT_ALTER_PROC = {ALTER, "PROC"};
        static final String[] STMT_ALTER_QUEUE = {ALTER, "QUEUE"};
        static final String[] STMT_ALTER_REMOTE_SERVICE_BINDING = {ALTER, "REMOTE", "SERVICE", "BINDING"};
        static final String[] STMT_ALTER_RESOURCE_GOVERNOR = {ALTER, "RESOURCE", "GOVERNOR"};
        static final String[] STMT_ALTER_RESOURCE_POOL = {ALTER, "RESOURCE", "POOL"};
        static final String[] STMT_ALTER_ROLE = {ALTER, "ROLE"};
        static final String[] STMT_ALTER_ROUTE = {ALTER, "ROUTE"};
        static final String[] STMT_ALTER_SCHEMA = {ALTER, "SCHEMA"};
        static final String[] STMT_ALTER_SEARCH_PROPERTY_LIST = {ALTER, "SEARCH", "PROPERTY", "LIST"};
        static final String[] STMT_ALTER_SEQUENCE = {ALTER, "SEQUENCE"};
        static final String[] STMT_ALTER_SERVER_AUDIT = {ALTER, "SERVER", "AUDIT"};
        static final String[] STMT_ALTER_SERVER_AUDIT_SPECIFICATION = {ALTER, "SERVER", "AUDIT", "SPECIFICATION"};
        static final String[] STMT_ALTER_SERVICE = {ALTER, "SERVICE"};
        static final String[] STMT_ALTER_SERVICE_MASTER_KEY = {ALTER, "SERVICE", "MASTER", "KEY"};
        static final String[] STMT_ALTER_SYMMETRIC_KEY = {ALTER, "SYMMETRIC", "KEY"};
        static final String[] STMT_ALTER_TABLE = {ALTER, "TABLE"};
        static final String[] STMT_ALTER_TRIGGER = {ALTER, "TRIGGER"};
        static final String[] STMT_ALTER_USER = {ALTER, "USER"};
        static final String[] STMT_ALTER_VIEW = {ALTER, "VIEW"};
        static final String[] STMT_ALTER_WORKLOAD_GROUP = {ALTER, "WORKLOAD", "GROUP"};
        static final String[] STMT_ALTER_XML_SCHEMA_COLLECTION = {ALTER, "XML", "SCHEMA", "COLLECTION", ""};

        
        static final String[][] ALTER_PHRASES = { 
            STMT_ALTER_APPLICATION_ROLE, STMT_ALTER_ASSEMBLY, STMT_ALTER_ASYMMETRIC_KEY, STMT_ALTER_AUTHORIZATION, 
            STMT_ALTER_BROKER_PRIORITY, STMT_ALTER_CERTIFICATE, STMT_ALTER_CREDENTIAL, STMT_ALTER_CRYPTOGRAPHIC_PROVIDER,
            STMT_ALTER_DATABASE, STMT_ALTER_ENDPOINT, STMT_ALTER_EVENT_SESSION, STMT_ALTER_FULLTEXT_CATALOG, 
            STMT_ALTER_FULLTEXT_INDEX, STMT_ALTER_FULLTEXT_STOPLIST, STMT_ALTER_FUNCTION, STMT_ALTER_INDEX, 
            STMT_ALTER_LOGIN, STMT_ALTER_MASTER_KEY, STMT_ALTER_MESSAGE_TYPE, STMT_ALTER_PARTITION_FUNCTION, 
            STMT_ALTER_PARTITION_SCHEME, STMT_ALTER_PROCEDURE, STMT_ALTER_PROC, STMT_ALTER_QUEUE, 
            STMT_ALTER_REMOTE_SERVICE_BINDING, STMT_ALTER_RESOURCE_GOVERNOR, STMT_ALTER_RESOURCE_POOL, STMT_ALTER_ROLE, 
            STMT_ALTER_ROUTE, STMT_ALTER_SCHEMA, STMT_ALTER_SEARCH_PROPERTY_LIST, STMT_ALTER_SEQUENCE, STMT_ALTER_SERVER_AUDIT,
            STMT_ALTER_SERVER_AUDIT_SPECIFICATION, STMT_ALTER_SERVICE, STMT_ALTER_SERVICE_MASTER_KEY, 
            STMT_ALTER_SYMMETRIC_KEY, STMT_ALTER_TABLE, STMT_ALTER_TRIGGER, STMT_ALTER_USER, STMT_ALTER_VIEW, 
            STMT_ALTER_WORKLOAD_GROUP, STMT_ALTER_XML_SCHEMA_COLLECTION
        };
        
        static final String[] STMT_ANALYZE = {"ANALYZE"};
        static final String[] STMT_ASSOCIATE_STATISTICS = {"ASSOCIATE", "STATISTICS"};
        static final String[] STMT_AUDIT = {"AUDIT"};
        
        
        static final String[] STMT_CREATE_AGGREGATE = {CREATE, "AGGREGATE"};
        static final String[] STMT_CREATE_APPLICATION_ROLE = {CREATE, "APPLICATION", "ROLE"};
        static final String[] STMT_CREATE_ASSEMBLY = {CREATE, "ASSEMBLY"};
        static final String[] STMT_CREATE_ASYMMETRIC_KEY = {CREATE, "ASYMMETRIC", "KEY"};
        static final String[] STMT_CREATE_BROKER_PRIORITY = {CREATE, "BROKER", "PRIORITY"};
        static final String[] STMT_CREATE_CERTIFICATE = {CREATE, "CERTIFICATE"};
        // CREATE [ NONCLUSTERED ] COLUMNSTORE INDEX
        static final String[] STMT_CREATE_NONCLUSTERED_COLUMNSTORE_INDEX = {CREATE, "NONCLUSTERED", "COLUMNSTORE", "INDEX"};
        static final String[] STMT_CREATE_COLUMNSTORE_INDEX = {CREATE, "COLUMNSTORE", "INDEX"};
        static final String[] STMT_CREATE_CONTRACT = {CREATE, "CONTRACT"};
        static final String[] STMT_CREATE_CREDENTIAL = {CREATE, "CREDENTIAL"};
        static final String[] STMT_CREATE_CRYPTOGRAPHIC_PROVIDER = {CREATE, "CRYPTOGRAPHIC", "PROVIDER"};
        static final String[] STMT_CREATE_DATABASE_AUDIT_SPECIFICATION = {CREATE, "DATABASE", "AUDIT", "SPECIFICATION"};
        static final String[] STMT_CREATE_DATABASE_ENCRYPTION_KEY = {CREATE, "DATABASE", "ENCRYPTION", "KEY"};
        static final String[] STMT_CREATE_DATABASE = {CREATE, "DATABASE"};
        static final String[] STMT_CREATE_DEFAULT = {CREATE, "DEFAULT"};
        static final String[] STMT_CREATE_ENDPOINT = {CREATE, "ENDPOINT"};
        static final String[] STMT_CREATE_EVENT_NOTIFICATION = {CREATE, "EVENT", "NOTIFICATION"};
        static final String[] STMT_CREATE_EVENT_SESSION = {CREATE, "EVENT", "SESSION"};
        static final String[] STMT_CREATE_FULLTEXT_CATALOG = {CREATE, "FULLTEXT", "CATALOG"};
        static final String[] STMT_CREATE_FULLTEXT_INDEX = {CREATE, "FULLTEXT", "INDEX"};
        static final String[] STMT_CREATE_FULLTEXT_STOPLIST = {CREATE, "FULLTEXT", "STOPLIST"};
        static final String[] STMT_CREATE_FUNCTION = {CREATE, "FUNCTION"};
        // CREATE [ UNIQUE ] [ CLUSTERED | NONCLUSTERED ] INDEX 
        static final String[] STMT_CREATE_INDEX = {CREATE, "INDEX"};
        static final String[] STMT_CREATE_UNIQUE_INDEX = {CREATE, "UNIQUE", "INDEX"};
        static final String[] STMT_CREATE_CLUSTERED_INDEX = {CREATE, "CLUSTERED", "INDEX"};
        static final String[] STMT_CREATE_NONCLUSTERED_INDEX = {CREATE, "NONCLUSTERED", "INDEX"};
        static final String[] STMT_CREATE_UNIQUE_CLUSTERED_INDEX = {CREATE, "UNIQUE", "CLUSTERED", "INDEX"};
        static final String[] STMT_CREATE_UNIQUE_NONCLUSTERED_INDEX = {CREATE, "UNIQUE", "NONCLUSTERED", "INDEX"};
        static final String[] STMT_CREATE_LOGIN = {CREATE, "LOGIN"};
        static final String[] STMT_CREATE_MASTER_KEY = {CREATE, "MASTER", "KEY"};
        static final String[] STMT_CREATE_MESSAGE_TYPE = {CREATE, "MESSAGE", "TYPE"};
        static final String[] STMT_CREATE_PARTITION_FUNCTION = {CREATE, "PARTITION", "FUNCTION"};
        static final String[] STMT_CREATE_PARTITION_SCHEME = {CREATE, "PARTITION", "SCHEME"};
        static final String[] STMT_CREATE_PROCEDURE = {CREATE, "PROCEDURE"};
        static final String[] STMT_CREATE_PROC = {CREATE, "PROC"};
        static final String[] STMT_CREATE_QUEUE = {CREATE, "QUEUE"};
        static final String[] STMT_CREATE_REMOTE_SERVICE_BINDING = {CREATE, "REMOTE", "SERVICE", "BINDING"};
        static final String[] STMT_CREATE_RESOURCE_POOL = {CREATE, "RESOURCE", "POOL"};
        static final String[] STMT_CREATE_ROLE = {CREATE, "ROLE"};
        static final String[] STMT_CREATE_ROUTE = {CREATE, "ROUTE"};
        static final String[] STMT_CREATE_RULE = {CREATE, "RULE"};
        static final String[] STMT_CREATE_SCHEMA = {CREATE, "SCHEMA"};
        static final String[] STMT_CREATE_SEARCH_PROPERTY_LIST = {CREATE, "SEARCH", "PROPERTY", "LIST"};
        static final String[] STMT_CREATE_SEQUENCE = {CREATE, "SEQUENCE"};
        static final String[] STMT_CREATE_SERVER_AUDIT_SPECIFICATION = {CREATE, "SERVER", "AUDIT", "SPECIFICATION"};
        static final String[] STMT_CREATE_SERVER_AUDIT = {CREATE, "SERVER", "AUDIT"};
        static final String[] STMT_CREATE_SERVICE = {CREATE, "SERVICE"};
        static final String[] STMT_CREATE_SPATIAL_INDEX = {CREATE, "SPATIAL", "INDEX"};
        static final String[] STMT_CREATE_STATISTICS = {CREATE, "STATISTICS"};
        static final String[] STMT_CREATE_SYMMETRIC_KEY = {CREATE, "SYMMETRIC", "KEY"};
        static final String[] STMT_CREATE_SYNONYM = {CREATE, "SYNONYM"};
        static final String[] STMT_CREATE_TABLE = {CREATE, "TABLE"};
        static final String[] STMT_CREATE_TRIGGER = {CREATE, "TRIGGER"};
        static final String[] STMT_CREATE_TYPE = {CREATE, "TYPE"};
        static final String[] STMT_CREATE_USER = {CREATE, "USER"};
        static final String[] STMT_CREATE_VIEW = {CREATE, "VIEW"};
        static final String[] STMT_CREATE_WORKLOAD_GROUP = {CREATE, "WORKLOAD", "GROUP"};
        // CREATE [ PRIMARY ] XML INDEX
        static final String[] STMT_CREATE_XML_INDEX = {CREATE, "XML", "INDEX"};
        static final String[] STMT_CREATE_PRIMARY_XML_INDEX = {CREATE, "PRIMARY", "XML", "INDEX"};
        static final String[] STMT_CREATE_XML_SCHEMA_COLLECTION = {CREATE, "XML", "SCHEMA", "COLLECTION"};

    
        public static final String[][] CREATE_PHRASES = {
            STMT_CREATE_AGGREGATE, STMT_CREATE_APPLICATION_ROLE, STMT_CREATE_ASSEMBLY, STMT_CREATE_ASYMMETRIC_KEY, 
            STMT_CREATE_BROKER_PRIORITY, STMT_CREATE_CERTIFICATE, STMT_CREATE_NONCLUSTERED_COLUMNSTORE_INDEX, 
            STMT_CREATE_COLUMNSTORE_INDEX, STMT_CREATE_CONTRACT, STMT_CREATE_CREDENTIAL, 
            STMT_CREATE_CRYPTOGRAPHIC_PROVIDER, STMT_CREATE_DATABASE_AUDIT_SPECIFICATION, 
            STMT_CREATE_DATABASE_ENCRYPTION_KEY, STMT_CREATE_DATABASE, STMT_CREATE_DEFAULT, STMT_CREATE_ENDPOINT, 
            STMT_CREATE_EVENT_NOTIFICATION, STMT_CREATE_EVENT_SESSION, STMT_CREATE_FULLTEXT_CATALOG, 
            STMT_CREATE_FULLTEXT_INDEX, STMT_CREATE_FULLTEXT_STOPLIST, STMT_CREATE_FUNCTION, STMT_CREATE_INDEX,
            STMT_CREATE_UNIQUE_INDEX, STMT_CREATE_CLUSTERED_INDEX, STMT_CREATE_NONCLUSTERED_INDEX, 
            STMT_CREATE_UNIQUE_CLUSTERED_INDEX, STMT_CREATE_UNIQUE_NONCLUSTERED_INDEX, STMT_CREATE_LOGIN, 
            STMT_CREATE_MASTER_KEY, STMT_CREATE_MESSAGE_TYPE, STMT_CREATE_PARTITION_FUNCTION, STMT_CREATE_PARTITION_SCHEME, 
            STMT_CREATE_PROCEDURE, STMT_CREATE_PROC, STMT_CREATE_QUEUE, STMT_CREATE_REMOTE_SERVICE_BINDING,
            STMT_CREATE_RESOURCE_POOL, STMT_CREATE_ROLE, STMT_CREATE_ROUTE, STMT_CREATE_RULE, STMT_CREATE_SCHEMA, 
            STMT_CREATE_SEARCH_PROPERTY_LIST, STMT_CREATE_SEQUENCE, STMT_CREATE_SERVER_AUDIT_SPECIFICATION,
            STMT_CREATE_SERVER_AUDIT, STMT_CREATE_SERVICE, STMT_CREATE_SPATIAL_INDEX, STMT_CREATE_STATISTICS, 
            STMT_CREATE_SYMMETRIC_KEY, STMT_CREATE_SYNONYM, STMT_CREATE_TABLE, STMT_CREATE_TRIGGER, STMT_CREATE_TYPE, 
            STMT_CREATE_USER, STMT_CREATE_VIEW, STMT_CREATE_WORKLOAD_GROUP, STMT_CREATE_XML_INDEX, 
            STMT_CREATE_PRIMARY_XML_INDEX, STMT_CREATE_XML_SCHEMA_COLLECTION
        };
        
        static final String[] STMT_DROP_AGGREGATE = {DROP, "AGGREGATE"};
        static final String[] STMT_DROP_APPLICATION_ROLE = {DROP, "APPLICATION", "ROLE"};
        static final String[] STMT_DROP_ASSEMBLY = {DROP, "ASSEMBLY"};
        static final String[] STMT_DROP_ASYMMETRIC_KEY = {DROP, "ASYMMETRIC", "KEY"};
        static final String[] STMT_DROP_BROKER_PRIORITY = {DROP, "BROKER", "PRIORITY"};
        static final String[] STMT_DROP_CERTIFICATE = {DROP, "CERTIFICATE"};
        static final String[] STMT_DROP_CONTRACT = {DROP, "CONTRACT"};
        static final String[] STMT_DROP_CREDENTIAL = {DROP, "CREDENTIAL"};
        static final String[] STMT_DROP_CRYPTOGRAPHIC_PROVIDER = {DROP, "CRYPTOGRAPHIC", "PROVIDER"};
        static final String[] STMT_DROP_DATABASE = {DROP, "DATABASE"};
        static final String[] STMT_DROP_DATABASE_AUDIT_SPECIFICATION = {DROP, "DATABASE", "AUDIT", "SPECIFICATION"};
        static final String[] STMT_DROP_DATABASE_ENCRYPTION_KEY = {DROP, "DATABASE", "ENCRYPTION", "KEY"};
        static final String[] STMT_DROP_DEFAULT = {DROP, "DEFAULT"};
        static final String[] STMT_DROP_ENDPOINT = {DROP, "ENDPOINT"};
        static final String[] STMT_DROP_EVENT_NOTIFICATION = {DROP, "EVENT", "NOTIFICATION"};
        static final String[] STMT_DROP_EVENT_SESSION = {DROP, "EVENT", "SESSION"};
        static final String[] STMT_DROP_FULLTEXT_CATALOG = {DROP, "FULLTEXT", "CATALOG"};
        static final String[] STMT_DROP_FULLTEXT_INDEX = {DROP, "FULLTEXT", "INDEX"};
        static final String[] STMT_DROP_FULLTEXT_STOPLIST = {DROP, "FULLTEXT", "STOPLIST"};
        static final String[] STMT_DROP_FUNCTION = {DROP, "FUNCTION"};
        static final String[] STMT_DROP_INDEX = {DROP, "INDEX"};
        static final String[] STMT_DROP_LOGIN = {DROP, "LOGIN"};
        static final String[] STMT_DROP_MASTER_KEY = {DROP, "MASTER", "KEY"};
        static final String[] STMT_DROP_MESSAGE_TYPE = {DROP, "MESSAGE", "TYPE"};
        static final String[] STMT_DROP_PARTITION_FUNCTION = {DROP, "PARTITION", "FUNCTION"};
        static final String[] STMT_DROP_PARTITION_SCHEME = {DROP, "PARTITION", "SCHEME"};
        // DROP { PROC | PROCEDURE } { [ schema_name. ] procedure } [ ,...n ]
        static final String[] STMT_DROP_PROCEDURE = {DROP, "PROCEDURE"};
        static final String[] STMT_DROP_PROC = {DROP, "PROC"};
        static final String[] STMT_DROP_QUEUE = {DROP, "QUEUE"};
        static final String[] STMT_DROP_REMOTE_SERVICE_BINDING = {DROP, "REMOTE", "SERVICE", "BINDING"};
        static final String[] STMT_DROP_RESOURCE_POOL = {DROP, "RESOURCE", "POOL"};
        static final String[] STMT_DROP_ROLE = {DROP, "ROLE"};
        static final String[] STMT_DROP_ROUTE = {DROP, "ROUTE"};
        static final String[] STMT_DROP_RULE = {DROP, "RULE"};
        static final String[] STMT_DROP_SCHEMA = {DROP, "SCHEMA"};
        static final String[] STMT_DROP_SEARCH_PROPERTY_LIST = {DROP, "SEARCH", "PROPERTY", "LIST"};
        static final String[] STMT_DROP_SEQUENCE = {DROP, "SEQUENCE"};
        static final String[] STMT_DROP_SERVER_AUDIT = {DROP, "SERVER", "AUDIT"};
        static final String[] STMT_DROP_SERVER_AUDIT_SPECIFICATION = {DROP, "SERVER", "AUDIT", "SPECIFICATION"};
        static final String[] STMT_DROP_SERVICE = {DROP, "SERVICE"};
        // DROP [ COUNTER ] SIGNATURE FROM module_name 
        static final String[] STMT_DROP_SIGNATURE = {DROP, "SIGNATURE"};
        static final String[] STMT_DROP_COUNTER_SIGNATURE = {DROP, "COUNTER", "SIGNATURE"};
        static final String[] STMT_DROP_STATISTICS = {DROP, "STATISTICS"};
        static final String[] STMT_DROP_SYMMETRIC_KEY = {DROP, "SYMMETRIC", "KEY"};
        static final String[] STMT_DROP_SYNONYM = {DROP, "SYNONYM"};
        static final String[] STMT_DROP_TABLE = {DROP, "TABLE"};
        static final String[] STMT_DROP_TRIGGER = {DROP, "TRIGGER"};
        static final String[] STMT_DROP_TYPE = {DROP, "TYPE"};
        static final String[] STMT_DROP_USER = {DROP, "USER"};
        static final String[] STMT_DROP_VIEW = {DROP, "VIEW"};
        static final String[] STMT_DROP_WORKLOAD_GROUP = {DROP, "WORKLOAD", "GROUP"};
        static final String[] STMT_DROP_XML_SCHEMA_COLLECTION = {DROP, "XML", "SCHEMA", "COLLECTION"};

        
        static final String[][] DROP_PHRASES = {
            STMT_DROP_AGGREGATE, STMT_DROP_APPLICATION_ROLE, STMT_DROP_ASSEMBLY, STMT_DROP_ASYMMETRIC_KEY, 
            STMT_DROP_BROKER_PRIORITY, STMT_DROP_CERTIFICATE, STMT_DROP_CONTRACT, STMT_DROP_CREDENTIAL, 
            STMT_DROP_CRYPTOGRAPHIC_PROVIDER, STMT_DROP_DATABASE_AUDIT_SPECIFICATION, STMT_DROP_DATABASE_ENCRYPTION_KEY,
            STMT_DROP_DATABASE, STMT_DROP_DEFAULT, STMT_DROP_ENDPOINT, STMT_DROP_EVENT_NOTIFICATION, STMT_DROP_EVENT_SESSION,
            STMT_DROP_FULLTEXT_CATALOG, STMT_DROP_FULLTEXT_INDEX, STMT_DROP_FULLTEXT_STOPLIST, STMT_DROP_FUNCTION, 
            STMT_DROP_INDEX, STMT_DROP_LOGIN, STMT_DROP_MASTER_KEY, STMT_DROP_MESSAGE_TYPE, STMT_DROP_PARTITION_FUNCTION,
            STMT_DROP_PARTITION_SCHEME, STMT_DROP_PROCEDURE, STMT_DROP_PROC, STMT_DROP_QUEUE, STMT_DROP_REMOTE_SERVICE_BINDING,
            STMT_DROP_RESOURCE_POOL, STMT_DROP_ROLE, STMT_DROP_ROUTE, STMT_DROP_RULE, STMT_DROP_SCHEMA, 
            STMT_DROP_SEARCH_PROPERTY_LIST, STMT_DROP_SEQUENCE, STMT_DROP_SERVER_AUDIT_SPECIFICATION, STMT_DROP_SERVER_AUDIT,
            STMT_DROP_SERVICE, STMT_DROP_SIGNATURE, STMT_DROP_COUNTER_SIGNATURE, STMT_DROP_STATISTICS, STMT_DROP_SYMMETRIC_KEY,
            STMT_DROP_SYNONYM, STMT_DROP_TABLE, STMT_DROP_TRIGGER, STMT_DROP_TYPE, STMT_DROP_USER, STMT_DROP_VIEW, 
            STMT_DROP_WORKLOAD_GROUP, STMT_DROP_XML_SCHEMA_COLLECTION
        };
        
        static final String[] STMT_BEGIN_DISTRIBUTED_TRANSACTION = {"BEGIN", "DISTRIBUTED", "TRANSACTION"};
        static final String[] STMT_BEGIN_DISTRIBUTED_TRAN = {"BEGIN", "DISTRIBUTED", "TRAN"};
        static final String[] STMT_ROLLBACK_TRANSACTION = {"ROLLBACK", "TRANSACTION"};
        static final String[] STMT_ROLLBACK_TRAN = {"ROLLBACK", "TRAN"};
        static final String[] STMT_BEGIN_TRANSACTION = {"BEGIN", "TRANSACTION"};
        static final String[] STMT_BEGIN_TRAN = {"BEGIN", "TRAN"};
        static final String[] STMT_ROLLBACK_WORK = {"ROLLBACK", "WORK"};
        static final String[] STMT_ROLLBACK = {"ROLLBACK"};
        static final String[] STMT_COMMIT_TRANSACTION = {"COMMIT", "TRANSACTION"};
        static final String[] STMT_COMMIT_TRAN = {"COMMIT", "TRAN"};
        static final String[] STMT_SAVE_TRANSACTION = {"SAVE", "TRANSACTION"};
        static final String[] STMT_SAVE_TRAN = {"SAVE", "TRAN"};
        static final String[] STMT_COMMIT_WORK = {"COMMIT", "WORK"};
        static final String[] STMT_COMMIT = {"COMMIT"}; // FIXME for Oracle it is marked as "don't register this statement"...
        static final String[] STMT_DISABLE_TRIGGER = {"DISABLE", "TRIGGER"};
        static final String[] STMT_ENABLE_TRIGGER = {"ENABLE", "TRIGGER"};
        static final String[] STMT_TRUNCATE_TABLE = {"TRUNCATE", "TABLE"};
        static final String[] STMT_UPDATE_STATISTICS = {"UPDATE", "STATISTICS"};

        
        static final String[] STMT_SET_DATEFIRST = {SET, "DATEFIRST"};
        static final String[] STMT_SET_DATEFORMAT = {SET, "DATEFORMAT"};
        static final String[] STMT_SET_DEADLOCK_PRIORITY = {SET, "DEADLOCK_PRIORITY"};
        static final String[] STMT_SET_LOCK_TIMEOUT = {SET, "LOCK_TIMEOUT"};
        static final String[] STMT_SET_CONCAT_NULL_YIELDS_NULL = {SET, "CONCAT_NULL_YIELDS_NULL"};
        static final String[] STMT_SET_CURSOR_CLOSE_ON_COMMIT = {SET, "CURSOR_CLOSE_ON_COMMIT"};
        static final String[] STMT_SET_FIPS_FLAGGER = {SET, "FIPS_FLAGGER"};
        static final String[] STMT_SET_IDENTITY_INSERT = {SET, "IDENTITY_INSERT"};
        static final String[] STMT_SET_LANGUAGE = {SET, "LANGUAGE"};
        static final String[] STMT_SET_OFFSETS = {SET, "OFFSETS"};
        static final String[] STMT_SET_QUOTED_IDENTIFIER = {SET, "QUOTED_IDENTIFIER"};
        static final String[] STMT_SET_ARITHABORT = {SET, "ARITHABORT"};
        static final String[] STMT_SET_ARITHIGNORE = {SET, "ARITHIGNORE"};
        static final String[] STMT_SET_FMTONLY = {SET, "FMTONLY"};
        static final String[] STMT_SET_ANSI_DEFAULTS = {SET, "ANSI_DEFAULTS"};
        static final String[] STMT_SET_ANSI_NULL_DFLT_OFF = {SET, "ANSI_NULL_DFLT_OFF"};
        static final String[] STMT_SET_ANSI_NULL_DFLT_ON = {SET, "ANSI_NULL_DFLT_ON"};
        static final String[] STMT_SET_ANSI_NULLS = {SET, "ANSI_NULLS"};
        static final String[] STMT_SET_ANSI_PADDING = {SET, "ANSI_PADDING"};
        static final String[] STMT_SET_ANSI_WARNINGS = {SET, "ANSI_WARNINGS"};
        static final String[] STMT_SET_FORCEPLAN = {SET, "FORCEPLAN"};
        static final String[] STMT_SET_SHOWPLAN_ALL = {SET, "SHOWPLAN_ALL"};
        static final String[] STMT_SET_SHOWPLAN_TEXT = {SET, "SHOWPLAN_TEXT"};
        static final String[] STMT_SET_SHOWPLAN_XML = {SET, "SHOWPLAN_XML"};
        static final String[] STMT_SET_STATISTICS = {SET, "STATISTICS"};
        static final String[] STMT_SET_IMPLICIT_TRANSACTIONS = {SET, "IMPLICIT_TRANSACTIONS"};
        static final String[] STMT_SET_REMOTE_PROC_TRANSACTIONS = {SET, "REMOTE_PROC_TRANSACTIONS"};
        static final String[] STMT_SET_TRANSACTION_ISOLATION_LEVEL = {SET, "TRANSACTION", "ISOLATION", "LEVEL"};
        static final String[] STMT_SET_XACT_ABORT = {SET, "XACT_ABORT"};
        static final String[] STMT_SET_NOCOUNT = {SET, "NOCOUNT"};
        static final String[] STMT_SET_NOEXEC = {SET, "NOEXEC"};
        static final String[] STMT_SET_NUMERIC_ROUNDABORT = {SET, "NUMERIC_ROUNDABORT"};
        static final String[] STMT_SET_PARSEONLY = {SET, "PARSEONLY"};
        static final String[] STMT_SET_QUERY_GOVERNOR_COST_LIMIT = {SET, "QUERY_GOVERNOR_COST_LIMIT"};
        static final String[] STMT_SET_ROWCOUNT = {SET, "ROWCOUNT"};
        static final String[] STMT_SET_TEXTSIZE = {SET, "TEXTSIZE"};

        
        static final String[][] SET_PHRASES = {
            STMT_SET_DATEFIRST, STMT_SET_DATEFORMAT, STMT_SET_DEADLOCK_PRIORITY, STMT_SET_LOCK_TIMEOUT,
            STMT_SET_CONCAT_NULL_YIELDS_NULL, STMT_SET_CURSOR_CLOSE_ON_COMMIT, STMT_SET_FIPS_FLAGGER, 
            STMT_SET_IDENTITY_INSERT, STMT_SET_LANGUAGE, STMT_SET_OFFSETS, STMT_SET_QUOTED_IDENTIFIER, 
            STMT_SET_ARITHABORT, STMT_SET_ARITHIGNORE, STMT_SET_FMTONLY, STMT_SET_ANSI_DEFAULTS, 
            STMT_SET_ANSI_NULL_DFLT_OFF, STMT_SET_ANSI_NULL_DFLT_ON, STMT_SET_ANSI_NULLS, STMT_SET_ANSI_PADDING, 
            STMT_SET_ANSI_WARNINGS, STMT_SET_FORCEPLAN, STMT_SET_SHOWPLAN_ALL, STMT_SET_SHOWPLAN_TEXT, 
            STMT_SET_SHOWPLAN_XML, STMT_SET_STATISTICS, STMT_SET_IMPLICIT_TRANSACTIONS, STMT_SET_REMOTE_PROC_TRANSACTIONS,
            STMT_SET_TRANSACTION_ISOLATION_LEVEL, STMT_SET_XACT_ABORT, STMT_SET_NOCOUNT, STMT_SET_NOEXEC, 
            STMT_SET_NUMERIC_ROUNDABORT, STMT_SET_PARSEONLY, STMT_SET_QUERY_GOVERNOR_COST_LIMIT, 
            STMT_SET_ROWCOUNT, STMT_SET_TEXTSIZE
        };
        
        static final String[][] MISC_PHRASES = {
            STMT_BEGIN_DISTRIBUTED_TRANSACTION, STMT_BEGIN_DISTRIBUTED_TRAN, STMT_ROLLBACK_TRANSACTION, 
            STMT_ROLLBACK_TRAN, STMT_BEGIN_TRANSACTION, STMT_BEGIN_TRAN, STMT_ROLLBACK_WORK, STMT_ROLLBACK, 
            STMT_COMMIT_TRANSACTION, STMT_COMMIT_TRAN, STMT_SAVE_TRANSACTION, STMT_SAVE_TRAN, STMT_COMMIT_WORK, 
            STMT_COMMIT, STMT_DISABLE_TRIGGER, STMT_ENABLE_TRIGGER, STMT_TRUNCATE_TABLE, STMT_UPDATE_STATISTICS
        };
        
        // CREATE TABLE, CREATE VIEW, and GRANT statements.
        public final static String[] VALID_SCHEMA_CHILD_STMTS = {
            StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT, 
            StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT,
            StandardDdlLexicon.TYPE_GRANT_STATEMENT
          };
        
        public final static String[] COMPLEX_STMT_TYPES = {
//            SqlServerDdlLexicon.TYPE_CREATE_FUNCTION_STATEMENT // FIXME MK slashed
        };
    }
    
    interface SqlServerDataTypes {
        // EXACT NUMBERS
        static final String[] DTYPE_BIGINT = {"BIGINT"};
        static final String[] DTYPE_MONEY = {"MONEY"};
        static final String[] DTYPE_SMALLMONEY = {"SMALLMONEY"};
        static final String[] DTYPE_TINYINT = {"TINYINT"};
        // DATE & TIME
        static final String[] DTYPE_DATETIMEOFFSET = {"DATETIMEOFFSET"};
        static final String[] DTYPE_DATETIME2 = {"DATETIME2"};
        static final String[] DTYPE_DATETIME = {"DATETIME"};
        static final String[] DTYPE_SMALLDATETIME = {"SMALLDATETIME"};
        // CHARACTER & TEXT
        static final String[] DTYPE_NTEXT = {"NTEXT"};
        static final String[] DTYPE_TEXT = {"TEXT"};
        static final String[] DTYPE_NVARCHAR = {"NVARCHAR"}; //NVARCHAR(max)
        static final String[] DTYPE_VARCHAR_SQLSERVER = {"VARCHAR"}; //VARCHAR(max)
        
        // BINARY
        static final String[] DTYPE_VARBINARY = {"VARBINARY"};
        static final String[] DTYPE_BINARY_VARYING = {"BINARY", "VARYING"}; //BINARY VARYING(max)
        static final String[] DTYPE_BINARY = {"BINARY"};
        static final String[] DTYPE_IMAGE = {"IMAGE"};
        // OTHER
        static final String[] DTYPE_HIERARCHYID = {"HIERARCHYID"};
        static final String[] DTYPE_SQL_VARIANT = {"SQL_VARIANT"};
        static final String[] DTYPE_TIMESTAMP_SQLSERVER = {"TIMESTAMP"}; // == ROWVERSION
        static final String[] DTYPE_ROWVERSION = {"ROWVERSION"};
        static final String[] DTYPE_UNIQUEIDENTIFIER = {"UNIQUEIDENTIFIER"};
        static final String[] DTYPE_XML = {"XML"};
        static final String[] DTYPE_GEOGRAPHY = {"GEOGRAPHY"};
        static final String[] DTYPE_GEOMETRY = {"GEOMETRY"};
        
    
        static final List<String[]> CUSTOM_DATATYPE_START_PHRASES = Arrays.asList(
                DTYPE_BIGINT, DTYPE_MONEY, DTYPE_SMALLMONEY, DTYPE_TINYINT,
                DTYPE_DATETIME2, DTYPE_DATETIME, DTYPE_DATETIMEOFFSET, DTYPE_SMALLDATETIME,
                DTYPE_NTEXT, DTYPE_TEXT, DTYPE_NVARCHAR, DTYPE_VARCHAR_SQLSERVER,
                DTYPE_VARBINARY, DTYPE_BINARY_VARYING, DTYPE_BINARY, DTYPE_IMAGE,
                DTYPE_HIERARCHYID, DTYPE_SQL_VARIANT, DTYPE_TIMESTAMP_SQLSERVER, DTYPE_ROWVERSION,
                DTYPE_UNIQUEIDENTIFIER, DTYPE_XML, DTYPE_GEOGRAPHY, DTYPE_GEOMETRY);
        
        static final List<String> CUSTOM_DATATYPE_START_WORDS = 
                Arrays.asList("BIGINT", "MONEY", "SMALLMONEY", "TINYINT", "DATETIMEOFFSET", "DATETIME2", "DATETIME", 
                        "SMALLDATETIME", "NTEXT", "TEXT", "NVARCHAR", "VARCHAR", "VARBINARY", "BINARY", "IMAGE",
                        "HIERARCHYID", "SQL_VARIANT", "TIMESTAMP", "ROWVERSION", "UNIQUEIDENTIFIER", "XML",
                        "GEOGRAPHY", "GEOMETRY");
    }

    interface IndexTypes {
        String TABLE = "TABLE";
    }
}
