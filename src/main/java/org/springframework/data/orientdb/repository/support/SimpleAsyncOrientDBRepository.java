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

import org.springframework.data.orientdb.core.OrientDBOperations;
import org.springframework.data.orientdb.repository.AsyncOrientDBRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of {@link AsyncOrientDBRepository}.
 * Delegates to {@link SimpleOrientDBRepository} and wraps results in {@link CompletableFuture}.
 *
 * @param <T> the domain type
 * @param <ID> the ID type
 * @author Spring Data OrientDB Team
 * @since 1.4.0
 */
public class SimpleAsyncOrientDBRepository<T, ID> extends SimpleOrientDBRepository<T, ID>
        implements AsyncOrientDBRepository<T, ID> {

    /**
     * Creates a new {@link SimpleAsyncOrientDBRepository} for the given {@link OrientDBEntityInformation}
     * and {@link OrientDBOperations}.
     *
     * @param metadata must not be {@literal null}.
     * @param orientDBOperations must not be {@literal null}.
     */
    public SimpleAsyncOrientDBRepository(OrientDBEntityInformation<T, ID> metadata,
                                        OrientDBOperations orientDBOperations) {
        super(metadata, orientDBOperations);
    }

    @Async
    @Override
    public CompletableFuture<Optional<T>> findByIdAsync(ID id) {
        return CompletableFuture.supplyAsync(() -> findById(id));
    }

    @Async
    @Override
    public CompletableFuture<List<T>> findAllAsync() {
        return CompletableFuture.supplyAsync(() -> findAll());
    }

    @Async
    @Override
    public CompletableFuture<T> saveAsync(T entity) {
        return CompletableFuture.supplyAsync(() -> save(entity));
    }

    @Async
    @Override
    public CompletableFuture<List<T>> saveAllAsync(Iterable<T> entities) {
        return CompletableFuture.supplyAsync(() -> {
            List<T> result = new ArrayList<>();
            for (T entity : entities) {
                result.add(save(entity));
            }
            return result;
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteByIdAsync(ID id) {
        return CompletableFuture.runAsync(() -> deleteById(id));
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteAsync(T entity) {
        return CompletableFuture.runAsync(() -> delete(entity));
    }

    @Async
    @Override
    public CompletableFuture<Long> countAsync() {
        return CompletableFuture.supplyAsync(() -> count());
    }

    @Async
    @Override
    public CompletableFuture<Boolean> existsByIdAsync(ID id) {
        return CompletableFuture.supplyAsync(() -> existsById(id));
    }
}

