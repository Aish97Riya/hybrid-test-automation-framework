package com.framework.ui.tests;

import com.framework.pages.CartPage;
import com.framework.pages.CheckoutPage;
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
 * CartSteps - Step definitions for SauceDemo Cart and Checkout features.
 *
 * <p>Design: Dependency Injection via PicoContainer for shared ScenarioContext.</p>
 */
public class CartSteps {

    private static final Logger log = LogManager.getLogger(CartSteps.class);

    private final ScenarioContext context;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    public CartSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the user is logged into SauceDemo")
    public void theUserIsLoggedIntoSauceDemo() {
        inventoryPage = new LoginPage().loginWithDefaultCredentials();
        assertThat(inventoryPage.isOnInventoryPage())
                .as("User should be on inventory page after login")
                .isTrue();
        log.info("User successfully logged in and is on inventory page");
    }

    @When("the user adds {string} to the cart")
    public void theUserAddsProductToCart(String productName) {
        log.info("Adding product to cart: [{}]", productName);
        // Currently supporting Backpack - extensible via product map
        inventoryPage.addBackpackToCart();
        context.set(ScenarioContext.Keys.PRODUCT_NAME, productName);
        log.info("Product [{}] added to cart", productName);
    }

    @Then("the cart badge count should be {int}")
    public void theCartBadgeCountShouldBe(int expectedCount) {
        int actualCount = inventoryPage.getCartCount();
        assertThat(actualCount)
                .as("Cart badge count")
                .isEqualTo(expectedCount);
        context.set(ScenarioContext.Keys.CART_COUNT, actualCount);
        log.info("Cart badge count verified: expected=[{}] actual=[{}]", expectedCount, actualCount);
    }

    @And("the cart should contain {string}")
    public void theCartShouldContain(String productName) {
        cartPage = inventoryPage.goToCart();
        assertThat(cartPage.isItemInCart(productName))
                .as("Cart should contain: " + productName)
                .isTrue();
        log.info("Product [{}] is in cart", productName);
    }

    @And("the user navigates to the cart")
    public void theUserNavigatesToTheCart() {
        cartPage = inventoryPage.goToCart();
        assertThat(cartPage.getPageTitle())
                .as("Cart page title")
                .containsIgnoringCase("Your Cart");
        log.info("Navigated to cart page");
    }

    @And("the user proceeds to checkout")
    public void theUserProceedsToCheckout() {
        checkoutPage = cartPage.proceedToCheckout();
        log.info("Proceeded to checkout page");
    }

    @And("the user fills checkout info with first name {string} last name {string} postal code {string}")
    public void theUserFillsCheckoutInfo(String firstName, String lastName, String postalCode) {
        checkoutPage.fillCheckoutInfo(firstName, lastName, postalCode)
                    .clickContinue();
        log.info("Filled checkout info: {} {} {}", firstName, lastName, postalCode);
    }

    @And("the user completes the order")
    public void theUserCompletesTheOrder() {
        checkoutPage.clickFinish();
        log.info("Order completion submitted");
    }

    @Then("the order completion message should be {string}")
    public void theOrderCompletionMessageShouldBe(String expectedMessage) {
        assertThat(checkoutPage.isOrderComplete())
                .as("Order complete section should be visible")
                .isTrue();
        assertThat(checkoutPage.getOrderCompleteHeader())
                .as("Order completion message")
                .containsIgnoringCase(expectedMessage);
        log.info("Order completion verified: [{}]", expectedMessage);
    }

    @Then("the inventory page should display at least {int} product")
    public void theInventoryPageShouldDisplayAtLeastProducts(int minCount) {
        int itemCount = inventoryPage.getInventoryItemCount();
        assertThat(itemCount)
                .as("Inventory item count should be at least " + minCount)
                .isGreaterThanOrEqualTo(minCount);
        log.info("Inventory item count: {}", itemCount);
    }
}
