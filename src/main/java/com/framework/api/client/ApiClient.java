package com.framework.api.client;

import com.framework.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * ApiClient - Singleton provider for RestAssured base specifications.
 *
 * <p>Design Patterns Applied:</p>
 * <ul>
 *   <li><b>Singleton</b> - single RequestSpec/ResponseSpec per run (thread-safe)</li>
 * </ul>
 *
 * <p>SOLID: Dependency Inversion - endpoints depend on this abstraction,
 * not raw RestAssured setup.</p>
 *
 * <p>Note: RequestSpecBuilder produces a new spec instance per call,
 * ensuring thread safety for parallel test execution.</p>
 */
public final class ApiClient {

    private static final Logger log = LogManager.getLogger(ApiClient.class);

    private ApiClient() {}

    /**
     * Builds and returns a fresh base RequestSpecification.
     * Called per request - RequestSpecBuilder is not thread-safe to share.
     *
     * @return configured RequestSpecification
     */
    public static RequestSpecification getRequestSpec() {
        log.debug("Building RequestSpec for: {}", ConfigManager.getConfig().apiBaseUrl());
        return buildRequestSpec();
    }

    /**
     * Builds and returns a fresh base ResponseSpecification.
     *
     * @return configured ResponseSpecification
     */
    public static ResponseSpecification getResponseSpec() {
        return buildResponseSpec();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private builders
    // ─────────────────────────────────────────────────────────────────────────

    private static RequestSpecification buildRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getConfig().apiBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "HybridFramework/1.0")
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();
    }

    private static ResponseSpecification buildResponseSpec() {
        return new ResponseSpecBuilder()
                .expectResponseTime(
                        org.hamcrest.Matchers.lessThan(10_000L),
                        TimeUnit.MILLISECONDS)
                .log(LogDetail.STATUS)
                .build();
    }
}
