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

/**
 * Annotation to customize the mapping of a field to an OrientDB property.
 * By default, field names are used as property names, but this annotation allows
 * specifying a different name.
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Vertex("User")
 * public class User {
 *     &#64;Property("user_name")
 *     private String username;
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Property {

    /**
     * Returns the name of the property in OrientDB.
     * @return See {@link #name()}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Returns the name of the property in OrientDB.
     * @return The name of the property as it will be stored in OrientDB.
     */
    @AliasFor("value")
    String name() default "";

}

