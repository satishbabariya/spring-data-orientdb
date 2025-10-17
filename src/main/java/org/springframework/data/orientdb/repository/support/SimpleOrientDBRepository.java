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
package org.springframework.data.orientdb.repository.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.orientdb.core.OrientDBOperations;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentProperty;
import org.springframework.data.orientdb.repository.OrientDBRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.util.Assert;
import org.springframework.data.mapping.PersistentPropertyAccessor;

/**
 * Default implementation of the {@link OrientDBRepository} interface.
 *
 * @param <T> the domain type
 * @param <ID> the ID type
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class SimpleOrientDBRepository<T, ID> implements OrientDBRepository<T, ID> {

    private final EntityInformation<T, ID> entityInformation;
    private final OrientDBOperations orientDBOperations;
    
    @Override
    public <S extends T, R> R findBy(Example<S> example, 
            java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Fluent API not yet implemented");
    }

    public SimpleOrientDBRepository(
            EntityInformation<T, ID> entityInformation,
            OrientDBOperations orientDBOperations) {
        Assert.notNull(entityInformation, "EntityInformation must not be null!");
        Assert.notNull(orientDBOperations, "OrientDBOperations must not be null!");

        this.entityInformation = entityInformation;
        this.orientDBOperations = orientDBOperations;
    }

    @Override
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "Entity must not be null!");
        return orientDBOperations.save(entity);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        Assert.notNull(entities, "Entities must not be null!");
        return orientDBOperations.saveAll(entities);
    }

    @Override
    public Optional<T> findById(ID id) {
        Assert.notNull(id, "ID must not be null!");
        return orientDBOperations.findById(id, entityInformation.getJavaType());
    }

    @Override
    public boolean existsById(ID id) {
        Assert.notNull(id, "ID must not be null!");
        return orientDBOperations.existsById(id, entityInformation.getJavaType());
    }

    @Override
    public List<T> findAll() {
        return orientDBOperations.findAll(entityInformation.getJavaType());
    }

    @Override
    public List<T> findAll(Sort sort) {
        Assert.notNull(sort, "Sort must not be null!");
        
        if (sort.isUnsorted()) {
            return findAll();
        }
        
        String query = buildSortedQuery(sort);
        return orientDBOperations.query(query, entityInformation.getJavaType());
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        Assert.notNull(pageable, "Pageable must not be null!");
        
        if (pageable.isUnpaged()) {
            List<T> all = findAll();
            return new PageImpl<>(all, pageable, all.size());
        }
        
        // Get total count
        long total = count();
        
        // Build paginated query
        String query = buildPaginatedQuery(pageable);
        List<T> content = orientDBOperations.query(query, entityInformation.getJavaType());
        
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        Assert.notNull(ids, "IDs must not be null!");
        List<T> result = new ArrayList<>();
        for (ID id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public long count() {
        return orientDBOperations.count(entityInformation.getJavaType());
    }

    @Override
    public void deleteById(ID id) {
        Assert.notNull(id, "ID must not be null!");
        orientDBOperations.deleteById(id, entityInformation.getJavaType());
    }

    @Override
    public void delete(T entity) {
        Assert.notNull(entity, "Entity must not be null!");
        orientDBOperations.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        Assert.notNull(ids, "IDs must not be null!");
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Assert.notNull(entities, "Entities must not be null!");
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        orientDBOperations.deleteAll(entityInformation.getJavaType());
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        Assert.notNull(example, "Example must not be null!");
        
        ExampleQuery<S> exampleQuery = buildExampleQuery(example, null, null);
        return orientDBOperations.querySingle(exampleQuery.getQuery(), 
            example.getProbeType(), exampleQuery.getParameters());
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        Assert.notNull(example, "Example must not be null!");
        
        ExampleQuery<S> exampleQuery = buildExampleQuery(example, null, null);
        return orientDBOperations.query(exampleQuery.getQuery(), 
            example.getProbeType(), exampleQuery.getParameters());
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        Assert.notNull(example, "Example must not be null!");
        Assert.notNull(sort, "Sort must not be null!");
        
        ExampleQuery<S> exampleQuery = buildExampleQuery(example, sort, null);
        return orientDBOperations.query(exampleQuery.getQuery(), 
            example.getProbeType(), exampleQuery.getParameters());
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        Assert.notNull(example, "Example must not be null!");
        Assert.notNull(pageable, "Pageable must not be null!");
        
        if (pageable.isUnpaged()) {
            List<S> all = findAll(example);
            return new PageImpl<>(all, pageable, all.size());
        }
        
        long total = count(example);
        ExampleQuery<S> exampleQuery = buildExampleQuery(example, pageable.getSort(), pageable);
        List<S> content = orientDBOperations.query(exampleQuery.getQuery(), 
            example.getProbeType(), exampleQuery.getParameters());
        
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        Assert.notNull(example, "Example must not be null!");
        
        ExampleQuery<S> exampleQuery = buildExampleQuery(example, null, null);
        String countQuery = "SELECT count(*) as count FROM " + getVertexClassName() + 
            exampleQuery.getWhereClause();
        
        return orientDBOperations.execute(session -> {
            try (var resultSet = session.query(countQuery, exampleQuery.getParameters())) {
                if (resultSet.hasNext()) {
                    return resultSet.next().<Long>getProperty("count");
                }
            }
            return 0L;
        });
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return count(example) > 0;
    }

    @Override
    public List<T> query(String query, Object... params) {
        return orientDBOperations.query(query, entityInformation.getJavaType(), params);
    }

    @Override
    public T querySingle(String query, Object... params) {
        return orientDBOperations.querySingle(query, entityInformation.getJavaType(), params).orElse(null);
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    /**
     * Get the vertex class name for this entity.
     */
    private String getVertexClassName() {
        OrientDBPersistentEntity<?> persistentEntity = 
            (OrientDBPersistentEntity<?>) orientDBOperations.getMappingContext()
                .getRequiredPersistentEntity(entityInformation.getJavaType());
        return persistentEntity.getVertexClassName();
    }

    /**
     * Build a query with sorting.
     */
    private String buildSortedQuery(Sort sort) {
        StringBuilder query = new StringBuilder("SELECT FROM ");
        query.append(getVertexClassName());
        
        if (sort.isSorted()) {
            query.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : sort) {
                if (!first) {
                    query.append(", ");
                }
                query.append(order.getProperty());
                query.append(" ");
                query.append(order.getDirection().name());
                first = false;
            }
        }
        
        return query.toString();
    }

    /**
     * Build a query with pagination.
     */
    private String buildPaginatedQuery(Pageable pageable) {
        StringBuilder query = new StringBuilder("SELECT FROM ");
        query.append(getVertexClassName());
        
        // Add sorting if present
        if (pageable.getSort().isSorted()) {
            query.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : pageable.getSort()) {
                if (!first) {
                    query.append(", ");
                }
                query.append(order.getProperty());
                query.append(" ");
                query.append(order.getDirection().name());
                first = false;
            }
        }
        
        // Add pagination
        query.append(" SKIP ");
        query.append(pageable.getOffset());
        query.append(" LIMIT ");
        query.append(pageable.getPageSize());
        
        return query.toString();
    }

    /**
     * Build a query from an Example.
     */
    private <S extends T> ExampleQuery<S> buildExampleQuery(Example<S> example, Sort sort, Pageable pageable) {
        S probe = example.getProbe();
        ExampleMatcher matcher = example.getMatcher();
        
        OrientDBPersistentEntity<?> persistentEntity = 
            (OrientDBPersistentEntity<?>) orientDBOperations.getMappingContext()
                .getRequiredPersistentEntity(example.getProbeType());
        
        PersistentPropertyAccessor<?> accessor = persistentEntity.getPropertyAccessor(probe);
        
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        
        // Build WHERE conditions from probe
        persistentEntity.doWithProperties((OrientDBPersistentProperty property) -> {
            if (!property.isIdProperty() && !property.isVersionProperty()) {
                Object value = accessor.getProperty(property);
                if (value != null) {
                    String fieldName = property.getName();
                    
                    // Handle string matching mode
                    if (value instanceof String) {
                        ExampleMatcher.StringMatcher stringMatcher = 
                            matcher.getDefaultStringMatcher();
                        
                        switch (stringMatcher) {
                            case CONTAINING:
                                conditions.add(fieldName + " LIKE ?");
                                parameters.add("%" + value + "%");
                                break;
                            case STARTING:
                                conditions.add(fieldName + " LIKE ?");
                                parameters.add(value + "%");
                                break;
                            case ENDING:
                                conditions.add(fieldName + " LIKE ?");
                                parameters.add("%" + value);
                                break;
                            case EXACT:
                            default:
                                if (matcher.isIgnoreCaseEnabled()) {
                                    conditions.add(fieldName + ".toLowerCase() = ?");
                                    parameters.add(((String) value).toLowerCase());
                                } else {
                                    conditions.add(fieldName + " = ?");
                                    parameters.add(value);
                                }
                                break;
                        }
                    } else {
                        conditions.add(fieldName + " = ?");
                        parameters.add(value);
                    }
                }
            }
        });
        
        // Build query
        StringBuilder query = new StringBuilder("SELECT FROM ");
        query.append(getVertexClassName());
        
        String whereClause = "";
        if (!conditions.isEmpty()) {
            whereClause = " WHERE " + String.join(" AND ", conditions);
            query.append(whereClause);
        }
        
        // Add sorting
        if (sort != null && sort.isSorted()) {
            query.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order order : sort) {
                if (!first) {
                    query.append(", ");
                }
                query.append(order.getProperty());
                query.append(" ");
                query.append(order.getDirection().name());
                first = false;
            }
        }
        
        // Add pagination
        if (pageable != null && pageable.isPaged()) {
            query.append(" SKIP ");
            query.append(pageable.getOffset());
            query.append(" LIMIT ");
            query.append(pageable.getPageSize());
        }
        
        return new ExampleQuery<>(query.toString(), whereClause, parameters.toArray());
    }

    /**
     * Inner class to hold example query details.
     */
    private static class ExampleQuery<S> {
        private final String query;
        private final String whereClause;
        private final Object[] parameters;

        ExampleQuery(String query, String whereClause, Object[] parameters) {
            this.query = query;
            this.whereClause = whereClause;
            this.parameters = parameters;
        }

        public String getQuery() {
            return query;
        }

        public String getWhereClause() {
            return whereClause;
        }

        public Object[] getParameters() {
            return parameters;
        }
    }

}

