package com.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * SmokeRunner - Runs only @Smoke tagged scenarios for fast feedback.
 * Ideal for post-deployment sanity checks in CI pipelines.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
            "com.framework.ui.tests",
            "com.framework.api.tests",
            "com.framework.utils"
        },
        tags = "@Smoke",
        plugin = {
            "pretty",
            "html:test-output/cucumber-reports/smoke-report.html",
            "json:test-output/cucumber-reports/smoke.json",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class SmokeRunner extends AbstractTestNGCucumberTests {
}
