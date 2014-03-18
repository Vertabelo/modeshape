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
package org.infinispan.schematic;

import javax.transaction.TransactionManager;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.test.AbstractInfinispanTest;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.DummyTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractSchematicDbTest extends AbstractInfinispanTest {

    protected SchematicDb db;
    protected EmbeddedCacheManager cm;
    protected TransactionManager tm;

    @Before
    public void beforeTest() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.invocationBatching()
                            .enable()
                            .transaction()
                            .transactionManagerLookup(new DummyTransactionManagerLookup())
                            .transactionMode(TransactionMode.TRANSACTIONAL)
                            .lockingMode(LockingMode.PESSIMISTIC)
                            .locking()
                            .isolationLevel(IsolationLevel.READ_COMMITTED);
        cm = TestCacheManagerFactory.createCacheManager(configurationBuilder);
        // Now create the SchematicDb ...
        db = Schematic.get(cm, "documents");
        tm = TestingUtil.getTransactionManager(db.getCache());
    }

    @After
    public void afterTest() {
        try {
            TestingUtil.killCacheManagers(cm);
        } finally {
            cm = null;
            db = null;
            try {
                TestingUtil.killTransaction(tm);
            } finally {
                tm = null;
            }
        }
    }

    protected SchematicDb db() {
        return db;
    }

    protected TransactionManager tm() {
        return tm;
    }

}
