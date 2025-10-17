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

import org.springframework.context.ApplicationListener;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.util.Assert;

/**
 * Event listener to populate auditing fields before entities are saved.
 * Handles @CreatedDate, @LastModifiedDate, @CreatedBy, and @LastModifiedBy annotations.
 *
 * @author Spring Data OrientDB Team
 * @since 1.2.0
 */
public class AuditingEventListener implements ApplicationListener<BeforeSaveEvent> {

    private final AuditingHandler auditingHandler;

    /**
     * Creates a new {@link AuditingEventListener} using the provided {@link AuditingHandler}.
     *
     * @param auditingHandler must not be {@literal null}.
     */
    public AuditingEventListener(AuditingHandler auditingHandler) {
        Assert.notNull(auditingHandler, "AuditingHandler must not be null!");
        this.auditingHandler = auditingHandler;
    }

    @Override
    public void onApplicationEvent(BeforeSaveEvent event) {
        Object entity = event.getEntity();
        
        if (entity != null) {
            auditingHandler.markModified(entity);
        }
    }

}

