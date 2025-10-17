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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.orientdb.core.mapping.OrientDBPersistentEntity;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * Creates OrientDB SQL queries from Spring Data {@link PartTree} method name parsing.
 *
 * Supported keywords:
 * - Equality: findBy{Property}
 * - Comparison: GreaterThan, LessThan, GreaterThanEqual, LessThanEqual
 * - Strings: Like, NotLike, StartingWith, EndingWith, Containing
 * - Null checks: IsNull, IsNotNull
 * - Boolean: IsTrue, IsFalse
 * - Collections: In, NotIn
 * - Logical: And, Or
 * - Sorting: OrderBy{Property}Asc/Desc
 *
 * @author Spring Data OrientDB Team
 * @since 1.1.0
 */
public class OrientDBQueryCreator {

    private final PartTree tree;
    private final OrientDBPersistentEntity<?> entity;
    private final String vertexClassName;

    public OrientDBQueryCreator(PartTree tree, OrientDBPersistentEntity<?> entity) {
        this.tree = tree;
        this.entity = entity;
        this.vertexClassName = entity.getVertexClassName();
    }

    /**
     * Creates the main SELECT query.
     */
    public String createQuery(Object[] parameters) {
        StringBuilder query = new StringBuilder("SELECT FROM ");
        query.append(vertexClassName);

        // Add WHERE clause
        String whereClause = createWhereClause();
        if (!whereClause.isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }

        // Add ORDER BY clause
        if (tree.getSort().isSorted()) {
            query.append(" ORDER BY ");
            boolean first = true;
            for (var order : tree.getSort()) {
                if (!first) query.append(", ");
                query.append(order.getProperty());
                query.append(" ").append(order.getDirection().name());
                first = false;
            }
        }

        // Add LIMIT clause if present
        if (tree.isLimiting()) {
            query.append(" LIMIT ").append(tree.getMaxResults());
        }

        return query.toString();
    }

    /**
     * Creates a COUNT query.
     */
    public String createCountQuery() {
        StringBuilder query = new StringBuilder("SELECT count(*) as count FROM ");
        query.append(vertexClassName);

        String whereClause = createWhereClause();
        if (!whereClause.isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }

        return query.toString();
    }

    /**
     * Creates a DELETE query.
     */
    public String createDeleteQuery() {
        StringBuilder query = new StringBuilder("DELETE VERTEX ");
        query.append(vertexClassName);

        String whereClause = createWhereClause();
        if (!whereClause.isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }

        return query.toString();
    }

    /**
     * Creates the WHERE clause from the PartTree.
     */
    private String createWhereClause() {
        List<String> conditions = new ArrayList<>();
        
        for (PartTree.OrPart orPart : tree) {
            List<String> andConditions = new ArrayList<>();
            
            for (Part part : orPart) {
                String condition = createCondition(part);
                andConditions.add(condition);
            }
            
            if (!andConditions.isEmpty()) {
                if (andConditions.size() == 1) {
                    conditions.add(andConditions.get(0));
                } else {
                    conditions.add("(" + String.join(" AND ", andConditions) + ")");
                }
            }
        }
        
        return String.join(" OR ", conditions);
    }

    /**
     * Creates a condition from a Part.
     */
    private String createCondition(Part part) {
        String property = part.getProperty().toDotPath();
        Part.Type type = part.getType();

        // Equality
        if (type == Part.Type.SIMPLE_PROPERTY) return property + " = ?";
        if (type == Part.Type.NEGATING_SIMPLE_PROPERTY) return property + " <> ?";

        // Comparison
        if (type == Part.Type.GREATER_THAN) return property + " > ?";
        if (type == Part.Type.GREATER_THAN_EQUAL) return property + " >= ?";
        if (type == Part.Type.LESS_THAN) return property + " < ?";
        if (type == Part.Type.LESS_THAN_EQUAL) return property + " <= ?";
        if (type == Part.Type.BETWEEN) return property + " BETWEEN ? AND ?";

        // String operations
        if (type == Part.Type.LIKE) return property + " LIKE ?";
        if (type == Part.Type.NOT_LIKE) return property + " NOT LIKE ?";
        if (type == Part.Type.STARTING_WITH) return property + " LIKE ?";
        if (type == Part.Type.ENDING_WITH) return property + " LIKE ?";
        if (type == Part.Type.CONTAINING) return property + " LIKE ?";
        if (type == Part.Type.NOT_CONTAINING) return property + " NOT LIKE ?";

        // Null checks
        if (type == Part.Type.IS_NULL) return property + " IS NULL";
        if (type == Part.Type.IS_NOT_NULL) return property + " IS NOT NULL";

        // Boolean
        if (type == Part.Type.TRUE) return property + " = true";
        if (type == Part.Type.FALSE) return property + " = false";

        // Collections
        if (type == Part.Type.IN) return property + " IN ?";
        if (type == Part.Type.NOT_IN) return property + " NOT IN ?";

        // Regex
        if (type == Part.Type.REGEX) return property + " MATCHES ?";

        throw new UnsupportedOperationException(
            "Part type " + type + " is not supported in OrientDB queries");
    }

}

