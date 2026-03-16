package com.framework.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * APIAssertions - Fluent assertion helper for REST API responses.
 *
 * <p>SOLID: Single Responsibility - centralizes API response assertion logic.</p>
 * <p>Wraps AssertJ to provide meaningful failure messages for API tests.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 *   APIAssertions.assertResponse(response)
 *       .hasStatusCode(200)
 *       .hasFieldValue("name", "Buddy")
 *       .hasNonNullField("id");
 * }</pre>
 */
public class APIAssertions {

    private static final Logger log = LogManager.getLogger(APIAssertions.class);

    private final Response response;

    private APIAssertions(Response response) {
        this.response = response;
    }

    /**
     * Entry point - wraps a Response for fluent assertions.
     */
    public static APIAssertions assertResponse(Response response) {
        assertThat(response).as("Response must not be null").isNotNull();
        return new APIAssertions(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Status assertions
    // ─────────────────────────────────────────────────────────────────────────

    public APIAssertions hasStatusCode(int expectedCode) {
        int actual = response.statusCode();
        log.debug("Asserting status code: expected={} actual={}", expectedCode, actual);
        assertThat(actual)
                .as("HTTP Status Code")
                .isEqualTo(expectedCode);
        return this;
    }

    public APIAssertions hasStatusCodeIn(int... codes) {
        assertThat(response.statusCode())
                .as("HTTP Status Code should be one of the expected codes")
                .isIn(intArrayToList(codes));
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Body / Field assertions
    // ─────────────────────────────────────────────────────────────────────────

    public APIAssertions hasFieldValue(String jsonPath, Object expectedValue) {
        Object actual = response.jsonPath().get(jsonPath);
        log.debug("Asserting field [{}]: expected={} actual={}", jsonPath, expectedValue, actual);
        assertThat(actual)
                .as("Response field: " + jsonPath)
                .isEqualTo(expectedValue);
        return this;
    }

    public APIAssertions hasFieldContaining(String jsonPath, String expectedSubstring) {
        String actual = response.jsonPath().getString(jsonPath);
        assertThat(actual)
                .as("Response field [" + jsonPath + "] should contain: " + expectedSubstring)
                .containsIgnoringCase(expectedSubstring);
        return this;
    }

    public APIAssertions hasNonNullField(String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        assertThat(value)
                .as("Response field [" + jsonPath + "] should not be null")
                .isNotNull();
        return this;
    }

    public APIAssertions hasNonEmptyBody() {
        String body = response.getBody().asString();
        assertThat(body)
                .as("Response body should not be empty")
                .isNotBlank();
        return this;
    }

    public APIAssertions bodyContains(String text) {
        assertThat(response.getBody().asString())
                .as("Response body should contain: " + text)
                .contains(text);
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Header assertions
    // ─────────────────────────────────────────────────────────────────────────

    public APIAssertions hasContentType(String contentType) {
        assertThat(response.contentType())
                .as("Response Content-Type")
                .containsIgnoringCase(contentType);
        return this;
    }

    public APIAssertions hasHeader(String headerName) {
        assertThat(response.header(headerName))
                .as("Response should have header: " + headerName)
                .isNotNull();
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Response time
    // ─────────────────────────────────────────────────────────────────────────

    public APIAssertions respondsWithinMs(long maxMillis) {
        long actual = response.time();
        log.debug("Response time: {}ms (max: {}ms)", actual, maxMillis);
        assertThat(actual)
                .as("Response time should be under " + maxMillis + "ms")
                .isLessThanOrEqualTo(maxMillis);
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // List assertions
    // ─────────────────────────────────────────────────────────────────────────

    public APIAssertions listIsNotEmpty(String jsonPath) {
        List<?> list = response.jsonPath().getList(jsonPath);
        assertThat(list)
                .as("List at path [" + jsonPath + "] should not be empty")
                .isNotNull()
                .isNotEmpty();
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Log helper
    // ─────────────────────────────────────────────────────────────────────────

    public APIAssertions logResponse() {
        log.info("Response Status : {}", response.statusCode());
        log.info("Response Time   : {}ms", response.time());
        log.info("Response Body   : {}", JsonUtils.prettyPrint(response.getBody().asString()));
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<Integer> intArrayToList(int[] arr) {
        List<Integer> list = new java.util.ArrayList<>();
        for (int i : arr) list.add(i);
        return list;
    }
}
