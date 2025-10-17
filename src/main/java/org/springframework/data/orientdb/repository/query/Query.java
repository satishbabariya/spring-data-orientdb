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
package org.springframework.data.orientdb.repository.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.QueryAnnotation;

/**
 * Annotation to declare a custom OrientDB SQL query on a repository method.
 *
 * <p>Example usage:</p>
 * <pre>
 * public interface UserRepository extends OrientDBRepository&lt;User, ORID&gt; {
 *     
 *     &#64;Query("SELECT FROM User WHERE email = :email")
 *     User findByEmail(&#64;Param("email") String email);
 *     
 *     &#64;Query("SELECT FROM User WHERE department = :dept AND active = true")
 *     List&lt;User&gt; findActiveUsersByDepartment(&#64;Param("dept") String department);
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@QueryAnnotation
public @interface Query {

    /**
     * Defines the OrientDB SQL query to be executed when the annotated method is called.
     * Uses named parameters (e.g., :paramName) that correspond to method parameters annotated with @Param.
     *
     * @return the SQL query string
     */
    String value();

    /**
     * Defines if the query is a count query.
     *
     * @return true if this is a count query, false otherwise
     */
    boolean count() default false;

    /**
     * Defines if the query is a delete query.
     *
     * @return true if this is a delete query, false otherwise
     */
    boolean delete() default false;

}

