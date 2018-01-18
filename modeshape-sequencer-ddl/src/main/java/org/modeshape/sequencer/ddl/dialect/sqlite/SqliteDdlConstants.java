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

import java.util.ArrayList;
import java.util.List;

import org.modeshape.sequencer.ddl.DdlConstants;

/**
 * Sqlite-specific constants including key words and statement start phrases.
 * 
 * @author Michał Kołodziejski
 */
public interface SqliteDdlConstants extends DdlConstants {
    
    // the list taken from: http://www.sqlite.org/lang_keywords.html
    public static final String[] CUSTOM_KEYWORDS = {
        "ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ATTACH", "AUTOINCREMENT", "BEFORE",
        "BEGIN", "BETWEEN", "BY", "CASCADE", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "COMMIT", "CONFLICT", "CONSTRAINT",
        "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT", "DEFERRABLE", "DEFERRED",
        "DELETE", "DESC", "DETACH", "DISTINCT", "DROP", "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUSIVE", "EXISTS", "EXPLAIN",
        "FAIL", "FOR", "FOREIGN", "FROM", "FULL", "GLOB", "GROUP", "HAVING", "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX", "INDEXED",
        "INITIALLY", "INNER", "INSERT", "INSTEAD", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT",
        "MATCH", "NATURAL", "NO", "NOT", "NOTNULL", "NULL", "OF", "OFFSET", "ON", "OR", "ORDER", "OUTER", "PLAN", "PRAGMA",
        "PRIMARY", "QUERY", "RAISE", "RECURSIVE", "REFERENCES", "REGEXP", "REINDEX", "RELEASE", "RENAME", "REPLACE", "RESTRICT",
        "RIGHT", "ROLLBACK", "ROW", "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY", "THEN", "TO", "TRANSACTION",
        "TRIGGER", "UNION", "UNIQUE", "UPDATE", "USING", "VACUUM", "VALUES", "VIEW", "VIRTUAL", "WHEN", "WHERE", "WITH", "WITHOUT",
    };
    
    // from: http://www.sqlite.org/lang.html
    interface SqliteStatementStartPhrases {
        static final String[] STMT_ALTER_TABLE = {ALTER, "TABLE"};

        
        static final String[][] ALTER_PHRASES = { 
            STMT_ALTER_TABLE
        };
        
        
        static final String[] STMT_CREATE_INDEX = {CREATE, "INDEX"};
        static final String[] STMT_CREATE_UNIQUE_INDEX = {CREATE, "UNIQUE", "INDEX"};
        static final String[] STMT_CREATE_TABLE = {CREATE, "TABLE"};
        static final String[] STMT_CREATE_TEMP_TABLE = {CREATE, "TEMP", "TABLE"};
        static final String[] STMT_CREATE_TEMPORARY_TABLE = {CREATE, "TEMPORARY", "TABLE"};
        static final String[] STMT_CREATE_TRIGGER = {CREATE, "TRIGGER"};
        static final String[] STMT_CREATE_TEMP_TRIGGER = {CREATE, "TEMP", "TRIGGER"};
        static final String[] STMT_CREATE_TEMPORARY_TRIGGER = {CREATE, "TEMPORARY", "TRIGGER"};
        static final String[] STMT_CREATE_VIEW = {CREATE, "VIEW"};
        static final String[] STMT_CREATE_TEMP_VIEW = {CREATE, "TEMP", "VIEW"};
        static final String[] STMT_CREATE_TEMPORARY_VIEW = {CREATE, "TEMPORARY", "VIEW"};
        static final String[] STMT_CREATE_VIRTUAL_TABLE = {CREATE, "VIRTUAL", "TABLE"};

    
        public static final String[][] CREATE_PHRASES = {
            STMT_CREATE_INDEX, STMT_CREATE_UNIQUE_INDEX, STMT_CREATE_TABLE, STMT_CREATE_TEMP_TABLE,
            STMT_CREATE_TEMPORARY_TABLE, STMT_CREATE_TRIGGER, STMT_CREATE_TEMP_TRIGGER, STMT_CREATE_TEMPORARY_TRIGGER,
            STMT_CREATE_VIEW, STMT_CREATE_TEMP_VIEW, STMT_CREATE_TEMPORARY_VIEW, STMT_CREATE_VIRTUAL_TABLE
        };
        
        
        static final String[] STMT_DROP_INDEX = {DROP, "INDEX"};
        static final String[] STMT_DROP_TABLE = {DROP, "TABLE"};
        static final String[] STMT_DROP_TRIGGER = {DROP, "TRIGGER"};
        static final String[] STMT_DROP_VIEW = {DROP, "VIEW"};

        
        static final String[][] DROP_PHRASES = {
            STMT_DROP_INDEX, STMT_DROP_TABLE, STMT_DROP_TRIGGER, STMT_DROP_VIEW
        };
        
