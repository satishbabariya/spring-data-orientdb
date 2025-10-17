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
package org.springframework.data.orientdb.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.orientechnologies.orient.core.id.ORID;
import org.springframework.data.mapping.model.SimpleTypeHolder;

/**
 * Simple type holder for OrientDB, defining which types are considered "simple"
 * and should be stored directly rather than as embedded documents or references.
 *
 * @author Spring Data OrientDB Team
 * @since 1.0
 */
public class OrientDBSimpleTypes {

    private static final Set<Class<?>> ORIENTDB_SIMPLE_TYPES;

    static {
        Set<Class<?>> simpleTypes = new HashSet<>();
        
        // OrientDB-specific types
        simpleTypes.add(ORID.class);
        
        // Temporal types
        simpleTypes.add(Date.class);
        simpleTypes.add(LocalDate.class);
        simpleTypes.add(LocalTime.class);
        simpleTypes.add(LocalDateTime.class);
        
        // Numeric types
        simpleTypes.add(BigDecimal.class);
        simpleTypes.add(BigInteger.class);
        
        ORIENTDB_SIMPLE_TYPES = Collections.unmodifiableSet(simpleTypes);
    }

    public static final SimpleTypeHolder HOLDER = new SimpleTypeHolder(ORIENTDB_SIMPLE_TYPES, true);

    private OrientDBSimpleTypes() {
        // Utility class
    }

}

