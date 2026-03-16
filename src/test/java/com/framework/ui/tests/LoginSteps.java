package com.framework.ui.tests;

import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.framework.utils.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginSteps - Step definitions for SauceDemo Login feature.
 *
 * <p>Design: Dependency Injection via constructor (PicoContainer).</p>
 * <p>SOLID: Single Responsibility - only handles login-related steps.</p>
 */
public class LoginSteps {

    private static final Logger log = LogManager.getLogger(LoginSteps.class);

    // Dependency Injection - PicoContainer injects shared ScenarioContext
    private final ScenarioContext context;

    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the user is on the SauceDemo login page")
    public void theUserIsOnTheSauceDemoLoginPage() {
        loginPage = new LoginPage();
        loginPage.navigateToLoginPage();
        assertThat(loginPage.isLoginPageDisplayed())
                .as("Login page should be displayed")
                .isTrue();
        log.info("User is on the SauceDemo login page");
    }

    @When("the user enters username {string} and password {string}")
    public void theUserEntersCredentials(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        log.info("Entered credentials: username=[{}]", username);
    }

    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        // Store current page ref to check outcome after click
        context.set("loginPage", loginPage);
        log.info("Clicking login button");
        // Note: click handled in Then steps to determine navigation vs error
    }

    @Then("the user should be redirected to the inventory page")
    public void theUserShouldBeRedirectedToInventoryPage() {
        inventoryPage = loginPage.clickLoginButton();
        assertThat(inventoryPage.isOnInventoryPage())
                .as("User should be redirected to inventory page")
                .isTrue();
        context.set(ScenarioContext.Keys.PRODUCT_NAME, "redirected");
        log.info("User successfully redirected to inventory page");
    }

    @And("the inventory page title should be {string}")
    public void theInventoryPageTitleShouldBe(String expectedTitle) {
        assertThat(inventoryPage.getPageTitle())
                .as("Inventory page title")
                .isEqualTo(expectedTitle);
        log.info("Inventory page title verified: [{}]", expectedTitle);
    }

    @Then("an error message should be displayed")
    public void anErrorMessageShouldBeDisplayed() {
        // Click login first to trigger error
        loginPage.clickLoginButton();
        assertThat(loginPage.isErrorDisplayed())
                .as("Error message should be visible")
                .isTrue();
        log.info("Error message is displayed as expected");
    }

    @And("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedMessage) {
        assertThat(loginPage.getErrorMessage())
                .as("Error message content")
                .contains(expectedMessage);
        log.info("Error message contains: [{}]", expectedMessage);
    }

    @Then("the expected result should be {string}")
    public void theExpectedResultShouldBe(String expectedResult) {
        if ("success".equalsIgnoreCase(expectedResult)) {
            inventoryPage = loginPage.clickLoginButton();
            assertThat(inventoryPage.isOnInventoryPage())
                    .as("Login should succeed and redirect to inventory")
                    .isTrue();
            log.info("Login succeeded as expected");
        } else {
            loginPage.clickLoginButton();
            assertThat(loginPage.isErrorDisplayed())
                    .as("Login should fail and show error")
                    .isTrue();
            log.info("Login failed as expected");
        }
    }
}
