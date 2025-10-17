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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

/**
 * Annotation to enable auditing in OrientDB repositories.
 * When enabled, entities annotated with @CreatedDate, @LastModifiedDate,
 * @CreatedBy, and @LastModifiedBy will have these fields automatically populated.
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Configuration
 * &#64;EnableOrientDBRepositories
 * &#64;EnableOrientDBAuditing
 * public class OrientDBConfig extends AbstractOrientDBConfiguration {
 *     
 *     &#64;Bean
 *     public AuditorAware&lt;String&gt; auditorProvider() {
 *         return () -&gt; Optional.of(SecurityContextHolder.getContext()
 *             .getAuthentication().getName());
 *     }
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.2.0
 * @see org.springframework.data.orientdb.core.schema.CreatedDate
 * @see org.springframework.data.orientdb.core.schema.LastModifiedDate
 * @see org.springframework.data.orientdb.core.schema.CreatedBy
 * @see org.springframework.data.orientdb.core.schema.LastModifiedBy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(OrientDBAuditingRegistrar.class)
public @interface EnableOrientDBAuditing {

    /**
     * Configures the {@link AuditorAware} bean to be used to lookup the current principal.
     *
     * @return the bean name of the {@link AuditorAware} bean
     */
    String auditorAwareRef() default "";

    /**
     * Configures whether to set creation and modification dates.
     *
     * @return true to enable date auditing, false to disable
     */
    boolean setDates() default true;

    /**
     * Configures whether to modify creation and modification information on entity updates.
     *
     * @return true to update modification info on updates
     */
    boolean modifyOnCreate() default true;

    /**
     * Configures the name of the {@link DateTimeProvider} bean to use for setting timestamps.
     *
     * @return the bean name or empty string to use default
     */
    String dateTimeProviderRef() default "";

}

