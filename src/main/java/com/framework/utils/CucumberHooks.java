package com.framework.utils;

import com.aventstack.extentreports.Status;
import com.framework.core.driver.DriverManager;
import com.framework.listeners.ExtentReportManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * CucumberHooks - Cucumber lifecycle hooks for WebDriver management and reporting.
 *
 * <p>SOLID: Single Responsibility - only manages test setup/teardown lifecycle.</p>
 *
 * <p>@Before / @After are Cucumber hooks (not TestNG).
 * Driver init happens here for UI scenarios tagged with @UI.</p>
 */
public class CucumberHooks {

    private static final Logger log = LogManager.getLogger(CucumberHooks.class);

    /**
     * Before each UI scenario: initialize WebDriver.
     * Tagged @UI scenarios trigger browser launch.
     */
    @Before(value = "@UI", order = 1)
    public void initDriverForUIScenario(Scenario scenario) {
        log.info("═══ SCENARIO STARTED [UI]: {} ═══", scenario.getName());
        DriverManager.initDriver();
        ExtentReportManager.createTest(scenario.getName(), "UI Scenario: " + scenario.getId());
    }

    /**
     * Before each API scenario: just setup reporting (no driver needed).
     */
    @Before(value = "@API", order = 1)
    public void initReportForAPIScenario(Scenario scenario) {
        log.info("═══ SCENARIO STARTED [API]: {} ═══", scenario.getName());
        ExtentReportManager.createTest(scenario.getName(), "API Scenario: " + scenario.getId());
    }

    /**
     * After each step: logs step result to ExtentReport.
     */
    @AfterStep
    public void afterEachStep(Scenario scenario) {
        if (scenario.isFailed()) {
            ExtentReportManager.getTest().log(Status.FAIL, "Step FAILED in scenario: " + scenario.getName());
        }
    }

    /**
     * After each UI scenario: capture screenshot on failure, quit WebDriver.
     */
    @After(value = "@UI", order = 1)
    public void tearDownUIScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            log.warn("Scenario FAILED: [{}] - capturing screenshot", scenario.getName());
            captureAndAttachScreenshot(scenario);
            ExtentReportManager.getTest().log(Status.FAIL, "Scenario FAILED: " + scenario.getName());
        } else {
            ExtentReportManager.getTest().log(Status.PASS, "Scenario PASSED ✔");
            log.info("Scenario PASSED: [{}]", scenario.getName());
        }
        DriverManager.quitDriver();
        ExtentReportManager.removeTest();
        log.info("═══ SCENARIO ENDED [UI]: {} ═══", scenario.getName());
    }

    /**
     * After each API scenario: log final result.
     */
    @After(value = "@API", order = 1)
    public void tearDownAPIScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            ExtentReportManager.getTest().log(Status.FAIL, "API Scenario FAILED: " + scenario.getName());
            log.warn("API Scenario FAILED: [{}]", scenario.getName());
        } else {
            ExtentReportManager.getTest().log(Status.PASS, "API Scenario PASSED ✔");
            log.info("API Scenario PASSED: [{}]", scenario.getName());
        }
        ExtentReportManager.removeTest();
        log.info("═══ SCENARIO ENDED [API]: {} ═══", scenario.getName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void captureAndAttachScreenshot(Scenario scenario) {
        try {
            if (DriverManager.isDriverActive()) {
                byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", scenario.getName() + "_failure");
                log.info("Screenshot attached to Cucumber report for: {}", scenario.getName());
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot for scenario [{}]: {}",
                    scenario.getName(), e.getMessage());
        }
    }
}
