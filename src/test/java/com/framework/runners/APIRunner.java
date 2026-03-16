package com.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * APIRunner - Runs only @API tagged scenarios.
 * No browser required; fastest feedback for API contracts.
 */
@CucumberOptions(
        features = "src/test/resources/features/api",
        glue = {
            "com.framework.api.tests",
            "com.framework.utils"
        },
        tags = "@API",
        plugin = {
            "pretty",
            "html:test-output/cucumber-reports/api-report.html",
            "json:test-output/cucumber-reports/api.json",
            "junit:test-output/cucumber-reports/api-junit.xml",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class APIRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
