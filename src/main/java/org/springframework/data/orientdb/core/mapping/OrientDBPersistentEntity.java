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
package org.springframework.data.orientdb.core.mapping;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.orientdb.core.schema.Vertex;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.StringUtils;

/**
 * OrientDB-specific implementation of {@link org.springframework.data.mapping.PersistentEntity}.
 * Represents metadata about a domain entity and its mapping to an OrientDB vertex class.
 *
 * @param <T> the entity type
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBPersistentEntity<T> extends BasicPersistentEntity<T, OrientDBPersistentProperty> {

    private final String vertexClassName;

    public OrientDBPersistentEntity(TypeInformation<T> information) {
        super(information);
        this.vertexClassName = determineVertexClassName(information);
    }

    /**
     * Determine the OrientDB vertex class name from the entity type.
     * If the class is annotated with @Vertex, use the value from the annotation.
     * Otherwise, use the simple class name.
     */
    private String determineVertexClassName(TypeInformation<T> information) {
        Class<?> type = information.getType();
        Vertex annotation = type.getAnnotation(Vertex.class);
        
        if (annotation != null) {
            String name = annotation.value();
            if (StringUtils.hasText(name)) {
                return name;
            }
            name = annotation.name();
            if (StringUtils.hasText(name)) {
                return name;
            }
        }
        
        // Default to simple class name
        return type.getSimpleName();
    }

    /**
     * Returns the OrientDB vertex class name for this entity.
     *
     * @return the vertex class name
     */
    public String getVertexClassName() {
        return vertexClassName;
    }

}

