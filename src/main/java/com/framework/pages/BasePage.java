package com.framework.pages;

import com.framework.config.ConfigManager;
import com.framework.core.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BasePage - Abstract base class for all Page Objects.
 *
 * <p>Design Patterns Applied:</p>
 * <ul>
 *   <li><b>Page Object Model</b> - encapsulates page element interactions</li>
 *   <li><b>Dependency Injection</b> - WebDriver injected via DriverManager (no direct instantiation)</li>
 * </ul>
 *
 * <p>SOLID: Single Responsibility - each subclass handles one page's interactions.</p>
 * <p>SOLID: Liskov Substitution - all page objects are substitutable via this base.</p>
 * <p>SOLID: Open/Closed - base provides utility methods; pages extend without modification.</p>
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * Constructor initializes PageFactory elements and WebDriverWait.
     * WebDriver is resolved via DriverManager (Dependency Injection).
     */
    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigManager.getConfig().explicitWait()));
        PageFactory.initElements(driver, this);
        log.debug("Page initialized: {}", this.getClass().getSimpleName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Navigates to the given URL.
     */
    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    /**
     * Returns the current page title.
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Returns the current URL.
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Waits
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Waits until an element is visible.
     */
    protected WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until an element located by locator is visible.
     */
    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until an element is clickable.
     */
    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until URL contains the given fragment.
     */
    protected boolean waitForUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Interactions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clicks an element after waiting for it to be clickable.
     */
    protected void click(WebElement element) {
        log.debug("Clicking element: {}", getElementDescription(element));
        waitForClickable(element).click();
    }

    /**
     * Types text into an element after clearing it.
     */
    protected void type(WebElement element, String text) {
        log.debug("Typing '{}' into element: {}", text, getElementDescription(element));
        waitForVisibility(element).clear();
        element.sendKeys(text);
    }

    /**
     * Returns the trimmed text of an element.
     */
    protected String getText(WebElement element) {
        return waitForVisibility(element).getText().trim();
    }

    /**
     * Returns the value attribute of an element.
     */
    protected String getValue(WebElement element) {
        return waitForVisibility(element).getAttribute("value");
    }

    /**
     * Checks whether an element is displayed.
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            log.debug("Element not displayed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Scrolls element into view using JavaScript.
     */
    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Clicks an element using JavaScript (fallback for intercepted clicks).
     */
    protected void jsClick(WebElement element) {
        log.debug("JS click on element: {}", getElementDescription(element));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Screenshot
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Captures a screenshot and saves it to the configured output directory.
     *
     * @param testName name to use in the screenshot filename
     * @return absolute path to the saved screenshot, or null on failure
     */
    public String captureScreenshot(String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testName + "_" + timestamp + ".png";
            Path screenshotDir = Paths.get("test-output", "screenshots");
            Files.createDirectories(screenshotDir);
            Path path = screenshotDir.resolve(fileName);
            Files.write(path, screenshot);
            log.info("Screenshot saved: {}", path.toAbsolutePath());
            return path.toAbsolutePath().toString();
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage(), e);
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String getElementDescription(WebElement element) {
        try {
            return element.toString().replaceAll("\\s+", " ");
        } catch (Exception e) {
            return "unknown-element";
        }
    }
}
