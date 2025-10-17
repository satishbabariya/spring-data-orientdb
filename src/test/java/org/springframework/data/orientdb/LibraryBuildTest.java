package org.springframework.data.orientdb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Simple test to verify the library compiles and classes are accessible.
 */
@DisplayName("Library Build Verification")
class LibraryBuildTest {

    @Test
    @DisplayName("Core classes should be accessible")
    void testCoreClassesAccessible() throws ClassNotFoundException {
        // Verify core classes can be loaded
        assertThat(Class.forName("org.springframework.data.orientdb.core.OrientDBTemplate")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.core.OrientDBMappingContext")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.core.convert.OrientDBEntityConverter")).isNotNull();
    }

    @Test
    @DisplayName("Repository classes should be accessible")
    void testRepositoryClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.repository.OrientDBRepository")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.repository.support.SimpleOrientDBRepository")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.repository.support.OrientDBRepositoryFactory")).isNotNull();
    }

    @Test
    @DisplayName("Transaction classes should be accessible")
    void testTransactionClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.transaction.OrientDBTransactionManager")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.transaction.SessionHolder")).isNotNull();
    }

    @Test
    @DisplayName("Async repository classes should be accessible")
    void testAsyncClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.repository.AsyncOrientDBRepository")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.repository.support.SimpleAsyncOrientDBRepository")).isNotNull();
    }

    @Test
    @DisplayName("Configuration classes should be accessible")
    void testConfigurationClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.config.AbstractOrientDBConfiguration")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.config.OrientDBTransactionManagementConfiguration")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.config.OrientDBCachingConfiguration")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.config.OrientDBObservabilityConfiguration")).isNotNull();
    }

    @Test
    @DisplayName("Event classes should be accessible")
    void testEventClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.core.mapping.event.AfterSaveEvent")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.core.mapping.event.BeforeDeleteEvent")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.core.mapping.event.AfterDeleteEvent")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.core.mapping.event.BeforeConvertEvent")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.core.mapping.event.AfterConvertEvent")).isNotNull();
    }

    @Test
    @DisplayName("Schema classes should be accessible")
    void testSchemaClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.core.schema.SchemaGenerator")).isNotNull();
    }

    @Test
    @DisplayName("Observability classes should be accessible")
    void testObservabilityClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.observability.OrientDBMetrics")).isNotNull();
    }
    
    @Test
    @DisplayName("Mapping classes should be accessible")
    void testMappingClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.core.mapping.OrientDBPersistentProperty")).isNotNull();
    }
    
    @Test
    @DisplayName("Query classes should be accessible")
    void testQueryClassesAccessible() throws ClassNotFoundException {
        assertThat(Class.forName("org.springframework.data.orientdb.repository.query.OrientDBQueryCreator")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.repository.query.PartTreeOrientDBQuery")).isNotNull();
        assertThat(Class.forName("org.springframework.data.orientdb.repository.query.StringBasedOrientDBQuery")).isNotNull();
    }
}

