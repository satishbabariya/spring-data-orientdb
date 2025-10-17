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

import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.Part;

/**
 * Parameter accessor for OrientDB queries.
 * Handles parameter binding and conversion for different query types.
 *
 * @author Spring Data OrientDB Team
 * @since 1.1.0
 */
public class OrientDBParameterAccessor extends ParametersParameterAccessor {

    public OrientDBParameterAccessor(OrientDBQueryMethod method, Object[] values) {
        super(method.getParameters(), values);
    }

    /**
     * Prepares a parameter for a specific Part type.
     * Handles special cases like LIKE patterns.
     */
    public Object prepareParameter(Object parameter, Part.Type type) {
        if (parameter == null) {
            return null;
        }

        // Handle special parameter transformations based on query type
        if (type == Part.Type.STARTING_WITH) {
            return parameter + "%";
        } else if (type == Part.Type.ENDING_WITH) {
            return "%" + parameter;
        } else if (type == Part.Type.CONTAINING || type == Part.Type.NOT_CONTAINING) {
            return "%" + parameter + "%";
        } else if (type == Part.Type.LIKE || type == Part.Type.NOT_LIKE) {
            // Assume user provides their own wildcards
            return parameter;
        } else {
            return parameter;
        }
    }

}

