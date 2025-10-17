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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.orientdb.core.convert.OrientDBEntityConverter;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.orientdb.core.mapping.event.*;
import org.springframework.data.orientdb.core.mapping.event.EntityCallbackHandler;
import org.springframework.data.orientdb.transaction.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * Primary implementation of {@link OrientDBOperations}.
 * Simplifies working with OrientDB by providing convenience methods for common operations.
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBTemplate implements OrientDBOperations, ApplicationContextAware, ApplicationEventPublisherAware {

    private static final Logger logger = LoggerFactory.getLogger(OrientDBTemplate.class);

    private final ODatabasePool databasePool;
    private final OrientDBMappingContext mappingContext;
    private final OrientDBEntityConverter entityConverter;
    private ApplicationContext applicationContext;
    private ApplicationEventPublisher eventPublisher;

    public OrientDBTemplate(ODatabasePool databasePool) {
        this(databasePool, new OrientDBMappingContext());
    }

    public OrientDBTemplate(ODatabasePool databasePool, OrientDBMappingContext mappingContext) {
        Assert.notNull(databasePool, "DatabasePool must not be null");
        Assert.notNull(mappingContext, "MappingContext must not be null");
        
        this.databasePool = databasePool;
        this.mappingContext = mappingContext;
        this.entityConverter = new OrientDBEntityConverter(mappingContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public <T> T save(T entity) {
        Assert.notNull(entity, "Entity must not be null");
        
        // Invoke @PrePersist callback
        EntityCallbackHandler.invokePrePersist(entity);
        
        // Publish before save event for auditing
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new BeforeSaveEvent(entity));
            eventPublisher.publishEvent(new BeforeConvertEvent(entity));
        }
        
        return execute(session -> {
            OrientDBPersistentEntity<?> persistentEntity = 
                (OrientDBPersistentEntity<?>) mappingContext.getRequiredPersistentEntity(entity.getClass());
            
            String vertexClassName = persistentEntity.getVertexClassName();
            
            // Get the ID if it exists
            PersistentPropertyAccessor<?> accessor = persistentEntity.getPropertyAccessor(entity);
            Object id = persistentEntity.getIdentifierAccessor(entity).getIdentifier();
            
            OVertex vertex;
            if (id != null && id instanceof ORID) {
                // Update existing vertex
                vertex = session.load((ORID) id);
                if (vertex == null) {
                    throw new IllegalArgumentException("Vertex with ID " + id + " not found");
                }
            } else if (id != null && id instanceof String) {
                // Try to load by string ID
                try {
                    ORID orid = new ORecordId((String) id);
                    vertex = session.load(orid);
                    if (vertex == null) {
                        // Create new if not found
                        vertex = session.newVertex(vertexClassName);
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid ORID format, create new
                    vertex = session.newVertex(vertexClassName);
                }
            } else {
                // Create new vertex
                vertex = session.newVertex(vertexClassName);
            }
            
            // Convert entity to vertex
            entityConverter.write(entity, vertex);
            
            // Save vertex
            vertex.save();
            
            // Only commit if NOT in a managed transaction
            if (!isTransactionActive(session)) {
                session.commit();
            }
            
            // Convert back to entity with updated ID
            T result = (T) entityConverter.read(entity.getClass(), vertex);
            
            // Publish after save and after convert events
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new AfterConvertEvent(result));
                eventPublisher.publishEvent(new AfterSaveEvent(result));
            }
            
            return result;
        });
    }

    @Override
    public <T> List<T> saveAll(Iterable<T> entities) {
        Assert.notNull(entities, "Entities must not be null");
        
        List<T> result = new ArrayList<>();
        for (T entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public <T> Optional<T> findById(Object id, Class<T> entityClass) {
        Assert.notNull(id, "ID must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");
        
        return execute(session -> {
            try {
                ORID orid = convertToORID(id);
                OVertex vertex = session.load(orid);
                
                if (vertex == null) {
                    return Optional.empty();
                }
                
                T entity = entityConverter.read(entityClass, vertex);
                return Optional.ofNullable(entity);
            } catch (Exception e) {
                logger.debug("Error finding entity by ID: {}", id, e);
                return Optional.empty();
            }
        });
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        Assert.notNull(entityClass, "Entity class must not be null");
        
        OrientDBPersistentEntity<?> persistentEntity = 
            (OrientDBPersistentEntity<?>) mappingContext.getRequiredPersistentEntity(entityClass);
        String vertexClassName = persistentEntity.getVertexClassName();
        
        String query = "SELECT FROM " + vertexClassName;
        return query(query, entityClass);
    }

    @Override
    public <T> void deleteById(Object id, Class<T> entityClass) {
        Assert.notNull(id, "ID must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");
        
        execute(session -> {
            ORID orid = convertToORID(id);
            session.delete(orid);
            // Only commit if NOT in a managed transaction
            if (!isTransactionActive(session)) {
                session.commit();
            }
            return null;
        });
    }

    @Override
    public <T> void delete(T entity) {
        Assert.notNull(entity, "Entity must not be null");
        
        // Publish before delete event
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new BeforeDeleteEvent(entity));
        }
        
        // Invoke @PreRemove callback
        EntityCallbackHandler.invokePreRemove(entity);
        
        execute(session -> {
            OrientDBPersistentEntity<?> persistentEntity = 
                (OrientDBPersistentEntity<?>) mappingContext.getRequiredPersistentEntity(entity.getClass());
            
            Object id = persistentEntity.getIdentifierAccessor(entity).getIdentifier();
            if (id == null) {
                throw new IllegalArgumentException("Cannot delete entity without ID");
            }
            
            ORID orid = convertToORID(id);
            session.delete(orid);
            // Only commit if NOT in a managed transaction
            if (!isTransactionActive(session)) {
                session.commit();
            }
            return null;
        });
    }

    @Override
    public <T> void deleteAll(Class<T> entityClass) {
        Assert.notNull(entityClass, "Entity class must not be null");
        
        OrientDBPersistentEntity<?> persistentEntity = 
            (OrientDBPersistentEntity<?>) mappingContext.getRequiredPersistentEntity(entityClass);
        String vertexClassName = persistentEntity.getVertexClassName();
        
        command("DELETE VERTEX " + vertexClassName);
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        Assert.notNull(entityClass, "Entity class must not be null");
        
        OrientDBPersistentEntity<?> persistentEntity = 
            (OrientDBPersistentEntity<?>) mappingContext.getRequiredPersistentEntity(entityClass);
        String vertexClassName = persistentEntity.getVertexClassName();
        
        String query = "SELECT COUNT(*) as count FROM " + vertexClassName;
        return execute(session -> {
            try (OResultSet resultSet = session.query(query)) {
                if (resultSet.hasNext()) {
                    OResult result = resultSet.next();
                    return result.getProperty("count");
                }
            }
            return 0L;
        });
    }

    @Override
    public <T> boolean existsById(Object id, Class<T> entityClass) {
        return findById(id, entityClass).isPresent();
    }

    @Override
    public <T> List<T> query(String query, Class<T> entityClass, Object... params) {
        Assert.notNull(query, "Query must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");
        
        return execute(session -> {
            List<T> results = new ArrayList<>();
            try (OResultSet resultSet = session.query(query, params)) {
                while (resultSet.hasNext()) {
                    OResult result = resultSet.next();
                    OVertex vertex = result.getVertex().orElse(null);
                    if (vertex != null) {
                        T entity = entityConverter.read(entityClass, vertex);
                        if (entity != null) {
                            results.add(entity);
                        }
                    }
                }
            }
            return results;
        });
    }

    @Override
    public <T> Optional<T> querySingle(String query, Class<T> entityClass, Object... params) {
        Assert.notNull(query, "Query must not be null");
        Assert.notNull(entityClass, "Entity class must not be null");
        
        return execute(session -> {
            try (OResultSet resultSet = session.query(query, params)) {
                if (resultSet.hasNext()) {
                    OResult result = resultSet.next();
                    OVertex vertex = result.getVertex().orElse(null);
                    if (vertex != null) {
                        T entity = entityConverter.read(entityClass, vertex);
                        return Optional.ofNullable(entity);
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public int command(String command, Object... params) {
        Assert.notNull(command, "Command must not be null");
        
        return execute(session -> {
            try (OResultSet resultSet = session.command(command, params)) {
                int count = 0;
                while (resultSet.hasNext()) {
                    resultSet.next();
                    count++;
                }
                // Only commit if NOT in a managed transaction
                if (!isTransactionActive(session)) {
                    session.commit();
                }
                return count;
            }
        });
    }

    @Override
    public <T> T execute(DatabaseCallback<T> callback) {
        Assert.notNull(callback, "Callback must not be null");
        
        // Check if we're in a Spring-managed transaction
        SessionHolder sessionHolder = (SessionHolder) 
            TransactionSynchronizationManager.getResource(databasePool);
        
        if (sessionHolder != null && sessionHolder.hasSession()) {
            // Use the transaction-bound session
            if (logger.isDebugEnabled()) {
                logger.debug("Using transaction-bound OrientDB session");
            }
            try {
                return callback.doInDatabase(sessionHolder.getSession());
            } catch (Exception e) {
                logger.error("Error executing database operation", e);
                throw new RuntimeException("Error executing database operation", e);
            }
        } else {
            // No transaction - acquire a new session and close it after use
            try (ODatabaseSession session = databasePool.acquire()) {
                return callback.doInDatabase(session);
            } catch (Exception e) {
                logger.error("Error executing database operation", e);
                throw new RuntimeException("Error executing database operation", e);
            }
        }
    }

    @Override
    public OrientDBMappingContext getMappingContext() {
        return mappingContext;
    }

    /**
     * Check if the session is part of an active Spring-managed transaction.
     */
    private boolean isTransactionActive(ODatabaseSession session) {
        SessionHolder holder = (SessionHolder) 
            TransactionSynchronizationManager.getResource(databasePool);
        return holder != null && holder.isTransactionActive();
    }

    /**
     * Convert an ID object to an ORID.
     */
    private ORID convertToORID(Object id) {
        if (id instanceof ORID) {
            return (ORID) id;
        } else if (id instanceof String) {
            return new ORecordId((String) id);
        } else {
            throw new IllegalArgumentException("ID must be an ORID or String, got: " + id.getClass().getName());
        }
    }

}

