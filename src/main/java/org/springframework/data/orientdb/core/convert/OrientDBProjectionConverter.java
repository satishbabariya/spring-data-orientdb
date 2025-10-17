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
package org.springframework.data.orientdb.core.convert;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

/**
 * Converter for creating projection instances from OrientDB vertices.
 * Supports interface-based and class-based projections (DTOs).
 *
 * @author Spring Data OrientDB Team
 * @since 1.3.0
 */
public class OrientDBProjectionConverter {

    private final ProjectionFactory projectionFactory;

    public OrientDBProjectionConverter() {
        this.projectionFactory = new SpelAwareProxyProjectionFactory();
    }

    /**
     * Create a projection from a vertex.
     *
     * @param vertex the OrientDB vertex
     * @param projectionType the projection interface or class
     * @param <T> the projection type
     * @return the projection instance
     */
    public <T> T createProjection(OVertex vertex, Class<T> projectionType) {
        if (vertex == null) {
            return null;
        }

        // Convert vertex to a map
        Map<String, Object> source = new HashMap<>();
        for (String propertyName : vertex.getPropertyNames()) {
            source.put(propertyName, vertex.getProperty(propertyName));
        }

        // Create projection
        return projectionFactory.createProjection(projectionType, source);
    }

    /**
     * Create a projection from a result.
     *
     * @param result the OrientDB result
     * @param projectionType the projection interface or class
     * @param <T> the projection type
     * @return the projection instance
     */
    public <T> T createProjection(OResult result, Class<T> projectionType) {
        if (result == null) {
            return null;
        }

        // Convert result to a map
        Map<String, Object> source = new HashMap<>();
        for (String propertyName : result.getPropertyNames()) {
            source.put(propertyName, result.getProperty(propertyName));
        }

        // Create projection
        return projectionFactory.createProjection(projectionType, source);
    }

}

