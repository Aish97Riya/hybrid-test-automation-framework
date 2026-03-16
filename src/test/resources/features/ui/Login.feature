# ============================================================
# Feature: SauceDemo Login
# Application: https://www.saucedemo.com
# Author: SDET Lead
# Tags: @UI @Smoke @Regression @Login
# ============================================================

@UI
Feature: SauceDemo Login Functionality
  As a registered user
  I want to log into the SauceDemo application
  So that I can access the product inventory

  Background:
    Given the user is on the SauceDemo login page

  @Smoke @Login
  Scenario: Successful login with valid credentials
    When the user enters username "standard_user" and password "secret_sauce"
    And the user clicks the login button
    Then the user should be redirected to the inventory page
    And the inventory page title should be "Products"

  @Regression @Login
  Scenario: Login fails with invalid credentials
    When the user enters username "invalid_user" and password "wrong_password"
    And the user clicks the login button
    Then an error message should be displayed
    And the error message should contain "Username and password do not match"

  @Regression @Login
  Scenario: Login fails with empty credentials
    When the user enters username "" and password ""
    And the user clicks the login button
    Then an error message should be displayed
    And the error message should contain "Username is required"

  @Regression @Login
  Scenario: Login fails with locked out user
    When the user enters username "locked_out_user" and password "secret_sauce"
    And the user clicks the login button
    Then an error message should be displayed
    And the error message should contain "Sorry, this user has been locked out"

  @Smoke @Login
  Scenario Outline: Login with multiple user types
    When the user enters username "<username>" and password "<password>"
    And the user clicks the login button
    Then the expected result should be "<result>"

    Examples:
      | username           | password      | result  |
      | standard_user      | secret_sauce  | success |
      | problem_user       | secret_sauce  | success |
      | performance_glitch_user | secret_sauce | success |
