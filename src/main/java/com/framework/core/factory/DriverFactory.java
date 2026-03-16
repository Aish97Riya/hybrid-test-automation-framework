package com.framework.core.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * DriverFactory - Factory pattern implementation for WebDriver creation.
 *
 * <p>Design Patterns Applied:</p>
 * <ul>
 *   <li><b>Factory Pattern</b> - encapsulates browser creation logic</li>
 * </ul>
 *
 * <p>SOLID: Open/Closed - new browsers added by extending the switch, not modifying existing code.</p>
 * <p>SOLID: Single Responsibility - only handles driver instantiation per browser type.</p>
 */
public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
        // Static factory - no instantiation
    }

    /**
     * Creates a WebDriver instance based on browser name.
     *
     * @param browser  browser name (chrome/firefox/edge) - case-insensitive
     * @param headless whether to run in headless mode
     * @return configured WebDriver instance
     * @throws IllegalArgumentException for unsupported browser types
     */
    public static WebDriver createDriver(String browser, boolean headless) {
        log.info("Creating WebDriver for browser: [{}] headless=[{}]", browser, headless);

        String browserLower = browser.trim().toLowerCase();
        switch (browserLower) {
            case "chrome":
                return createChromeDriver(headless);
            case "firefox":
                return createFirefoxDriver(headless);
            case "edge":
                return createEdgeDriver(headless);
            default:
                throw new IllegalArgumentException(
                        "Unsupported browser: [" + browser + "]. Supported: chrome, firefox, edge");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Chrome
    // ─────────────────────────────────────────────────────────────────────────

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildChromeOptions(headless);
        log.debug("ChromeOptions configured: {}", options.asMap());
        return new ChromeDriver(options);
    }

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--window-size=1920,1080",
            "--disable-extensions",
            "--disable-popup-blocking",
            "--ignore-certificate-errors",
            "--remote-allow-origins=*"
        );
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        return options;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Firefox
    // ─────────────────────────────────────────────────────────────────────────

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        return new FirefoxDriver(options);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Edge
    // ─────────────────────────────────────────────────────────────────────────

    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080"
        );
        return new EdgeDriver(options);
    }
}
