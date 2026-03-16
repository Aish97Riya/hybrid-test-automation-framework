package com.framework.core.driver;

import com.framework.config.ConfigManager;
import com.framework.core.factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/**
 * DriverManager - ThreadLocal-based Singleton WebDriver container.
 *
 * <p>Design Patterns Applied:</p>
 * <ul>
 *   <li><b>Singleton</b> - one WebDriver per thread (ThreadLocal ensures parallel safety)</li>
 *   <li><b>Factory</b> - delegates browser instantiation to {@link DriverFactory}</li>
 * </ul>
 *
 * <p>SOLID: Open/Closed - new browsers added in DriverFactory without touching this class.</p>
 * <p>SOLID: Single Responsibility - only manages driver lifecycle (init/get/quit).</p>
 */
public final class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);

    /**
     * ThreadLocal ensures each test thread gets its own isolated WebDriver instance.
     * Critical for parallel execution safety.
     */
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Utility class - no instantiation
    }

    /**
     * Initializes a new WebDriver for the current thread.
     * Browser type and capabilities are resolved from {@link ConfigManager}.
     */
    public static void initDriver() {
        String browser = ConfigManager.getConfig().browser();
        boolean headless = ConfigManager.getConfig().headless();

        log.info("Initializing [{}] driver | headless={} | thread={}", browser, headless,
                Thread.currentThread().getName());

        WebDriver driver = DriverFactory.createDriver(browser, headless);

        configureTimeouts(driver);
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        log.info("WebDriver initialized successfully for thread: {}", Thread.currentThread().getName());
    }

    /**
     * Returns the WebDriver instance bound to the current thread.
     *
     * @return WebDriver for current thread
     * @throws IllegalStateException if driver not initialized
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is not initialized for thread: " + Thread.currentThread().getName() +
                    ". Call DriverManager.initDriver() in your @BeforeMethod.");
        }
        return driver;
    }

    /**
     * Quits the WebDriver and removes it from ThreadLocal to prevent memory leaks.
     * Must be called in @AfterMethod to ensure cleanup even on failures.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver quit successfully for thread: {}", Thread.currentThread().getName());
            } catch (Exception e) {
                log.error("Error while quitting WebDriver: {}", e.getMessage(), e);
            } finally {
                driverThreadLocal.remove(); // Critical: prevent ThreadLocal memory leak
            }
        }
    }

    /**
     * Checks if a driver is currently active for this thread.
     *
     * @return true if driver is initialized
     */
    public static boolean isDriverActive() {
        return driverThreadLocal.get() != null;
    }

    /**
     * Applies timeout configurations from config.
     */
    private static void configureTimeouts(WebDriver driver) {
        var config = ConfigManager.getConfig();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.implicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.pageLoadTimeout()));
        log.debug("Timeouts set: implicit={}s, pageLoad={}s",
                config.implicitWait(), config.pageLoadTimeout());
    }
}
