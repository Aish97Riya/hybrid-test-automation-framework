package com.framework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * JsonUtils - Singleton ObjectMapper wrapper for JSON serialization.
 *
 * <p>SOLID: Single Responsibility - only handles JSON marshalling/unmarshalling.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 *   Pet pet = JsonUtils.fromJson(responseBody, Pet.class);
 *   String json = JsonUtils.toJson(pet);
 *   Map<String,Object> map = JsonUtils.toMap(responseBody);
 * }</pre>
 */
public final class JsonUtils {

    private static final Logger log = LogManager.getLogger(JsonUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private JsonUtils() {}

    /**
     * Serializes an object to a JSON string.
     */
    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    /**
     * Deserializes a JSON string to the specified class.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("JSON deserialization failed for type: " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Deserializes a JSON string to a List of the specified type.
     */
    public static <T> List<T> fromJsonList(String json, Class<T> elementClass) {
        try {
            return MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (Exception e) {
            log.error("Failed to deserialize JSON to List<{}>: {}", elementClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("JSON list deserialization failed", e);
        }
    }

    /**
     * Deserializes JSON to a Map.
     */
    public static Map<String, Object> toMap(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Failed to convert JSON to Map: {}", e.getMessage(), e);
            throw new RuntimeException("JSON to Map conversion failed", e);
        }
    }

    /**
     * Reads a JSON file from the classpath resources.
     */
    public static <T> T fromJsonFile(String resourcePath, Class<T> clazz) {
        try (InputStream is = JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return MAPPER.readValue(is, clazz);
        } catch (IOException e) {
            log.error("Failed to read JSON from resource [{}]: {}", resourcePath, e.getMessage(), e);
            throw new RuntimeException("Failed to read JSON resource: " + resourcePath, e);
        }
    }

    /**
     * Pretty-prints a JSON string for logging/debugging.
     */
    public static String prettyPrint(String json) {
        try {
            Object obj = MAPPER.readValue(json, Object.class);
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json; // Return as-is if pretty print fails
        }
    }
}
