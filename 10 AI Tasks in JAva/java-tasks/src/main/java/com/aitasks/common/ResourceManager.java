package com.aitasks.common;

public interface ResourceManager extends AutoCloseable {
    void initialize() throws Exception;
    boolean isInitialized();
    void cleanup() throws Exception;
} 