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
package org.springframework.data.orientdb.core.schema;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.annotation.Persistent;

/**
 * Annotation to configure the mapping from an OrientDB vertex class to a Java class and vice versa.
 * OrientDB vertices are analogous to Neo4j nodes - they represent entities in the graph.
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Vertex("User")
 * public class User {
 *     &#64;Id
 *     private ORID id;
 *     private String name;
 *     // ...
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Persistent
public @interface Vertex {

    /**
     * Returns the OrientDB vertex class name.
     * @return See {@link #value()}.
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Returns the OrientDB vertex class name.
     * @return The OrientDB vertex class name that is supposed to be mapped to the
     * class annotated with {@link Vertex @Vertex}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Returns the parent vertex class if this vertex extends another.
     * OrientDB supports class inheritance.
     * @return The parent vertex class name, or empty string if none.
     */
    String extendsClass() default "";

}

