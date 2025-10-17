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

import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

/**
 * Resource holder wrapping an OrientDB {@link ODatabaseSession}.
 * OrientDBTransactionManager binds instances of this class to the thread,
 * for a specific {@link com.orientechnologies.orient.core.db.ODatabasePool}.
 *
 * <p>Note: This is an SPI class, not intended to be used by applications.
 *
 * @author Spring Data OrientDB Team
 * @since 1.4.0
 */
public class SessionHolder extends ResourceHolderSupport {

    private final ODatabaseSession session;
    private boolean transactionActive = false;

    /**
     * Create a new SessionHolder for the given OrientDB session.
     *
     * @param session the OrientDB session
     */
    public SessionHolder(ODatabaseSession session) {
        Assert.notNull(session, "Session must not be null");
        this.session = session;
    }

    /**
     * Return the OrientDB session held by this holder.
     *
     * @return the session
     */
    public ODatabaseSession getSession() {
        return this.session;
    }

    /**
     * Return whether this holder currently has a session.
     *
     * @return true if a session is present
     */
    public boolean hasSession() {
        return (this.session != null);
    }

    /**
     * Set whether this holder represents an active, OrientDB-managed transaction.
     *
     * @param transactionActive true if transaction is active
     */
    public void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
    }

    /**
     * Return whether this holder represents an active, OrientDB-managed transaction.
     *
     * @return true if transaction is active
     */
    public boolean isTransactionActive() {
        return this.transactionActive;
    }

    @Override
    public void clear() {
        super.clear();
        this.transactionActive = false;
    }
}

