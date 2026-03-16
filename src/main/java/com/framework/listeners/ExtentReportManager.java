package com.framework.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReportManager - Singleton manager for ExtentReports.
 *
 * <p>Design Patterns:</p>
 * <ul>
 *   <li><b>Singleton</b> - single ExtentReports instance per run</li>
 *   <li><b>ThreadLocal</b> - each test thread gets its own ExtentTest node</li>
 * </ul>
 */
public final class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);

    private static volatile ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {}

    // ─────────────────────────────────────────────────────────────────────────
    // ExtentReports lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the singleton ExtentReports instance, creating if necessary.
     */
    public static ExtentReports getExtentReports() {
        if (extentReports == null) {
            synchronized (ExtentReportManager.class) {
                if (extentReports == null) {
                    extentReports = createExtentReports();
                }
            }
        }
        return extentReports;
    }

    /**
     * Flushes all test results to the report file.
     * Call once in @AfterSuite.
     */
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("ExtentReports flushed successfully.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ExtentTest thread management
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a new test node for the current thread.
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getExtentReports().createTest(testName, description);
        extentTestThreadLocal.set(test);
        return test;
    }

    /**
     * Creates a new test node with only a name.
     */
    public static ExtentTest createTest(String testName) {
        return createTest(testName, "");
    }

    /**
     * Returns the ExtentTest for the current thread.
     */
    public static ExtentTest getTest() {
        ExtentTest test = extentTestThreadLocal.get();
        if (test == null) {
            log.warn("ExtentTest not initialized for thread: {}. Creating a default.",
                    Thread.currentThread().getName());
            return createTest("Unknown Test - " + Thread.currentThread().getName());
        }
        return test;
    }

    /**
     * Removes ExtentTest from ThreadLocal to prevent memory leaks.
     */
    public static void removeTest() {
        extentTestThreadLocal.remove();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private setup
    // ─────────────────────────────────────────────────────────────────────────

    private static ExtentReports createExtentReports() {
        var config = ConfigManager.getConfig();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportDir = config.reportDir();
        String reportPath = reportDir + File.separator + "ExtentReport_" + timestamp + ".html";

        // Ensure report directory exists
        new File(reportDir).mkdirs();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Hybrid Automation Framework - Test Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");
        sparkReporter.config().setEncoding("UTF-8");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(sparkReporter);
        reports.setSystemInfo("Environment", config.env().toUpperCase());
        reports.setSystemInfo("Browser",     config.browser());
        reports.setSystemInfo("OS",          System.getProperty("os.name"));
        reports.setSystemInfo("Java Version", System.getProperty("java.version"));
        reports.setSystemInfo("Framework",   "Hybrid BDD - Selenium + RestAssured");
        reports.setSystemInfo("Author",      "Udhay");

        log.info("ExtentReports initialized. Report path: {}", reportPath);
        return reports;
    }
}
