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
package org.springframework.data.orientdb.core;

import java.util.List;
import java.util.Optional;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.id.ORID;

/**
 * Interface that specifies a basic set of OrientDB operations.
 * Provides a higher-level abstraction than direct OrientDB API usage.
 *
 * <p>Implemented by {@link OrientDBTemplate}. Not often used directly, but a useful option
 * for extensibility and testability (as it can be easily mocked or stubbed).</p>
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public interface OrientDBOperations {

    /**
     * Save an entity to OrientDB.
     *
     * @param entity the entity to save
     * @param <T> the entity type
     * @return the saved entity with updated ID and version
     */
    <T> T save(T entity);

    /**
     * Save multiple entities to OrientDB.
     *
     * @param entities the entities to save
     * @param <T> the entity type
     * @return the saved entities with updated IDs and versions
     */
    <T> List<T> saveAll(Iterable<T> entities);

    /**
     * Find an entity by its ID.
     *
     * @param id the entity ID
     * @param entityClass the entity class
     * @param <T> the entity type
     * @return an Optional containing the entity, or empty if not found
     */
    <T> Optional<T> findById(Object id, Class<T> entityClass);

    /**
     * Find all entities of a given type.
     *
     * @param entityClass the entity class
     * @param <T> the entity type
     * @return list of all entities
     */
    <T> List<T> findAll(Class<T> entityClass);

    /**
     * Delete an entity by its ID.
     *
     * @param id the entity ID
     * @param entityClass the entity class
     * @param <T> the entity type
     */
    <T> void deleteById(Object id, Class<T> entityClass);

    /**
     * Delete an entity.
     *
     * @param entity the entity to delete
     * @param <T> the entity type
     */
    <T> void delete(T entity);

    /**
     * Delete all entities of a given type.
     *
     * @param entityClass the entity class
     * @param <T> the entity type
     */
    <T> void deleteAll(Class<T> entityClass);

    /**
     * Count all entities of a given type.
     *
     * @param entityClass the entity class
     * @param <T> the entity type
     * @return the count of entities
     */
    <T> long count(Class<T> entityClass);

    /**
     * Check if an entity exists by its ID.
     *
     * @param id the entity ID
     * @param entityClass the entity class
     * @param <T> the entity type
     * @return true if the entity exists, false otherwise
     */
    <T> boolean existsById(Object id, Class<T> entityClass);

    /**
     * Execute a custom SQL query and return results.
     *
     * @param query the SQL query
     * @param entityClass the entity class for result mapping
     * @param params query parameters
     * @param <T> the entity type
     * @return list of entities matching the query
     */
    <T> List<T> query(String query, Class<T> entityClass, Object... params);

    /**
     * Execute a custom SQL query and return a single result.
     *
     * @param query the SQL query
     * @param entityClass the entity class for result mapping
     * @param params query parameters
     * @param <T> the entity type
     * @return an Optional containing the entity, or empty if not found
     */
    <T> Optional<T> querySingle(String query, Class<T> entityClass, Object... params);

    /**
     * Execute a command (INSERT, UPDATE, DELETE) and return the number of affected records.
     *
     * @param command the SQL command
     * @param params command parameters
     * @return the number of affected records
     */
    int command(String command, Object... params);

    /**
     * Execute a callback with a database session.
     *
     * @param callback the callback to execute
     * @param <T> the return type
     * @return the result of the callback
     */
    <T> T execute(DatabaseCallback<T> callback);

    /**
     * Get the OrientDB mapping context.
     *
     * @return the mapping context
     */
    OrientDBMappingContext getMappingContext();

    /**
     * Callback interface for executing operations with a database session.
     *
     * @param <T> the return type
     */
    @FunctionalInterface
    interface DatabaseCallback<T> {
        T doInDatabase(ODatabaseSession session);
    }

}

