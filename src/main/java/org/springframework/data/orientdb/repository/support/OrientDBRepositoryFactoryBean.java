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
package org.springframework.data.orientdb.repository.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.orientdb.core.OrientDBOperations;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.FactoryBean} to create
 * {@link org.springframework.data.orientdb.repository.OrientDBRepository} instances.
 *
 * @param <T> the repository type
 * @param <S> the domain type
 * @param <ID> the ID type
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
        extends RepositoryFactoryBeanSupport<T, S, ID> {

    private OrientDBOperations orientDBOperations;

    /**
     * Creates a new {@link OrientDBRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public OrientDBRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Configures the {@link OrientDBOperations} to be used.
     *
     * @param orientDBOperations the OrientDB operations
     */
    @Autowired
    public void setOrientDBOperations(OrientDBOperations orientDBOperations) {
        this.orientDBOperations = orientDBOperations;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        Assert.notNull(orientDBOperations, "OrientDBOperations must not be null!");
        return new OrientDBRepositoryFactory(orientDBOperations);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(orientDBOperations, "OrientDBOperations must not be null!");
        super.afterPropertiesSet();
    }

}

