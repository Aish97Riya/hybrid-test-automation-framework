package com.framework.config;

import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ConfigManager - Singleton factory for environment configuration.
 *
 * <p>Design Patterns Applied:</p>
 * <ul>
 *   <li><b>Singleton</b> - single instance per JVM, thread-safe via volatile + double-checked locking</li>
 *   <li><b>Factory</b> - delegates creation to ConfigFactory (OWNER)</li>
 * </ul>
 *
 * <p>SOLID: Single Responsibility - only manages config loading lifecycle.</p>
 */
public final class ConfigManager {

    private static final Logger log = LogManager.getLogger(ConfigManager.class);

    private static volatile EnvironmentConfig instance;

    private ConfigManager() {
        // Prevent instantiation - utility class
    }

    /**
     * Returns the singleton EnvironmentConfig instance.
     * Thread-safe double-checked locking pattern.
     *
     * @return EnvironmentConfig loaded for the active environment
     */
    public static EnvironmentConfig getConfig() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    String env = System.getProperty("env", "qa");
                    System.setProperty("env", env);
                    instance = ConfigFactory.create(EnvironmentConfig.class, System.getProperties());
                    log.info("ConfigManager initialized for environment: [{}]", env.toUpperCase());
                    log.info("UI Base URL: {}", instance.uiBaseUrl());
                    log.info("API Base URL: {}", instance.apiBaseUrl());
                    log.info("Browser: {}", instance.browser());
                }
            }
        }
        return instance;
    }

    /**
     * Resets the config instance - useful for testing config reloads.
     * Should NOT be called in production test runs.
     */
    public static synchronized void reset() {
        instance = null;
        log.warn("ConfigManager instance has been reset.");
    }
}
