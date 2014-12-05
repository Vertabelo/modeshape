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

import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import static org.modeshape.sequencer.ddl.dialect.sqlserver.SqlServerDdlLexicon.Namespace.PREFIX;

public class SqlServerDdlLexicon extends StandardDdlLexicon {
    public static class Namespace {
        public static final String URI = "http://www.modeshape.org/ddl/sqlserver/1.0";
        public static final String PREFIX = "sqlserverddl";
    }

    // MIXINS
    public static final String TYPE_BACKSLASH_TERMINATOR                        = PREFIX + ":backslashTerminator";
    
    public static final String TYPE_CREATE_SEQUENCE_STATEMENT 		        = PREFIX + ":createSequenceStatement";
    public static final String TYPE_CREATE_INDEX_STATEMENT                      = PREFIX + ":createIndexStatement";
    
    public static final String TYPE_RENAME_COLUMN 				= PREFIX + ":renameColumn";
    public static final String TYPE_RENAME_CONSTRAINT 				= PREFIX + ":renameConstraint";
    public static final String TYPE_FUNCTION_PARAMETER                          = PREFIX + ":functionParameter";
    public static final String TYPE_INDEX_ORDERABLE                             = PREFIX + ":indexOrderable";
    public static final String TYPE_COLUMN_DEFINITION                           = PREFIX + ":columnDefinition";
    
    public static final String TYPE_EXECUTE_STATEMENT                   = PREFIX + ":execute";
    public static final String TYPE_EXECUTE_PARAMETER                   = PREFIX + ":parameter";

    // PROPERTY NAMES
    public static final String COMMENT                  = PREFIX + ":comment";
    public static final String UNIQUE_INDEX             = PREFIX + ":unique";
    public static final String TABLE_NAME               = PREFIX + ":tableName";
    public static final String INDEX_TYPE               = PREFIX + ":indexType";
    public static final String OTHER_INDEX_REFS         = PREFIX + ":otherRefs";
    public static final String INDEX_ATTRIBUTES         = PREFIX + ":indexAttributes";
    public static final String INDEX_ORDER              = PREFIX + ":order";
    public static final String INDEX_CLUSTERED          = PREFIX + ":clustered";
    public static final String INDEX_NONCLUSTERED       = PREFIX + ":nonclustered";
    public static final String WHERE_CLAUSE             = PREFIX + ":whereClause";
    public static final String ON_CLAUSE                = PREFIX + ":onClause";
    public static final String INCLUDE_COLUMNS          = PREFIX + ":includeColumns";
    public static final String WITH_OPTIONS             = PREFIX + ":withOptions";
    public static final String FILESTREAM_ON_CLAUSE     = PREFIX + ":filestreamOnClause";
    public static final String TEXTIMAGE_ON_CLAUSE      = PREFIX + ":textimageOnClause";
    public static final String AS_FILETABLE             = PREFIX + ":asFileTable";
    public static final String CLUSTERED                = PREFIX + ":clustered";
    public static final String NONCLUSTERED             = PREFIX + ":nonclustered";
    public static final String NOT_FOR_REPLICATION      = PREFIX + ":notForReplication";
    
    // sequence properties
    public static final String SEQ_INCREMENT_BY   = PREFIX + ":incrementBy";
    public static final String SEQ_START_WITH     = PREFIX + ":startWith";
    public static final String SEQ_MAX_VALUE      = PREFIX + ":maxValue";
    public static final String SEQ_NO_MAX_VALUE   = PREFIX + ":noMaxValue";
    public static final String SEQ_MIN_VALUE      = PREFIX + ":minValue";
    public static final String SEQ_NO_MIN_VALUE   = PREFIX + ":noMinValue";
    public static final String SEQ_CYCLE          = PREFIX + ":cycle";
    public static final String SEQ_CACHE          = PREFIX + ":cache";
    public static final String SEQ_NO_CACHE       = PREFIX + ":noCache";
    public static final String SEQ_AS_DATA_TYPE   = PREFIX + ":asDataType";
    
    // view properties
    public static final String VIEW_WITH_ATTRIBUTES     = PREFIX + ":withAttributes";
    public static final String VIEW_WITH_CHECK_OPTION   = PREFIX + ":withCheckOption";
    
    // columns properties
    public static final String COLUMN_FILESTREAM    = PREFIX + ":filestream";
    public static final String COLUMN_SPARSE        = PREFIX + ":sparse";
    public static final String COLUMN_IDENTITY      = PREFIX + ":identity";
    public static final String COLUMN_ROWGUIDCOL    = PREFIX + ":rowguidcol";
    
    // execute properties
    public static final String BODY                 = PREFIX + ":body";
    public static final String SERVER_NAME          = PREFIX + ":serverName";
    public static final String USER                 = PREFIX + ":user";
    public static final String RETURN_STATUS        = PREFIX + ":returnStatus";
    public static final String OUTPUT               = PREFIX + ":output";
    public static final String PARAMETER            = PREFIX + ":parameter";

    
}
