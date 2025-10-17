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
package org.springframework.data.orientdb.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * OrientDB specific extension of {@link org.springframework.data.repository.Repository}
 * with asynchronous method support. All methods return {@link CompletableFuture} for
 * non-blocking operations.
 *
 * <p>Example usage:</p>
 * <pre>
 * public interface UserRepository extends AsyncOrientDBRepository&lt;User, ORID&gt; {
 *     CompletableFuture&lt;User&gt; findByUsernameAsync(String username);
 *     CompletableFuture&lt;List&lt;User&gt;&gt; findByDepartmentAsync(String department);
 * }
 * </pre>
 *
 * <p>Note: To use async repositories, enable async processing in your configuration:
 * <pre>
 * &#64;Configuration
 * &#64;EnableAsync
 * public class AsyncConfig {
 *     &#64;Bean
 *     public Executor taskExecutor() {
 *         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *         executor.setCorePoolSize(2);
 *         executor.setMaxPoolSize(10);
 *         executor.setQueueCapacity(500);
 *         executor.setThreadNamePrefix("orientdb-async-");
 *         executor.initialize();
 *         return executor;
 *     }
 * }
 * </pre>
 *
 * @param <T> the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 * @author Spring Data OrientDB Team
 * @since 1.4.0
 */
@NoRepositoryBean
public interface AsyncOrientDBRepository<T, ID> extends OrientDBRepository<T, ID> {

    /**
     * Asynchronously retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return a CompletableFuture wrapping the entity with the given id or {@literal Optional#empty()} if none found.
     */
    @Async
    CompletableFuture<Optional<T>> findByIdAsync(ID id);

    /**
     * Asynchronously returns all instances of the type.
     *
     * @return a CompletableFuture wrapping all entities
     */
    @Async
    CompletableFuture<List<T>> findAllAsync();

    /**
     * Asynchronously saves a given entity.
     *
     * @param entity must not be {@literal null}.
     * @return a CompletableFuture wrapping the saved entity
     */
    @Async
    CompletableFuture<T> saveAsync(T entity);

    /**
     * Asynchronously saves all given entities.
     *
     * @param entities must not be {@literal null}.
     * @return a CompletableFuture wrapping the saved entities
     */
    @Async
    CompletableFuture<List<T>> saveAllAsync(Iterable<T> entities);

    /**
     * Asynchronously deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @return a CompletableFuture that completes when the delete operation is done
     */
    @Async
    CompletableFuture<Void> deleteByIdAsync(ID id);

    /**
     * Asynchronously deletes a given entity.
     *
     * @param entity must not be {@literal null}.
     * @return a CompletableFuture that completes when the delete operation is done
     */
    @Async
    CompletableFuture<Void> deleteAsync(T entity);

    /**
     * Asynchronously returns the number of entities available.
     *
     * @return a CompletableFuture wrapping the number of entities
     */
    @Async
    CompletableFuture<Long> countAsync();

    /**
     * Asynchronously returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return a CompletableFuture wrapping true if an entity with the given id exists, false otherwise.
     */
    @Async
    CompletableFuture<Boolean> existsByIdAsync(ID id);
}

