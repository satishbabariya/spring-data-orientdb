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

import java.lang.reflect.Method;

import org.springframework.data.orientdb.core.OrientDBOperations;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * Query lookup strategy for OrientDB repositories.
 * Handles different query creation strategies:
 * 1. @Query annotated methods
 * 2. Named queries from properties files
 * 3. Derived queries from method names
 *
 * @author Spring Data OrientDB Team
 * @since 1.1.0
 */
public class OrientDBQueryLookupStrategy {

    /**
     * Creates a {@link QueryLookupStrategy} for the given {@link Key}.
     */
    public static QueryLookupStrategy create(
            OrientDBOperations operations,
            QueryMethodEvaluationContextProvider evaluationContextProvider,
            Key key) {

        switch (key != null ? key : Key.CREATE_IF_NOT_FOUND) {
            case CREATE:
                return new CreateQueryLookupStrategy(operations);
            case USE_DECLARED_QUERY:
                return new DeclaredQueryLookupStrategy(operations);
            case CREATE_IF_NOT_FOUND:
            default:
                return new CreateIfNotFoundQueryLookupStrategy(operations);
        }
    }

    /**
     * Strategy to create derived queries from method names.
     */
    private static class CreateQueryLookupStrategy implements QueryLookupStrategy {

        private final OrientDBOperations operations;

        public CreateQueryLookupStrategy(OrientDBOperations operations) {
            this.operations = operations;
        }

        @Override
        public RepositoryQuery resolveQuery(
                Method method,
                RepositoryMetadata metadata,
                ProjectionFactory factory,
                NamedQueries namedQueries) {

            OrientDBQueryMethod queryMethod = new OrientDBQueryMethod(method, metadata, factory);
            return new PartTreeOrientDBQuery(queryMethod, operations);
        }
    }

    /**
     * Strategy to use only declared queries (@Query annotation or named queries).
     */
    private static class DeclaredQueryLookupStrategy implements QueryLookupStrategy {

        private final OrientDBOperations operations;

        public DeclaredQueryLookupStrategy(OrientDBOperations operations) {
            this.operations = operations;
        }

        @Override
        public RepositoryQuery resolveQuery(
                Method method,
                RepositoryMetadata metadata,
                ProjectionFactory factory,
                NamedQueries namedQueries) {

            OrientDBQueryMethod queryMethod = new OrientDBQueryMethod(method, metadata, factory);

            // Try @Query annotation first
            if (queryMethod.hasAnnotatedQuery()) {
                return new StringBasedOrientDBQuery(queryMethod, operations);
            }

            // Try named query
            String namedQueryName = queryMethod.getNamedQueryName();
            if (namedQueries.hasQuery(namedQueryName)) {
                String queryString = namedQueries.getQuery(namedQueryName);
                return new StringBasedOrientDBQuery(queryString, queryMethod, operations);
            }

            throw new IllegalStateException(
                String.format("Did not find query for method %s", method.getName()));
        }
    }

    /**
     * Strategy to try declared queries first, fall back to derived queries.
     */
    private static class CreateIfNotFoundQueryLookupStrategy implements QueryLookupStrategy {

        private final DeclaredQueryLookupStrategy declaredQueryLookupStrategy;
        private final CreateQueryLookupStrategy createQueryLookupStrategy;

        public CreateIfNotFoundQueryLookupStrategy(OrientDBOperations operations) {
            this.declaredQueryLookupStrategy = new DeclaredQueryLookupStrategy(operations);
            this.createQueryLookupStrategy = new CreateQueryLookupStrategy(operations);
        }

        @Override
        public RepositoryQuery resolveQuery(
                Method method,
                RepositoryMetadata metadata,
                ProjectionFactory factory,
                NamedQueries namedQueries) {

            try {
                // Try declared query first
                return declaredQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
            } catch (IllegalStateException e) {
                // Fall back to derived query
                return createQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
            }
        }
    }

}

