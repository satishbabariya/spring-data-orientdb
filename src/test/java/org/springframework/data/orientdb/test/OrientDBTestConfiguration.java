package org.springframework.data.orientdb.test;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orientdb.config.AbstractOrientDBConfiguration;
import org.springframework.data.orientdb.repository.config.EnableOrientDBRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Base test configuration for OrientDB integration tests.
 * Provides embedded OrientDB database for testing.
 */
@Configuration
@EnableOrientDBRepositories(basePackages = "org.springframework.data.orientdb.integration")
@EnableTransactionManagement
public class OrientDBTestConfiguration extends AbstractOrientDBConfiguration {

    private static final String DATABASE_NAME = "testdb";
    
    @Bean
    public OrientDB orientDB() {
        OrientDB orientDB = new OrientDB("embedded:./target/testdb", OrientDBConfig.defaultConfig());
        orientDB.createIfNotExists(DATABASE_NAME, ODatabaseType.MEMORY);
        return orientDB;
    }

    @Bean
    @Override
    public ODatabasePool databasePool() {
        return new ODatabasePool(orientDB(), DATABASE_NAME, "admin", "admin");
    }

    @Override
    protected String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    protected String getUsername() {
        return "admin";
    }

    @Override
    protected String getPassword() {
        return "admin";
    }
}

