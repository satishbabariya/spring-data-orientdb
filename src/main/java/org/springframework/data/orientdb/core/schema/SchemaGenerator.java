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
package org.springframework.data.orientdb.core.schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.orientdb.core.OrientDBMappingContext;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentProperty;

/**
 * Utility class to generate OrientDB schema from entity classes.
 * Creates vertex classes and properties based on @Vertex annotations.
 *
 * @author Spring Data OrientDB Team
 * @since 1.3.0
 */
public class SchemaGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SchemaGenerator.class);

    private final OrientDBMappingContext mappingContext;

    public SchemaGenerator(OrientDBMappingContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    /**
     * Generate schema for all entities in the mapping context.
     */
    public void generateSchema(ODatabaseSession session) {
        logger.info("Generating OrientDB schema from entities...");

        for (OrientDBPersistentEntity<?> entity : mappingContext.getPersistentEntities()) {
            createVertexClass(session, entity);
            createProperties(session, entity);
        }

        logger.info("Schema generation complete");
    }

    /**
     * Create vertex class for an entity.
     */
    private void createVertexClass(ODatabaseSession session, OrientDBPersistentEntity<?> entity) {
        String className = entity.getVertexClassName();
        
        try {
            if (!classExists(session, className)) {
                String command = "CREATE CLASS " + className + " EXTENDS V";
                session.command(command);
                logger.info("Created vertex class: {}", className);
            } else {
                logger.debug("Vertex class already exists: {}", className);
            }
        } catch (Exception e) {
            logger.warn("Error creating vertex class {}: {}", className, e.getMessage());
        }
    }

    /**
     * Create properties for an entity.
     */
    private void createProperties(ODatabaseSession session, OrientDBPersistentEntity<?> entity) {
        String className = entity.getVertexClassName();

        entity.doWithProperties((OrientDBPersistentProperty property) -> {
            if (!property.isIdProperty() && !property.isVersionProperty()) {
                createProperty(session, className, property);
            }
        });
    }

    /**
     * Create a single property.
     */
    private void createProperty(ODatabaseSession session, String className, OrientDBPersistentProperty property) {
        String propertyName = property.getName();
        String propertyType = getOrientDBType(property);

        try {
            if (!propertyExists(session, className, propertyName)) {
                String command = String.format("CREATE PROPERTY %s.%s %s",
                    className, propertyName, propertyType);
                session.command(command);
                logger.debug("Created property: {}.{} ({})", className, propertyName, propertyType);
            }
        } catch (Exception e) {
            logger.warn("Error creating property {}.{}: {}", className, propertyName, e.getMessage());
        }
    }

    /**
     * Map Java types to OrientDB types.
     */
    private String getOrientDBType(OrientDBPersistentProperty property) {
        Class<?> type = property.getActualType();

        if (type == String.class) {
            return "STRING";
        } else if (type == Integer.class || type == int.class) {
            return "INTEGER";
        } else if (type == Long.class || type == long.class) {
            return "LONG";
        } else if (type == Double.class || type == double.class) {
            return "DOUBLE";
        } else if (type == Float.class || type == float.class) {
            return "FLOAT";
        } else if (type == Boolean.class || type == boolean.class) {
            return "BOOLEAN";
        } else if (type == Date.class || type == LocalDateTime.class || type == LocalDate.class) {
            return "DATETIME";
        } else if (type == byte[].class) {
            return "BINARY";
        } else {
            // Default to STRING for complex types (will store as JSON)
            return "STRING";
        }
    }

    /**
     * Check if a class exists in the database.
     */
    private boolean classExists(ODatabaseSession session, String className) {
        try {
            return session.getClass(className) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a property exists on a class.
     */
    private boolean propertyExists(ODatabaseSession session, String className, String propertyName) {
        try {
            var oClass = session.getClass(className);
            return oClass != null && oClass.getProperty(propertyName) != null;
        } catch (Exception e) {
            return false;
        }
    }

}

