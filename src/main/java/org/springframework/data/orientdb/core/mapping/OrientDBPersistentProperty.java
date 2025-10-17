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

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.orientdb.core.schema.Edge;
import org.springframework.util.StringUtils;

/**
 * OrientDB-specific implementation of {@link org.springframework.data.mapping.PersistentProperty}.
 * Represents metadata about a property of a domain entity.
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBPersistentProperty
        extends AnnotationBasedPersistentProperty<OrientDBPersistentProperty> {

    public OrientDBPersistentProperty(
            Property property,
            OrientDBPersistentEntity<?> owner,
            SimpleTypeHolder simpleTypeHolder) {
        super(property, owner, simpleTypeHolder);
    }

    @Override
    protected Association<OrientDBPersistentProperty> createAssociation() {
        return new Association<>(this, null);
    }

    /**
     * Returns the property name to use in OrientDB.
     * If the property is annotated with @Property, use the value from the annotation.
     * Otherwise, use the field name.
     *
     * @return the property name
     */
    public String getOrientDBPropertyName() {
        org.springframework.data.orientdb.core.schema.Property annotation = 
            findAnnotation(org.springframework.data.orientdb.core.schema.Property.class);
        
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
        
        return getName();
    }

    /**
     * Returns true if this property represents an edge (relationship).
     *
     * @return true if this is an edge property
     */
    public boolean isEdge() {
        return isAnnotationPresent(Edge.class);
    }

    /**
     * Returns the edge annotation if present.
     *
     * @return the edge annotation, or null if not present
     */
    public Edge getEdgeAnnotation() {
        return findAnnotation(Edge.class);
    }

}