        static final String[] STMT_ANALYZE = {"ANALYZE"};
        static final String[] STMT_ATTACH = {"ATTACH"};
        static final String[] STMT_ATTACH_DATABASE = {"ATTACH", "DATABASE"};
        static final String[] STMT_BEGIN = {"BEGIN"};
        static final String[] STMT_BEGIN_DEFERRED = {"BEGIN", "DEFERRED"};
        static final String[] STMT_BEGIN_IMMEDIATE = {"BEGIN", "IMMEDIATE"};
        static final String[] STMT_BEGIN_EXCLUSIVE = {"BEGIN", "EXCLUSIVE"};
        static final String[] STMT_BEGIN_TRANSACTION = {"BEGIN", "TRANSACTION"};
        static final String[] STMT_BEGIN_DEFERRED_TRANSACTION = {"BEGIN", "DEFERRED", "TRANSACTION"};
        static final String[] STMT_BEGIN_IMMEDIATE_TRANSACTION = {"BEGIN", "IMMEDIATE", "TRANSACTION"};
        static final String[] STMT_BEGIN_EXCLUSIVE_TRANSACTION = {"BEGIN", "EXCLUSIVE", "TRANSACTION"};
        static final String[] STMT_COMMIT = {"COMMIT"};
        static final String[] STMT_COMMIT_TRANSACTION = {"COMMIT", "TRANSACTION"};
        static final String[] STMT_END = {"END"};
        static final String[] STMT_END_TRANSACTION = {"END", "TRANSACTION"};
        static final String[] STMT_ROLLBACK_TRANSACTION = {"ROLLBACK", "TRANSACTION"};
        static final String[] STMT_ROLLBACK = {"ROLLBACK"};
        static final String[] STMT_DELETE_FROM = {"DELETE", "FROM"};
        static final String[] STMT_DETACH = {"DETACH"};
        static final String[] STMT_DETACH_DATABASE = {"DETACH", "DATABASE"};
        static final String[] STMT_EXPLAIN = {"EXPLAIN"};
        static final String[] STMT_EXPLAIN_QUERY_PLAN = {"EXPLAIN", "QUERY", "PLAN"};
        static final String[] STMT_PRAGMA = {"PRAGMA"};
        static final String[] STMT_REINDEX = {"REINDEX"};
        static final String[] STMT_SAVEPOINT = {"SAVEPOINT"};
        static final String[] STMT_RELEASE = {"RELEASE"};
        static final String[] STMT_RELEASE_SAVEPOINT = {"RELEASE", "SAVEPOINT"};
        static final String[] STMT_VACUUM = {"VACUUM"};
        
        
        static final String[][] MISC_PHRASES = {
            STMT_ANALYZE, STMT_ATTACH, STMT_ATTACH_DATABASE, STMT_BEGIN, STMT_BEGIN_DEFERRED, STMT_BEGIN_IMMEDIATE, 
            STMT_BEGIN_EXCLUSIVE, STMT_BEGIN_TRANSACTION, STMT_BEGIN_DEFERRED_TRANSACTION, STMT_BEGIN_IMMEDIATE_TRANSACTION,
            STMT_BEGIN_EXCLUSIVE_TRANSACTION, STMT_COMMIT, STMT_COMMIT_TRANSACTION, STMT_END, STMT_END_TRANSACTION,
            STMT_ROLLBACK_TRANSACTION, STMT_ROLLBACK, STMT_DELETE_FROM, STMT_DETACH, STMT_DETACH_DATABASE, STMT_EXPLAIN,
            STMT_EXPLAIN_QUERY_PLAN, STMT_PRAGMA, STMT_REINDEX, STMT_SAVEPOINT, STMT_RELEASE, STMT_RELEASE_SAVEPOINT, 
            STMT_VACUUM
        };
    }
    
    interface SqliteDataTypes {
        
        static final List<String[]> CUSTOM_DATATYPE_START_PHRASES = new ArrayList<String[]>();
        
        static final List<String> CUSTOM_DATATYPE_START_WORDS = new ArrayList<String>();
    }
}
