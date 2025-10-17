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
package org.springframework.data.orientdb.repository.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.orientdb.core.mapping.OrientDBPersistentProperty;
import org.springframework.data.orientdb.repository.OrientDBRepository;
import org.springframework.data.orientdb.repository.support.OrientDBRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * OrientDB-specific implementation of {@link org.springframework.data.repository.config.RepositoryConfigurationExtension}.
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

    private static final String ORIENTDB_TEMPLATE_REF = "orientDBTemplateRef";

    @Override
    public String getModuleName() {
        return "OrientDB";
    }

    @Override
    protected String getModulePrefix() {
        return "orientdb";
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return OrientDBRepositoryFactoryBean.class.getName();
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.singleton(OrientDBRepository.class);
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource source) {
        source.getAttribute(ORIENTDB_TEMPLATE_REF)
            .ifPresent(ref -> builder.addPropertyReference("orientDBOperations", ref.toString()));
    }

}

