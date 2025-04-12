package com.aitasks.core;

import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = Configuration.class.getClassLoader()
                .getResourceAsStream("config/application.properties")) {
            properties.load(input);
        } catch (Exception e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
} 