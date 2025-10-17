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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.orientechnologies.orient.core.record.OVertex;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.orientdb.core.OrientDBMappingContext;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentProperty;
import org.springframework.data.orientdb.core.mapping.event.EntityCallbackHandler;
import org.springframework.util.Assert;

/**
 * Converter for mapping between domain entities and OrientDB vertices.
 * Handles the conversion of properties and nested objects.
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBEntityConverter {

    private final OrientDBMappingContext mappingContext;
    private final ConversionService conversionService;

    public OrientDBEntityConverter(OrientDBMappingContext mappingContext) {
        Assert.notNull(mappingContext, "MappingContext must not be null");
        this.mappingContext = mappingContext;
        this.conversionService = DefaultConversionService.getSharedInstance();
    }

    /**
     * Read an entity from an OrientDB vertex.
     *
     * @param type the entity type
     * @param vertex the OrientDB vertex
     * @param <T> the entity type
     * @return the entity instance
     */
    public <T> T read(Class<T> type, OVertex vertex) {
        if (vertex == null) {
            return null;
        }

        OrientDBPersistentEntity<?> entity = 
            (OrientDBPersistentEntity<?>) mappingContext.getRequiredPersistentEntity(type);

        @SuppressWarnings("unchecked")
        T instance = org.springframework.beans.BeanUtils.instantiateClass(type);

        PersistentPropertyAccessor<?> accessor = entity.getPropertyAccessor(instance);
        ConvertingPropertyAccessor<?> convertingAccessor = 
            new ConvertingPropertyAccessor<>(accessor, conversionService);

        // Set the ID
        OrientDBPersistentProperty idProperty = entity.getIdProperty();
        if (idProperty != null) {
            convertingAccessor.setProperty(idProperty, vertex.getIdentity());
        }

        // Set all properties
        entity.doWithProperties((OrientDBPersistentProperty property) -> {
            if (!property.isIdProperty() && !property.isVersionProperty()) {
                String fieldName = property.getName();
                Object value = vertex.getProperty(fieldName);
                
                if (value != null) {
                    // Convert Date to LocalDateTime if needed
                    if (value instanceof Date && property.getActualType().equals(LocalDateTime.class)) {
                        value = LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault());
                    } else if (value instanceof Date && property.getActualType().equals(LocalDate.class)) {
                        value = LocalDate.from(((Date) value).toInstant().atZone(ZoneId.systemDefault()));
                    }
                    
                    convertingAccessor.setProperty(property, value);
                }
            }
        });

        // Set version if present
        OrientDBPersistentProperty versionProperty = entity.getVersionProperty();
        if (versionProperty != null) {
            Integer version = vertex.getProperty("@version");
            if (version != null) {
                convertingAccessor.setProperty(versionProperty, version);
            }
        }

        // Invoke @PostLoad callback
        EntityCallbackHandler.invokePostLoad(instance);

        return instance;
    }

    /**
     * Write an entity to an OrientDB vertex.
     *
     * @param entity the entity to write
     * @param vertex the target OrientDB vertex
     * @param <T> the entity type
     */
    public <T> void write(T entity, OVertex vertex) {
        Assert.notNull(entity, "Entity must not be null");
        Assert.notNull(vertex, "Vertex must not be null");

        OrientDBPersistentEntity<?> persistentEntity = 
            (OrientDBPersistentEntity<?>) mappingContext.getRequiredPersistentEntity(entity.getClass());

        PersistentPropertyAccessor<?> accessor = persistentEntity.getPropertyAccessor(entity);

        // Write all properties except ID and version (those are managed by OrientDB)
        persistentEntity.doWithProperties((OrientDBPersistentProperty property) -> {
            if (!property.isIdProperty() && !property.isVersionProperty() && !property.isTransient()) {
                String fieldName = property.getName();
                Object value = accessor.getProperty(property);
                
                if (value != null) {
                    // Convert LocalDateTime to Date for OrientDB storage
                    if (value instanceof LocalDateTime) {
                        value = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
                    } else if (value instanceof LocalDate) {
                        value = Date.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                }
                
                vertex.setProperty(fieldName, value);
            }
        });
    }

}

