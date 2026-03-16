package com.framework.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestRunner - Master Cucumber TestNG runner for all features.
 *
 * <p>Runs all feature files under src/test/resources/features (UI + API).</p>
 * <p>Tags are driven by Maven property: -Dcucumber.filter.tags="@Smoke"</p>
 *
 * <p>Extend {@link AbstractTestNGCucumberTests} for TestNG + Cucumber integration.</p>
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
            "html:test-output/cucumber-reports/cucumber.html",
            "json:test-output/cucumber-reports/cucumber.json",
            "junit:test-output/cucumber-reports/cucumber.xml",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true,
        dryRun = false,
        publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables parallel execution of scenarios via TestNG DataProvider.
     * parallelScenarios = true activates parallel scenario runs.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
