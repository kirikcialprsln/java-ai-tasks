package com.aitasks.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAITask implements AITask {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected boolean initialized = false;
    protected final Configuration config;
    
    protected BaseAITask() {
        this.config = new Configuration();
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public void close() throws Exception {
        cleanup();
    }
    
    protected void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Task not initialized");
        }
    }
} 