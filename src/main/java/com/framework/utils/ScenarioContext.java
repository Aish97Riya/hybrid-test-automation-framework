package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ScenarioContext - Shared in-memory state container for Cucumber step definitions.
 *
 * <p>Design Patterns Applied:</p>
 * <ul>
 *   <li><b>Dependency Injection</b> - injected into step definition classes via Cucumber's PicoContainer</li>
 * </ul>
 *
 * <p>SOLID: Single Responsibility - only stores and retrieves scenario-scoped data.</p>
 *
 * <p>Usage: Declare as a constructor parameter in step definition classes.
 * Cucumber's PicoContainer will inject the same instance across all step classes in a scenario.</p>
 *
 * <pre>{@code
 * public class LoginSteps {
 *     private final ScenarioContext context;
 *     public LoginSteps(ScenarioContext context) { this.context = context; }
 * }
 * }</pre>
 */
public class ScenarioContext {

    private static final Logger log = LogManager.getLogger(ScenarioContext.class);

    private final Map<String, Object> context = new HashMap<>();

    /**
     * Stores a value in the context under the given key.
     *
     * @param key   context key
     * @param value value to store
     */
    public void set(String key, Object value) {
        log.debug("ScenarioContext.set: [{}] = {}", key, value);
        context.put(key, value);
    }

    /**
     * Retrieves a value from the context by key.
     *
     * @param key          context key
     * @param expectedType expected type class
     * @param <T>          type parameter
     * @return typed value
     * @throws IllegalStateException if key not found
     */
    public <T> T get(String key, Class<T> expectedType) {
        Object value = context.get(key);
        if (value == null) {
            throw new IllegalStateException(
                    "ScenarioContext: No value found for key [" + key + "]. " +
                    "Available keys: " + context.keySet());
        }
        log.debug("ScenarioContext.get: [{}] = {}", key, value);
        return expectedType.cast(value);
    }

    /**
     * Retrieves an optional value from context.
     */
    public <T> Optional<T> getOptional(String key, Class<T> expectedType) {
        Object value = context.get(key);
        if (value == null) return Optional.empty();
        return Optional.of(expectedType.cast(value));
    }

    /**
     * Checks if a key exists in the context.
     */
    public boolean contains(String key) {
        return context.containsKey(key);
    }

    /**
     * Removes a key from the context.
     */
    public void remove(String key) {
        context.remove(key);
    }

    /**
     * Clears all context values. Useful in @After hooks.
     */
    public void clear() {
        log.debug("ScenarioContext cleared. {} keys removed.", context.size());
        context.clear();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Commonly used context keys (constants)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Keys {
        public static final String PET_ID        = "petId";
        public static final String PET_NAME      = "petName";
        public static final String RESPONSE       = "response";
        public static final String STATUS_CODE    = "statusCode";
        public static final String PRODUCT_NAME  = "productName";
        public static final String CART_COUNT    = "cartCount";

        private Keys() {}
    }
}
