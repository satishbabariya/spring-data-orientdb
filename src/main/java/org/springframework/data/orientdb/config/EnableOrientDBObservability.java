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
package org.springframework.data.orientdb.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to enable observability (metrics) for OrientDB repositories.
 * Integrates with Micrometer to provide metrics for repository operations.
 *
 * <p>Metrics provided:</p>
 * <ul>
 *   <li>orientdb.repository.saves - Counter for save operations</li>
 *   <li>orientdb.repository.finds - Counter for find operations</li>
 *   <li>orientdb.repository.deletes - Counter for delete operations</li>
 *   <li>orientdb.repository.queries - Counter for query operations</li>
 *   <li>orientdb.repository.errors - Counter for errors</li>
 *   <li>orientdb.repository.save.time - Timer for save operation duration</li>
 *   <li>orientdb.repository.find.time - Timer for find operation duration</li>
 *   <li>orientdb.repository.delete.time - Timer for delete operation duration</li>
 *   <li>orientdb.repository.query.time - Timer for query operation duration</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Configuration
 * &#64;EnableOrientDBRepositories
 * &#64;EnableOrientDBObservability
 * public class OrientDBConfig extends AbstractOrientDBConfiguration {
 *     // configuration
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.5.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(OrientDBObservabilityConfiguration.class)
public @interface EnableOrientDBObservability {
}

