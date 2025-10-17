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

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to enable caching for OrientDB repositories.
 * This annotation enables Spring's caching infrastructure and configures
 * default cache settings for OrientDB operations.
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Configuration
 * &#64;EnableOrientDBRepositories
 * &#64;EnableOrientDBCaching
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
@EnableCaching
@Import(OrientDBCachingConfiguration.class)
public @interface EnableOrientDBCaching {
    
    /**
     * Cache names to create for OrientDB operations.
     * Default includes "orientdb-entities" and "orientdb-queries".
     */
    String[] cacheNames() default {"orientdb-entities", "orientdb-queries"};
}

