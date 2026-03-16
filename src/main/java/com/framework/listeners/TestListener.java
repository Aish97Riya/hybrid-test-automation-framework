package com.framework.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.framework.config.ConfigManager;
import com.framework.core.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Base64;

/**
 * TestListener - TestNG listener that bridges test lifecycle events to ExtentReports.
 *
 * <p>SOLID: Single Responsibility - only handles test lifecycle → reporting mapping.</p>
 * <p>SOLID: Open/Closed - add new event handlers without modifying existing ones.</p>
 *
 * <p>Configured in testng.xml via &lt;listeners&gt; element.</p>
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("======= Test Suite STARTED: [{}] =======", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("======= Test Suite FINISHED: [{}] | Passed={} | Failed={} | Skipped={} =======",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
        ExtentReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);
        String description = result.getMethod().getDescription();
        log.info("─── TEST STARTED: [{}] ───", testName);
        ExtentReportManager.createTest(testName,
                description.isBlank() ? testName : description);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);
        log.info("✔ TEST PASSED: [{}] ({}ms)", testName, getDurationMs(result));
        ExtentReportManager.getTest()
                .log(Status.PASS, "Test PASSED ✔");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        Throwable cause = result.getThrowable();
        log.error("✘ TEST FAILED: [{}] | Cause: {}", testName, cause != null ? cause.getMessage() : "Unknown");

        var test = ExtentReportManager.getTest();
        test.log(Status.FAIL, "Test FAILED ✘");
        if (cause != null) {
            test.log(Status.FAIL, cause);
        }

        // Attach screenshot if UI driver is active
        if (ConfigManager.getConfig().screenshotOnFailure() && DriverManager.isDriverActive()) {
            try {
                String base64Screenshot = ((TakesScreenshot) DriverManager.getDriver())
                        .getScreenshotAs(OutputType.BASE64);
                test.fail("Screenshot on failure:",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                log.info("Screenshot attached to report for: {}", testName);
            } catch (Exception e) {
                log.warn("Could not capture screenshot: {}", e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);
        log.warn("⚠ TEST SKIPPED: [{}]", testName);
        ExtentReportManager.getTest()
                .log(Status.SKIP, "Test SKIPPED ⚠ " +
                        (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("Test failed within success percentage: {}", getTestName(result));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String getTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + " :: " + result.getMethod().getMethodName();
    }

    private long getDurationMs(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
