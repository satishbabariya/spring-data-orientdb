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
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orientdb.core.OrientDBMappingContext;
import org.springframework.data.orientdb.core.OrientDBTemplate;
import org.springframework.data.orientdb.core.schema.SchemaGenerator;
import org.springframework.data.orientdb.repository.config.EnableOrientDBRepositories;

/**
 * Base class for Spring Data OrientDB configuration using JavaConfig.
 * Subclasses must implement {@link #orientDB()} to provide the OrientDB connection.
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Configuration
 * &#64;EnableOrientDBRepositories(basePackages = "com.example.repository")
 * public class OrientDBConfig extends AbstractOrientDBConfiguration {
 *     
 *     &#64;Override
 *     protected OrientDB orientDB() {
 *         return new OrientDB("embedded:./databases", OrientDBConfig.defaultConfig());
 *     }
 *     
 *     &#64;Override
 *     protected String getDatabaseName() {
 *         return "mydb";
 *     }
 *     
 *     &#64;Override
 *     protected String getUsername() {
 *         return "admin";
 *     }
 *     
 *     &#64;Override
 *     protected String getPassword() {
 *         return "admin";
 *     }
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 * @see EnableOrientDBRepositories
 */
@Configuration
public abstract class AbstractOrientDBConfiguration implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(AbstractOrientDBConfiguration.class);

    /**
     * Returns the {@link OrientDB} instance to use for database access.
     * Must be implemented by subclasses.
     *
     * @return the OrientDB instance
     */
    protected abstract OrientDB orientDB();

    /**
     * Returns the name of the database to connect to.
     *
     * @return the database name
     */
    protected abstract String getDatabaseName();

    /**
     * Returns the username for database authentication.
     *
     * @return the username
     */
    protected abstract String getUsername();

    /**
     * Returns the password for database authentication.
     *
     * @return the password
     */
    protected abstract String getPassword();

    /**
     * Returns the minimum number of connections in the pool.
     * Override to customize.
     *
     * @return the minimum pool size (default: 1)
     */
    protected int getPoolMin() {
        return 1;
    }

    /**
     * Returns the maximum number of connections in the pool.
     * Override to customize.
     *
     * @return the maximum pool size (default: 20)
     */
    protected int getPoolMax() {
        return 20;
    }

    /**
     * Returns whether to automatically create the database if it doesn't exist.
     * Only works in embedded mode.
     *
     * @return true to create database if missing (default: true)
     */
    protected boolean isAutoCreateDatabase() {
        return true;
    }

    /**
     * Returns the database type to create if auto-creation is enabled.
     *
     * @return the database type (default: PLOCAL)
     */
    protected ODatabaseType getDatabaseType() {
        return ODatabaseType.PLOCAL;
    }

    /**
     * Creates the {@link ODatabasePool} bean for connection pooling.
     *
     * @return the database pool
     */
    @Bean
    public ODatabasePool databasePool() {
        OrientDB orientDB = orientDB();
        String databaseName = getDatabaseName();

        // Create database if it doesn't exist and auto-create is enabled
        if (isAutoCreateDatabase() && !orientDB.exists(databaseName)) {
            orientDB.create(databaseName, getDatabaseType());
        }

        return new ODatabasePool(orientDB, databaseName, getUsername(), getPassword());
    }

    /**
     * Creates the {@link OrientDBMappingContext} bean.
     *
     * @return the mapping context
     */
    @Bean
    public OrientDBMappingContext orientDBMappingContext() {
        return new OrientDBMappingContext();
    }

    /**
     * Creates the {@link OrientDBTemplate} bean.
     *
     * @return the OrientDB template
     */
    @Bean
    public OrientDBTemplate orientDBTemplate() {
        return new OrientDBTemplate(databasePool(), orientDBMappingContext());
    }

    /**
     * Returns whether to automatically generate schema from entities.
     * Override to customize.
     *
     * @return true to auto-generate schema (default: true)
     */
    protected boolean isAutoGenerateSchema() {
        return true;
    }

    /**
     * Initialize schema after beans are created.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // Schema generation disabled here to avoid circular dependency
        // Applications can implement ApplicationRunner for schema initialization
    }

}

