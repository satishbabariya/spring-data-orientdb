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
package org.springframework.data.orientdb.core.mapping.event;

import java.lang.reflect.Method;

import org.springframework.data.orientdb.core.schema.PostLoad;
import org.springframework.data.orientdb.core.schema.PrePersist;
import org.springframework.data.orientdb.core.schema.PreRemove;
import org.springframework.util.ReflectionUtils;

/**
 * Handles invocation of entity lifecycle callback methods.
 * Supports @PrePersist, @PostLoad, and @PreRemove annotations.
 *
 * @author Spring Data OrientDB Team
 * @since 1.3.0
 */
public class EntityCallbackHandler {

    /**
     * Invoke @PrePersist methods on the entity before saving.
     */
    public static void invokePrePersist(Object entity) {
        if (entity == null) {
            return;
        }

        Method[] methods = ReflectionUtils.getAllDeclaredMethods(entity.getClass());
        for (Method method : methods) {
            if (method.isAnnotationPresent(PrePersist.class)) {
                ReflectionUtils.makeAccessible(method);
                ReflectionUtils.invokeMethod(method, entity);
            }
        }
    }

    /**
     * Invoke @PostLoad methods on the entity after loading.
     */
    public static void invokePostLoad(Object entity) {
        if (entity == null) {
            return;
        }

        Method[] methods = ReflectionUtils.getAllDeclaredMethods(entity.getClass());
        for (Method method : methods) {
            if (method.isAnnotationPresent(PostLoad.class)) {
                ReflectionUtils.makeAccessible(method);
                ReflectionUtils.invokeMethod(method, entity);
            }
        }
    }

    /**
     * Invoke @PreRemove methods on the entity before deleting.
     */
    public static void invokePreRemove(Object entity) {
        if (entity == null) {
            return;
        }

        Method[] methods = ReflectionUtils.getAllDeclaredMethods(entity.getClass());
        for (Method method : methods) {
            if (method.isAnnotationPresent(PreRemove.class)) {
                ReflectionUtils.makeAccessible(method);
                ReflectionUtils.invokeMethod(method, entity);
            }
        }
    }

}

