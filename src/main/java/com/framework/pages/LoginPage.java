package com.framework.pages;

import com.framework.config.ConfigManager;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage - Page Object for SauceDemo login screen.
 *
 * <p>SOLID: Single Responsibility - only handles login page interactions.</p>
 * <p>Design: Page Object Model - all locators and actions encapsulated here.</p>
 */
public class LoginPage extends BasePage {

    // ─────────────────────────────────────────────────────────────────────────
    // Page Elements (POM)
    // ─────────────────────────────────────────────────────────────────────────

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @FindBy(css = ".login_logo")
    private WebElement loginLogo;

    // ─────────────────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Navigates to the SauceDemo login page.
     */
    public LoginPage navigateToLoginPage() {
        log.info("Navigating to SauceDemo login page");
        navigateTo(ConfigManager.getConfig().uiBaseUrl());
        return this;
    }

    /**
     * Enters the username.
     */
    public LoginPage enterUsername(String username) {
        log.info("Entering username: {}", username);
        type(usernameInput, username);
        return this;
    }

    /**
     * Enters the password.
     */
    public LoginPage enterPassword(String password) {
        log.info("Entering password");
        type(passwordInput, password);
        return this;
    }

    /**
     * Clicks the login button.
     */
    public InventoryPage clickLoginButton() {
        log.info("Clicking login button");
        click(loginButton);
        return new InventoryPage();
    }

    /**
     * Performs end-to-end login using credentials from config.
     */
    public InventoryPage loginWithDefaultCredentials() {
        var config = ConfigManager.getConfig();
        return navigateToLoginPage()
                .enterUsername(config.uiUsername())
                .enterPassword(config.uiPassword())
                .clickLoginButton();
    }

    /**
     * Performs login with custom credentials.
     */
    public InventoryPage login(String username, String password) {
        return navigateToLoginPage()
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();
    }

    /**
     * Attempts login and expects failure (returns LoginPage for error validation).
     */
    public LoginPage loginExpectingFailure(String username, String password) {
        navigateToLoginPage()
                .enterUsername(username)
                .enterPassword(password);
        click(loginButton);
        return this;
    }

    /**
     * Returns error message text.
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * Returns true if the error message container is visible.
     */
    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Returns true if the login page logo is visible.
     */
    public boolean isLoginPageDisplayed() {
        return isDisplayed(loginLogo);
    }
}
