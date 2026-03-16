package com.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * RegressionRunner - Runs full @Regression suite including all UI and API tests.
 * Designed for nightly builds and release validation.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
            "com.framework.ui.tests",
            "com.framework.api.tests",
            "com.framework.utils"
        },
        tags = "@Regression",
        plugin = {
            "pretty",
            "html:test-output/cucumber-reports/regression-report.html",
            "json:test-output/cucumber-reports/regression.json",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class RegressionRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
