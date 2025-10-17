/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.orientdb.transaction;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.transaction.PlatformTransactionManager} implementation for OrientDB.
 * Binds an OrientDB {@link ODatabaseSession} from the specified {@link ODatabasePool} to the thread,
 * potentially allowing for one thread-bound session per pool.
 *
 * <p>This transaction manager is appropriate for applications that use a single OrientDB database
 * and supports Spring's declarative transaction management via {@code @Transactional}.
 *
 * <p>Application code is required to retrieve the OrientDB session via
 * {@link OrientDBTransactionManager#getSession(ODatabasePool)} instead of a standard
 * {@code pool.acquire()} call. Spring classes such as {@link org.springframework.data.orientdb.core.OrientDBTemplate}
 * use this strategy implicitly.
 *
 * @author Spring Data OrientDB Team
 * @since 1.4.0
 */
public class OrientDBTransactionManager extends AbstractPlatformTransactionManager 
        implements ResourceTransactionManager {

    private ODatabasePool databasePool;

    /**
     * Create a new OrientDBTransactionManager.
     */
    public OrientDBTransactionManager() {
    }

    /**
     * Create a new OrientDBTransactionManager.
     *
     * @param databasePool the OrientDB database pool to manage transactions for
     */
    public OrientDBTransactionManager(ODatabasePool databasePool) {
        setDatabasePool(databasePool);
        afterPropertiesSet();
    }

    /**
     * Set the OrientDB database pool that this instance should manage transactions for.
     *
     * @param databasePool the database pool
     */
    public void setDatabasePool(ODatabasePool databasePool) {
        this.databasePool = databasePool;
    }

    /**
     * Return the OrientDB database pool that this instance manages transactions for.
     *
     * @return the database pool
     */
    public ODatabasePool getDatabasePool() {
        return this.databasePool;
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.databasePool, "Property 'databasePool' is required");
    }

    @Override
    public Object getResourceFactory() {
        return getDatabasePool();
    }

    @Override
    protected Object doGetTransaction() throws TransactionException {
        OrientDBTransactionObject txObject = new OrientDBTransactionObject();
        SessionHolder sessionHolder = (SessionHolder) 
            TransactionSynchronizationManager.getResource(getDatabasePool());
        txObject.setSessionHolder(sessionHolder, false);
        return txObject;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        OrientDBTransactionObject txObject = (OrientDBTransactionObject) transaction;
        return (txObject.hasSessionHolder() && txObject.getSessionHolder().isTransactionActive());
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) 
            throws TransactionException {
        OrientDBTransactionObject txObject = (OrientDBTransactionObject) transaction;

        if (txObject.hasSessionHolder()) {
            throw new IllegalStateException(
                "Pre-bound OrientDB session found - OrientDBTransactionManager does not support " +
                "running within existing transactions. Use PROPAGATION_REQUIRES_NEW to force a new transaction.");
        }

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Acquired OrientDB session [" + databasePool + "] for OrientDB transaction");
            }

            ODatabaseSession session = databasePool.acquire();
            
            // Begin OrientDB transaction
            session.begin();

            SessionHolder sessionHolder = new SessionHolder(session);
            sessionHolder.setTransactionActive(true);
            sessionHolder.setSynchronizedWithTransaction(true);

            // Set timeout if specified
            int timeout = determineTimeout(definition);
            if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
                sessionHolder.setTimeoutInSeconds(timeout);
            }

            // Bind the session holder to the thread
            TransactionSynchronizationManager.bindResource(getDatabasePool(), sessionHolder);
            txObject.setSessionHolder(sessionHolder, true);
        }
        catch (Exception ex) {
            throw new TransactionException("Could not open OrientDB session for transaction", ex) {};
        }
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        OrientDBTransactionObject txObject = (OrientDBTransactionObject) status.getTransaction();
        ODatabaseSession session = txObject.getSessionHolder().getSession();
        
        if (status.isDebug()) {
            logger.debug("Committing OrientDB transaction on session [" + session + "]");
        }
        
        try {
            session.commit();
        }
        catch (Exception ex) {
            throw new TransactionException("Could not commit OrientDB transaction", ex) {};
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        OrientDBTransactionObject txObject = (OrientDBTransactionObject) status.getTransaction();
        ODatabaseSession session = txObject.getSessionHolder().getSession();
        
        if (status.isDebug()) {
            logger.debug("Rolling back OrientDB transaction on session [" + session + "]");
        }
        
        try {
            session.rollback();
        }
        catch (Exception ex) {
            throw new TransactionException("Could not roll back OrientDB transaction", ex) {};
        }
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        OrientDBTransactionObject txObject = (OrientDBTransactionObject) transaction;

        // Remove the session holder from the thread
        TransactionSynchronizationManager.unbindResource(getDatabasePool());

        // Close the session
        ODatabaseSession session = txObject.getSessionHolder().getSession();
        if (logger.isDebugEnabled()) {
            logger.debug("Releasing OrientDB session [" + session + "] after transaction");
        }
        session.close();
    }

    /**
     * Get the OrientDB session for the current transaction, or acquire a new one if needed.
     *
     * @param pool the database pool
     * @return the session
     */
    public static ODatabaseSession getSession(ODatabasePool pool) {
        Assert.notNull(pool, "Database pool must not be null");
        
        SessionHolder sessionHolder = (SessionHolder) 
            TransactionSynchronizationManager.getResource(pool);
        
        if (sessionHolder != null && sessionHolder.hasSession()) {
            return sessionHolder.getSession();
        }
        
        // No transactional session - return new session (caller must close it)
        return pool.acquire();
    }

    /**
     * OrientDB transaction object, representing a SessionHolder.
     */
    private static class OrientDBTransactionObject {

        private SessionHolder sessionHolder;
        private boolean newSessionHolder;

        public void setSessionHolder(SessionHolder sessionHolder, boolean newSessionHolder) {
            this.sessionHolder = sessionHolder;
            this.newSessionHolder = newSessionHolder;
        }

        public SessionHolder getSessionHolder() {
            return this.sessionHolder;
        }

        public boolean hasSessionHolder() {
            return (this.sessionHolder != null);
        }

        public boolean isNewSessionHolder() {
            return this.newSessionHolder;
        }
    }
}

