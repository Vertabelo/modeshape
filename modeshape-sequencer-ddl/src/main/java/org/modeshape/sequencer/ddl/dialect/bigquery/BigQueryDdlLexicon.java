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
package org.modeshape.sequencer.ddl.dialect.bigquery;

import org.modeshape.sequencer.ddl.StandardDdlLexicon;

public class BigQueryDdlLexicon extends StandardDdlLexicon {

    public static class Namespace {
        public static final String PREFIX = "bigqueryddl";
    }

    // table property
    public static final String PARTITION_BY = Namespace.PREFIX + ":partitionBy";
    public static final String CLUSTER_BY = Namespace.PREFIX + ":clusterBy";

    // option list common for table and view
    public static final String IF_NOT_EXISTS_CLAUSE = Namespace.PREFIX + ":ifNotExists";
    public static final String EXPIRATION_TIMESTAMP = Namespace.PREFIX + ":expirationTimestamp";
    public static final String FRIENDLY_NAME = Namespace.PREFIX + ":friendlyName";
    public static final String DESCRIPTION = Namespace.PREFIX + ":description";
    public static final String LABELS = Namespace.PREFIX + ":labels";
    public static final String DATASET_NAME = Namespace.PREFIX + ":datasetName";
    public static final String PROJECT_NAME = Namespace.PREFIX + ":projectName";

    // table option list
    public static final String REQUIRE_PARTITION_FILTER = Namespace.PREFIX + ":requirePartitionFilter";
    public static final String PARTITION_EXPIRATION_DAYS = Namespace.PREFIX + ":partitionExpirationDays";
    public static final String KMS_KEY_NAME = Namespace.PREFIX + ":kmsKeyName";

    // view option list
    public static final String ENABLE_REFRESH = Namespace.PREFIX + ":enableRefresh";
    public static final String REFRESH_INTERVAL_MINUTES = Namespace.PREFIX + ":refreshIntervalMinutes";

    // view property
    public static final String MATERIALIZED =Namespace.PREFIX + ":materialized";
}
