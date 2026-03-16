# ============================================================
# Feature: SauceDemo Shopping Cart
# Application: https://www.saucedemo.com
# Tags: @UI @Cart @Regression
# ============================================================

@UI
Feature: SauceDemo Shopping Cart
  As a logged-in user
  I want to add products to my cart and complete checkout
  So that I can place an order

  Background:
    Given the user is logged into SauceDemo

  @Smoke @Cart
  Scenario: User adds a product to the cart
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge count should be 1
    And the cart should contain "Sauce Labs Backpack"

  @Regression @Cart
  Scenario: User completes end-to-end checkout
    When the user adds "Sauce Labs Backpack" to the cart
    And the user navigates to the cart
    And the user proceeds to checkout
    And the user fills checkout info with first name "John" last name "Doe" postal code "10001"
    And the user completes the order
    Then the order completion message should be "Thank you for your order!"

  @Regression @Cart
  Scenario: User verifies inventory page has products
    Then the inventory page should display at least 1 product
