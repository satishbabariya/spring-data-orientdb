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
package org.springframework.data.orientdb.repository.query;

import org.springframework.data.orientdb.core.OrientDBOperations;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;

/**
 * {@link RepositoryQuery} implementation that derives queries from method names.
 * 
 * Examples:
 * - findByUsername(String username) → SELECT FROM User WHERE username = ?
 * - findByAgeGreaterThan(Integer age) → SELECT FROM User WHERE age > ?
 * - findByNameAndEmail(String name, String email) → SELECT FROM User WHERE name = ? AND email = ?
 *
 * @author Spring Data OrientDB Team
 * @since 1.1.0
 */
public class PartTreeOrientDBQuery implements RepositoryQuery {

    private final OrientDBQueryMethod queryMethod;
    private final OrientDBOperations operations;
    private final PartTree tree;
    private final OrientDBQueryCreator queryCreator;

    public PartTreeOrientDBQuery(OrientDBQueryMethod queryMethod, OrientDBOperations operations) {
        Assert.notNull(queryMethod, "QueryMethod must not be null!");
        Assert.notNull(operations, "OrientDBOperations must not be null!");

        this.queryMethod = queryMethod;
        this.operations = operations;
        this.tree = new PartTree(queryMethod.getName(), queryMethod.getEntityInformation().getJavaType());
        
        OrientDBPersistentEntity<?> entity = (OrientDBPersistentEntity<?>) operations.getMappingContext()
            .getRequiredPersistentEntity(queryMethod.getEntityInformation().getJavaType());
        
        this.queryCreator = new OrientDBQueryCreator(tree, entity);
    }

    @Override
    public Object execute(Object[] parameters) {
        // Build the query
        String query = queryCreator.createQuery(parameters);
        Class<?> returnType = queryMethod.getReturnedObjectType();

        // Handle count queries
        if (tree.isCountProjection()) {
            String countQuery = queryCreator.createCountQuery();
            return operations.execute(session -> {
                try (var resultSet = session.query(countQuery, parameters)) {
                    if (resultSet.hasNext()) {
                        return resultSet.next().<Long>getProperty("count");
                    }
                }
                return 0L;
            });
        }

        // Handle exists queries
        if (tree.isExistsProjection()) {
            String countQuery = queryCreator.createCountQuery();
            Long count = operations.execute(session -> {
                try (var resultSet = session.query(countQuery, parameters)) {
                    if (resultSet.hasNext()) {
                        return resultSet.next().<Long>getProperty("count");
                    }
                }
                return 0L;
            });
            return count > 0;
        }

        // Handle delete queries
        if (tree.isDelete()) {
            return operations.command(queryCreator.createDeleteQuery(), parameters);
        }

        // Handle collection return types
        if (queryMethod.isCollectionQuery()) {
            return operations.query(query, returnType, parameters);
        }

        // Handle single result
        return operations.querySingle(query, returnType, parameters).orElse(null);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return queryMethod;
    }

}

