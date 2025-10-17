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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation to configure mappings of OrientDB edges (relationships).
 * OrientDB edges are analogous to Neo4j relationships - they represent connections between vertices.
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64;Vertex("User")
 * public class User {
 *     &#64;Id
 *     private ORID id;
 *     
 *     &#64;Edge(type = "KNOWS", direction = Direction.OUTGOING)
 *     private List&lt;User&gt; friends;
 * }
 * </pre>
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Edge {

    /**
     * Returns the type (class name) of the edge.
     * @return See {@link #type()}.
     */
    @AliasFor("type")
    String value() default "";

    /**
     * Returns the type (class name) of the edge.
     * @return The type of the edge in OrientDB.
     */
    @AliasFor("value")
    String type() default "";

    /**
     * If {@code direction} is {@link Direction#OUTGOING}, then the attribute annotated
     * with {@link Edge} will be the target vertex of the edge and the class
     * containing the annotated attribute will be the source vertex.
     * <p>
     * If {@code direction} is {@link Direction#INCOMING}, then the attribute annotated
     * with {@link Edge} will be the source vertex of the edge and the class
     * containing the annotated attribute will be the target vertex.
     * @return The direction of the edge.
     */
    Direction direction() default Direction.OUTGOING;

    /**
     * Set this attribute to {@literal false} if you don't want updates on an aggregate
     * root to be cascaded to related objects.
     * @return whether updates to the owning instance should be cascaded to the related objects
     */
    boolean cascadeUpdates() default true;

    /**
     * Enumeration of the direction an edge can take.
     *
     * @since 1.0
     */
    enum Direction {

        /**
         * Describes an outgoing edge (from this vertex to another).
         */
        OUTGOING,

        /**
         * Describes an incoming edge (from another vertex to this one).
         */
        INCOMING,

        /**
         * Describes a bidirectional edge (direction doesn't matter).
         */
        BOTH;

        public Direction opposite() {
            if (this == OUTGOING) return INCOMING;
            if (this == INCOMING) return OUTGOING;
            return BOTH;
        }
    }

}

