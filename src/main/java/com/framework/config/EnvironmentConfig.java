package com.framework.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

/**
 * EnvironmentConfig - Interface-based configuration using OWNER library.
 *
 * <p>SOLID: Interface Segregation - separate config contracts per concern.</p>
 * <p>Supports QA, Stage, Prod environments resolved at runtime.</p>
 */
@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "system:env",
    "classpath:configs/${env}.properties"
})
public interface EnvironmentConfig extends Config {

    /**
     * Base URL for the UI under test.
     */
    @Key("ui.base.url")
    @DefaultValue("https://www.saucedemo.com")
    String uiBaseUrl();

    /**
     * Base URL for the API under test.
     */
    @Key("api.base.url")
    @DefaultValue("https://petstore.swagger.io/v2")
    String apiBaseUrl();

    /**
     * Browser to use for UI tests.
     */
    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    /**
     * Whether to run browser in headless mode.
     */
    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    /**
     * Implicit wait timeout in seconds.
     */
    @Key("implicit.wait")
    @DefaultValue("10")
    int implicitWait();

    /**
     * Explicit wait timeout in seconds.
     */
    @Key("explicit.wait")
    @DefaultValue("20")
    int explicitWait();

    /**
     * Page load timeout in seconds.
     */
    @Key("page.load.timeout")
    @DefaultValue("30")
    int pageLoadTimeout();

    /**
     * UI login username.
     */
    @Key("ui.username")
    @DefaultValue("standard_user")
    String uiUsername();

    /**
     * UI login password.
     */
    @Key("ui.password")
    @DefaultValue("secret_sauce")
    String uiPassword();

    /**
     * API default content type.
     */
    @Key("api.content.type")
    @DefaultValue("application/json")
    String apiContentType();

    /**
     * API connection timeout in ms.
     */
    @Key("api.connection.timeout")
    @DefaultValue("10000")
    int apiConnectionTimeout();

    /**
     * Screenshot on failure toggle.
     */
    @Key("screenshot.on.failure")
    @DefaultValue("true")
    boolean screenshotOnFailure();

    /**
     * Report output directory.
     */
    @Key("report.dir")
    @DefaultValue("test-output/extent-reports")
    String reportDir();

    /**
     * Environment name (qa / stage / prod).
     */
    @Key("env")
    @DefaultValue("qa")
    String env();
}
