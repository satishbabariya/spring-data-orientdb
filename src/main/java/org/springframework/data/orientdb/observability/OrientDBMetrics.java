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
package org.springframework.data.orientdb.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * Provides metrics collection for OrientDB operations using Micrometer.
 * Tracks repository operation counts, timings, and errors.
 *
 * @author Spring Data OrientDB Team
 * @since 1.5.0
 */
public class OrientDBMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // Counters
    private Counter saveCounter;
    private Counter findCounter;
    private Counter deleteCounter;
    private Counter queryCounter;
    private Counter errorCounter;
    
    // Timers
    private Timer saveTimer;
    private Timer findTimer;
    private Timer deleteTimer;
    private Timer queryTimer;
    
    @Autowired(required = false)
    public OrientDBMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        if (meterRegistry != null) {
            initializeMetrics();
        }
    }
    
    private void initializeMetrics() {
        // Initialize counters
        saveCounter = Counter.builder("orientdb.repository.saves")
            .description("Number of save operations")
            .register(meterRegistry);
            
        findCounter = Counter.builder("orientdb.repository.finds")
            .description("Number of find operations")
            .register(meterRegistry);
            
        deleteCounter = Counter.builder("orientdb.repository.deletes")
            .description("Number of delete operations")
            .register(meterRegistry);
            
        queryCounter = Counter.builder("orientdb.repository.queries")
            .description("Number of query operations")
            .register(meterRegistry);
            
        errorCounter = Counter.builder("orientdb.repository.errors")
            .description("Number of operation errors")
            .register(meterRegistry);
        
        // Initialize timers
        saveTimer = Timer.builder("orientdb.repository.save.time")
            .description("Time spent in save operations")
            .register(meterRegistry);
            
        findTimer = Timer.builder("orientdb.repository.find.time")
            .description("Time spent in find operations")
            .register(meterRegistry);
            
        deleteTimer = Timer.builder("orientdb.repository.delete.time")
            .description("Time spent in delete operations")
            .register(meterRegistry);
            
        queryTimer = Timer.builder("orientdb.repository.query.time")
            .description("Time spent in query operations")
            .register(meterRegistry);
    }
    
    public void recordSave() {
        if (saveCounter != null) {
            saveCounter.increment();
        }
    }
    
    public void recordFind() {
        if (findCounter != null) {
            findCounter.increment();
        }
    }
    
    public void recordDelete() {
        if (deleteCounter != null) {
            deleteCounter.increment();
        }
    }
    
    public void recordQuery() {
        if (queryCounter != null) {
            queryCounter.increment();
        }
    }
    
    public void recordError() {
        if (errorCounter != null) {
            errorCounter.increment();
        }
    }
    
    public <T> T recordSaveTime(java.util.function.Supplier<T> operation) {
        if (saveTimer != null) {
            long start = System.nanoTime();
            try {
                return operation.get();
            } finally {
                saveTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            }
        }
        return operation.get();
    }
    
    public <T> T recordFindTime(java.util.function.Supplier<T> operation) {
        if (findTimer != null) {
            long start = System.nanoTime();
            try {
                return operation.get();
            } finally {
                findTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            }
        }
        return operation.get();
    }
    
    public void recordDeleteTime(Runnable operation) {
        if (deleteTimer != null) {
            long start = System.nanoTime();
            try {
                operation.run();
            } finally {
                deleteTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            }
        } else {
            operation.run();
        }
    }
    
    public <T> T recordQueryTime(java.util.function.Supplier<T> operation) {
        if (queryTimer != null) {
            long start = System.nanoTime();
            try {
                return operation.get();
            } finally {
                queryTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            }
        }
        return operation.get();
    }
}

