package com.aitasks.core;

/**
 * Base interface for all AI tasks
 */
public interface AITask extends AutoCloseable {
    void initialize() throws Exception;
    boolean isInitialized();
    void cleanup() throws Exception;
    String getTaskName();
    String getTaskDescription();
} 