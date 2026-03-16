package com.framework.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Pet - Domain model with full Getters/Setters for response deserialization,
 * Builder for request construction, and RequestBuilder for RestAssured spec.
 *
 * <p>Design Patterns: Builder (Pet.Builder + Pet.RequestBuilder in same class)</p>
 * <p>SOLID: Single Responsibility - each inner class handles one concern.</p>
 *
 * Usage - Deserialize response and validate via getters:
 * <pre>{@code
 *   Pet pet = response.as(Pet.class);
 *   pet.getId();
 *   pet.getName();
 *   pet.getStatus();
 *   pet.getCategory().getName();
 *   pet.getTags().get(0).getName();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pet {

    private static final Logger log = LogManager.getLogger(Pet.class);

    // ─────────────────────────────────────────────────────────────────────────
    // Pet Status Enum
    // ─────────────────────────────────────────────────────────────────────────

    public enum PetStatus {
        @JsonProperty("available") AVAILABLE,
        @JsonProperty("pending")   PENDING,
        @JsonProperty("sold")      SOLD
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Pet Fields
    // ─────────────────────────────────────────────────────────────────────────

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private String status;

    @JsonProperty("photoUrls")
    private List<String> photoUrls;

    @JsonProperty("tags")
    private List<Tag> tags;

    @JsonProperty("category")
    private Category category;

    // ─────────────────────────────────────────────────────────────────────────
    // No-arg constructor - required by Jackson for response.as(Pet.class)
    // ─────────────────────────────────────────────────────────────────────────

    public Pet() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Private constructor - accepts Builder for request building
    // ─────────────────────────────────────────────────────────────────────────

    private Pet(Builder builder) {
        this.id         = builder.id;
        this.name       = builder.name;
        this.status     = builder.status;
        this.photoUrls  = builder.photoUrls;
        this.tags       = builder.tags;
        this.category   = builder.category;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GETTERS - used to validate deserialized API response
    // ─────────────────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getPhotoUrls() {
        return photoUrls != null ? Collections.unmodifiableList(photoUrls) : null;
    }

    public List<Tag> getTags() {
        return tags != null ? Collections.unmodifiableList(tags) : null;
    }

    public Category getCategory() {
        return category;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SETTERS - required by Jackson for response deserialization
    // ─────────────────────────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("Pet{id=%d, name='%s', status='%s'}", id, name, status);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INNER CLASS 1: Pet Builder - fluent domain object construction
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builder for constructing Pet request payloads.
     * Calls private Pet(Builder) constructor - canonical Builder pattern.
     */
    public static final class Builder {

        private Long id;
        private String name;
        private String status = PetStatus.AVAILABLE.name().toLowerCase();
        private List<String> photoUrls = Collections.emptyList();
        private List<Tag> tags;
        private Category category;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Pet name must not be null or blank");
            }
            this.name = name;
            return this;
        }

        public Builder status(PetStatus status) {
            this.status = status.name().toLowerCase();
            return this;
        }

        public Builder photoUrls(List<String> photoUrls) {
            this.photoUrls = photoUrls;
            return this;
        }

        public Builder photoUrls(String... urls) {
            this.photoUrls = Arrays.asList(urls);
            return this;
        }

        public Builder tags(List<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public Builder category(String categoryName) {
            this.category = new Category(categoryName);
            return this;
        }

        /**
         * Validates mandatory fields and constructs Pet via private constructor.
         *
         * @return Pet instance ready to use as request payload
         * @throws IllegalStateException if name is missing
         */
        public Pet build() {
            if (name == null || name.isBlank()) {
                throw new IllegalStateException("Pet.Builder: 'name' is mandatory");
            }
            Pet pet = new Pet(this);
            log.debug("Pet built: {}", pet);
            return pet;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INNER CLASS 2: RequestBuilder - fluent RestAssured spec construction
    // ─────────────────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────
    // Nested supporting models - Tag and Category with Getters + Setters
    // ─────────────────────────────────────────────────────────────────────────

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        public Tag() {}
        public Tag(Long id, String name) { this.id = id; this.name = name; }

        public Long getId()        { return id; }
        public String getName()    { return name; }
        public void setId(Long id)         { this.id = id; }
        public void setName(String name)   { this.name = name; }

        @Override
        public String toString() {
            return String.format("Tag{id=%d, name='%s'}", id, name);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        public Category() {}
        public Category(String name)          { this.name = name; }
        public Category(Long id, String name) { this.id = id; this.name = name; }

        public Long getId()        { return id; }
        public String getName()    { return name; }
        public void setId(Long id)         { this.id = id; }
        public void setName(String name)   { this.name = name; }

        @Override
        public String toString() {
            return String.format("Category{id=%d, name='%s'}", id, name);
        }
    }
}
