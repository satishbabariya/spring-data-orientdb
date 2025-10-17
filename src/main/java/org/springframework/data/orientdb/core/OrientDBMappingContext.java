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
package org.springframework.data.orientdb.core;

import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentProperty;
import org.springframework.data.util.TypeInformation;

/**
 * Mapping context for OrientDB entities.
 * Manages the metadata about domain entities and their mapping to OrientDB vertices and edges.
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBMappingContext
        extends AbstractMappingContext<OrientDBPersistentEntity<?>, OrientDBPersistentProperty> {

    public OrientDBMappingContext() {
        setSimpleTypeHolder(OrientDBSimpleTypes.HOLDER);
    }

    @Override
    protected <T> OrientDBPersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
        return new OrientDBPersistentEntity<>(typeInformation);
    }

    @Override
    protected OrientDBPersistentProperty createPersistentProperty(
            Property property,
            OrientDBPersistentEntity<?> owner,
            SimpleTypeHolder simpleTypeHolder) {
        return new OrientDBPersistentProperty(property, owner, simpleTypeHolder);
    }

}

