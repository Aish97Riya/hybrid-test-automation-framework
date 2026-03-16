package com.framework.utils;

import com.framework.config.ConfigManager;
import com.framework.core.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils - Centralized explicit wait utility for UI tests.
 *
 * <p>SOLID: Single Responsibility - only provides wait utility methods.</p>
 * <p>SOLID: Dependency Inversion - depends on DriverManager abstraction.</p>
 */
public final class WaitUtils {

    private static final Logger log = LogManager.getLogger(WaitUtils.class);

    private WaitUtils() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Factory methods for WebDriverWait
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns a WebDriverWait using the configured explicit wait timeout.
     */
    public static WebDriverWait defaultWait() {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(ConfigManager.getConfig().explicitWait()));
    }

    /**
     * Returns a WebDriverWait with a custom timeout in seconds.
     */
    public static WebDriverWait customWait(int seconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(seconds));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Element Waits
    // ─────────────────────────────────────────────────────────────────────────

    public static WebElement waitForVisible(By locator) {
        log.debug("Waiting for visibility: {}", locator);
        return defaultWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisible(WebElement element) {
        return defaultWait().until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickable(By locator) {
        log.debug("Waiting for clickable: {}", locator);
        return defaultWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickable(WebElement element) {
        return defaultWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    public static boolean waitForInvisibility(By locator) {
        log.debug("Waiting for invisibility: {}", locator);
        return defaultWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForInvisibility(WebElement element) {
        return defaultWait().until(ExpectedConditions.invisibilityOf(element));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Text / Attribute Waits
    // ─────────────────────────────────────────────────────────────────────────

    public static boolean waitForTextPresent(WebElement element, String text) {
        log.debug("Waiting for text '{}' in element", text);
        return defaultWait().until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public static boolean waitForTextInValue(By locator, String text) {
        return defaultWait().until(ExpectedConditions.textToBePresentInElementValue(locator, text));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // URL / Title Waits
    // ─────────────────────────────────────────────────────────────────────────

    public static boolean waitForUrlContains(String fragment) {
        log.debug("Waiting for URL to contain: {}", fragment);
        return defaultWait().until(ExpectedConditions.urlContains(fragment));
    }

    public static boolean waitForTitleContains(String title) {
        return defaultWait().until(ExpectedConditions.titleContains(title));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Custom condition
    // ─────────────────────────────────────────────────────────────────────────

    public static <T> T waitFor(ExpectedCondition<T> condition) {
        return defaultWait().until(condition);
    }

    public static <T> T waitFor(ExpectedCondition<T> condition, int timeoutSeconds) {
        return customWait(timeoutSeconds).until(condition);
    }
}
