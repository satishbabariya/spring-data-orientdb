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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.annotation.*;

/**
 * Annotation to enable OrientDB transaction management in Spring.
 * This annotation automatically configures {@link org.springframework.data.orientdb.transaction.OrientDBTransactionManager}
 * and enables support for Spring's {@code @Transactional} annotation.
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Configuration
 * &#64;EnableOrientDBTransactionManagement
 * &#64;EnableOrientDBRepositories
 * public class OrientDBConfig extends AbstractOrientDBConfiguration {
 *     // configuration
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.4.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableTransactionManagement
@Import(OrientDBTransactionManagementConfiguration.class)
public @interface EnableOrientDBTransactionManagement {

    /**
     * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed to standard Java
     * interface-based proxies. The default is {@code false}.
     */
    boolean proxyTargetClass() default false;
}

