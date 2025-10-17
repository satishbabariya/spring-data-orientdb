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

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.util.StringUtils;

/**
 * OrientDB-specific extension of {@link QueryMethod}.
 * Provides metadata about query methods and handles @Query annotations.
 *
 * @author Spring Data OrientDB Team
 * @since 1.1.0
 */
public class OrientDBQueryMethod extends QueryMethod {

    private final Method method;

    public OrientDBQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);
        this.method = method;
    }

    /**
     * Returns whether the method has an annotated query.
     */
    public boolean hasAnnotatedQuery() {
        return getAnnotatedQuery() != null;
    }

    /**
     * Returns the query string from the @Query annotation if present.
     */
    public String getAnnotatedQuery() {
        Query queryAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Query.class);
        if (queryAnnotation != null) {
            String query = queryAnnotation.value();
            return StringUtils.hasText(query) ? query : null;
        }
        return null;
    }

    /**
     * Returns whether this is a modifying query (delete).
     */
    public boolean isModifyingQuery() {
        Query queryAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Query.class);
        return queryAnnotation != null && queryAnnotation.delete();
    }

    /**
     * Returns whether this is a count query.
     */
    public boolean isCountQuery() {
        Query queryAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Query.class);
        return queryAnnotation != null && queryAnnotation.count();
    }

}

