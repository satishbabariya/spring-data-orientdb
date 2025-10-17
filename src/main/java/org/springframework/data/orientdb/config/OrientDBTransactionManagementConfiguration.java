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
package org.springframework.data.orientdb.config;

import com.orientechnologies.orient.core.db.ODatabasePool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orientdb.transaction.OrientDBTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration class for OrientDB transaction management.
 * Automatically creates and configures {@link OrientDBTransactionManager}.
 *
 * @author Spring Data OrientDB Team
 * @since 1.4.0
 */
@Configuration
public class OrientDBTransactionManagementConfiguration {

    /**
     * Creates the OrientDB transaction manager bean.
     *
     * @param databasePool the OrientDB database pool
     * @return the configured transaction manager
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager orientDBTransactionManager(ODatabasePool databasePool) {
        return new OrientDBTransactionManager(databasePool);
    }
}

