package com.Utils.CommonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Optimized configuration manager with caching and environment-specific settings
 * Provides thread-safe access to configuration properties
 */

public class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "configurator.properties";
    private static final Properties CONFIG = new Properties();
    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();
    private static volatile boolean INITIALIZED = false;

    static {
        loadConfiguration();
    }

    /**
     * Loads configuration from properties file
     */
    private static void loadConfiguration(){
        if (INITIALIZED) return;

        synchronized (ConfigManager.class){
            if (INITIALIZED) return;

            try (InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)){

                if (inputStream == null){
                    throw new IOException("Configuration file not found: " + CONFIG_FILE);
                }
                CONFIG.load(inputStream);
                INITIALIZED = true;
                LOGGER.info("Configuration loaded successfully: {}", CONFIG_FILE);
            }catch (IOException e){
                LOGGER.error("Failed to load configuration from: {}", CONFIG_FILE, e);
                throw new RuntimeException("Failed to load configuration: ", e);
            }
        }
    }

    /**
     * Gets a configuration property with caching
     * @param key Property key
     * @return Property value or null if not found
     */
    public static String getProperty(String key){
        return CACHE.computeIfAbsent(key, k -> CONFIG.getProperty(k));
    }

    /**
     * Gets a configuration property with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public static String getProperty(String key, String defaultValue){
        return CACHE.computeIfAbsent(key, k -> CONFIG.getProperty(k, defaultValue));
    }

    /**
     * Gets a configuration property with caching
     * @param key Property key
     * @return Property value or null if not found
     */
    public static int getIntProperty(String key){
        return Integer.parseInt(getProperty(key));
    }

    /**
     * Gets an integer property
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Integer value
     */
    public static int getIntProperty(String key, int defaultValue){
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        }catch (NumberFormatException e){
            LOGGER.warn("Invalid integer value for property '{}', using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Gets a boolean property
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Boolean value
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue){
        String value = getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets environment-specific URL
     * @param service Service name
     * @param environment Environment (stage/prod)
     * @return Service URL
     */
    public static String getServiceUrl(String service, String environment){
        String key = service + "." + environment;
        String url = getProperty(key);

        if (url == null){
            LOGGER.warn("Service url not found for: {}", key);
        }
        return url;
    }

    /**
     * Clears the property cache
     */
    public static void clearCache(){
        CACHE.clear();
    }
}
