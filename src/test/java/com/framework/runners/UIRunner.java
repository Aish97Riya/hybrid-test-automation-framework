package com.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * UIRunner - Runs only @UI tagged scenarios.
 * Spins up browser for each scenario via CucumberHooks.
 */
@CucumberOptions(
        features = "src/test/resources/features/ui",
        glue = {
            "com.framework.ui.tests",
            "com.framework.utils"
        },
        tags = "@UI",
        plugin = {
            "pretty",
            "html:test-output/cucumber-reports/ui-report.html",
            "json:test-output/cucumber-reports/ui.json",
            "junit:test-output/cucumber-reports/ui-junit.xml",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class UIRunner extends AbstractTestNGCucumberTests {
}
