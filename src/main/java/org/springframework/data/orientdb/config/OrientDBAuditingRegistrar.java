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

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.auditing.config.AuditingBeanDefinitionRegistrarSupport;
import org.springframework.data.auditing.config.AuditingConfiguration;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.orientdb.core.mapping.event.AuditingEventListener;
import org.springframework.util.Assert;

/**
 * {@link ImportBeanDefinitionRegistrar} to enable {@link EnableOrientDBAuditing} annotation.
 *
 * @author Spring Data OrientDB Team
 * @since 1.2.0
 */
public class OrientDBAuditingRegistrar extends AuditingBeanDefinitionRegistrarSupport {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        // Register PersistentEntities bean that wraps the OrientDB mapping context
        if (!registry.containsBeanDefinition("persistentEntities")) {
            BeanDefinitionBuilder persistentEntitiesBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(PersistentEntities.class);
            persistentEntitiesBuilder.setFactoryMethod("of");
            persistentEntitiesBuilder.addConstructorArgReference("orientDBMappingContext");
            
            registry.registerBeanDefinition("persistentEntities", 
                persistentEntitiesBuilder.getBeanDefinition());
        }
        
        // Now register auditing beans
        super.registerBeanDefinitions(annotationMetadata, registry);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableOrientDBAuditing.class;
    }

    @Override
    protected String getAuditingHandlerBeanName() {
        return "orientDBAuditingHandler";
    }

    @Override
    protected void registerAuditListenerBeanDefinition(
            org.springframework.beans.factory.config.BeanDefinition auditingHandlerDefinition,
            BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder
            .rootBeanDefinition(AuditingEventListener.class);

        // Pass the bean name/reference rather than the definition itself
        // Spring will automatically wrap it in an ObjectFactory
        builder.addConstructorArgReference(getAuditingHandlerBeanName());

        registerInfrastructureBeanWithId(
            builder.getRawBeanDefinition(),
            AuditingEventListener.class.getName(),
            registry
        );
    }

    @Override
    protected AuditingConfiguration getConfiguration(AnnotationMetadata annotationMetadata) {
        return new AuditingConfiguration() {
            @Override
            public String getAuditorAwareRef() {
                return annotationMetadata.getAnnotationAttributes(getAnnotation().getName())
                    .get("auditorAwareRef").toString();
            }

            @Override
            public boolean isSetDates() {
                return (boolean) annotationMetadata.getAnnotationAttributes(getAnnotation().getName())
                    .get("setDates");
            }

            @Override
            public boolean isModifyOnCreate() {
                return (boolean) annotationMetadata.getAnnotationAttributes(getAnnotation().getName())
                    .get("modifyOnCreate");
            }

            @Override
            public String getDateTimeProviderRef() {
                return annotationMetadata.getAnnotationAttributes(getAnnotation().getName())
                    .get("dateTimeProviderRef").toString();
            }
        };
    }

}

