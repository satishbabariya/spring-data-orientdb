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
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

/**
 * {@link RepositoryQuery} implementation that executes queries from @Query annotations
 * or named queries.
 *
 * @author Spring Data OrientDB Team
 * @since 1.1.0
 */
public class StringBasedOrientDBQuery implements RepositoryQuery {

    private final OrientDBQueryMethod queryMethod;
    private final OrientDBOperations operations;
    private final String query;

    public StringBasedOrientDBQuery(OrientDBQueryMethod queryMethod, OrientDBOperations operations) {
        this(queryMethod.getAnnotatedQuery(), queryMethod, operations);
    }

    public StringBasedOrientDBQuery(String query, OrientDBQueryMethod queryMethod, OrientDBOperations operations) {
        Assert.hasText(query, "Query must not be empty!");
        Assert.notNull(queryMethod, "QueryMethod must not be null!");
        Assert.notNull(operations, "OrientDBOperations must not be null!");

        this.query = query;
        this.queryMethod = queryMethod;
        this.operations = operations;
    }

    @Override
    public Object execute(Object[] parameters) {
        Class<?> returnType = queryMethod.getReturnedObjectType();

        // Handle count queries
        if (queryMethod.isCountQuery()) {
            return operations.execute(session -> {
                try (var resultSet = session.query(query, parameters)) {
                    if (resultSet.hasNext()) {
                        return resultSet.next().<Long>getProperty("count");
                    }
                }
                return 0L;
            });
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

