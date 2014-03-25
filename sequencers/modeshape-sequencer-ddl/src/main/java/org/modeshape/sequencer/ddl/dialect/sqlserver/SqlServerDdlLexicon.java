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

    // PROPERTY NAMES
    public static final String TARGET_OBJECT_TYPE = PREFIX + ":targetObjectType";
    public static final String COMMENT            = PREFIX + ":comment";
    public static final String UNIQUE_INDEX       = PREFIX + ":unique";
    public static final String BITMAP_INDEX       = PREFIX + ":bitmap";
    public static final String TABLE_NAME         = PREFIX + ":tableName";
    public static final String IN_OUT_NO_COPY     = PREFIX + ":inOutNoCopy";
    public static final String AUTHID_VALUE       = PREFIX + ":authIdValue";
    public static final String INDEX_TYPE         = PREFIX + ":indexType";
    public static final String UNUSABLE_INDEX     = PREFIX + ":unusable";
    public static final String CLUSTER_NAME       = PREFIX + ":clustereName";
    public static final String TABLE_ALIAS        = PREFIX + ":tableAlias";
    public static final String OTHER_INDEX_REFS   = PREFIX + ":otherRefs";
    public static final String INDEX_ATTRIBUTES   = PREFIX + ":indexAttributes";
    public static final String INDEX_ORDER        = PREFIX + ":order";
    public static final String WHERE_CLAUSE       = PREFIX + ":whereClause";
    
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

}
