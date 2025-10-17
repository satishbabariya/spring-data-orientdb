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

import java.util.Optional;

import org.springframework.data.orientdb.core.OrientDBOperations;
import org.springframework.data.orientdb.repository.query.OrientDBQueryLookupStrategy;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.util.Assert;

/**
 * Factory to create {@link org.springframework.data.orientdb.repository.OrientDBRepository} instances.
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBRepositoryFactory extends RepositoryFactorySupport {

    private final OrientDBOperations orientDBOperations;

    /**
     * Creates a new {@link OrientDBRepositoryFactory} with the given {@link OrientDBOperations}.
     *
     * @param orientDBOperations must not be {@literal null}.
     */
    public OrientDBRepositoryFactory(OrientDBOperations orientDBOperations) {
        Assert.notNull(orientDBOperations, "OrientDBOperations must not be null!");
        this.orientDBOperations = orientDBOperations;
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new OrientDBEntityInformation<>(domainClass, orientDBOperations.getMappingContext());
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        EntityInformation<?, Object> entityInformation = getEntityInformation(metadata.getDomainType());
        return getTargetRepositoryViaReflection(metadata, entityInformation, orientDBOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleOrientDBRepository.class;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
            QueryLookupStrategy.Key key,
            QueryMethodEvaluationContextProvider evaluationContextProvider) {
        
        return Optional.of(OrientDBQueryLookupStrategy.create(
            orientDBOperations, evaluationContextProvider, key));
    }

}

