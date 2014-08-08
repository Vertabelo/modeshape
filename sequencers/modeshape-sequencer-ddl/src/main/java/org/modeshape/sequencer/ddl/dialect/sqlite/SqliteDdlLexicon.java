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

import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import static org.modeshape.sequencer.ddl.dialect.sqlite.SqliteDdlLexicon.Namespace.PREFIX;

public class SqliteDdlLexicon extends StandardDdlLexicon {
    public static class Namespace {
        public static final String URI = "http://www.modeshape.org/ddl/sqlite/1.0";
        public static final String PREFIX = "sqliteddl";
    }

    // MIXINS
    public static final String TYPE_CREATE_INDEX_STATEMENT                      = PREFIX + ":createIndexStatement";
    public static final String TYPE_RENAME_COLUMN                               = PREFIX + ":renameColumn";
    public static final String TYPE_INDEX_ORDERABLE                             = PREFIX + ":indexOrderable";

    // PROPERTY NAMES
    public static final String COMMENT                  = PREFIX + ":comment";
    public static final String UNIQUE_INDEX             = PREFIX + ":unique";
    public static final String TABLE_NAME               = PREFIX + ":tableName";
    public static final String INDEX_ATTRIBUTES         = PREFIX + ":indexAttributes";
    public static final String INDEX_ORDER              = PREFIX + ":order";
    public static final String WHERE_CLAUSE             = PREFIX + ":whereClause";
    public static final String ON_CLAUSE                = PREFIX + ":onClause";
    public static final String ON_CONFLICT_CLAUSE       = PREFIX + ":onConflictClause";
    public static final String IF_NOT_EXISTS_CLAUSE     = PREFIX + ":ifNotExistsClause";
    public static final String AUTOINCREMENT            = PREFIX + ":autoincrement";
    public static final String WITHOUT_ROWID_CLAUSE     = PREFIX + ":withoutRowidClause";
    
}
