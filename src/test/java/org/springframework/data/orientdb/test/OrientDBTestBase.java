package org.springframework.data.orientdb.test;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Base class for OrientDB integration tests.
 * Provides database cleanup between tests.
 */
@SpringJUnitConfig(OrientDBTestConfiguration.class)
public abstract class OrientDBTestBase {

    @Autowired
    protected ODatabasePool databasePool;

    /**
     * Clean database before each test.
     */
    @BeforeEach
    void cleanDatabase() {
        try (ODatabaseSession session = databasePool.acquire()) {
            // Delete all vertices
            session.command("DELETE VERTEX V");
            session.commit();
        }
    }

    /**
     * Additional cleanup after each test if needed.
     */
    @AfterEach
    void afterTest() {
        // Hook for subclasses
    }

    /**
     * Execute a command in the database.
     */
    protected void executeCommand(String sql, Object... params) {
        try (ODatabaseSession session = databasePool.acquire()) {
            session.command(sql, params);
            session.commit();
        }
    }

    /**
     * Count vertices of a specific class.
     */
    protected long countVertices(String className) {
        try (ODatabaseSession session = databasePool.acquire()) {
            var result = session.query("SELECT count(*) as count FROM " + className);
            if (result.hasNext()) {
                return result.next().getProperty("count");
            }
            return 0L;
        }
    }
}

