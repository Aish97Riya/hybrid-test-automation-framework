package com.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CartPage - Page Object for SauceDemo shopping cart.
 */
public class CartPage extends BasePage {

    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> cartItemNames;

    @FindBy(css = "[data-test='checkout']")
    private WebElement checkoutButton;

    @FindBy(css = "[data-test='continue-shopping']")
    private WebElement continueShoppingButton;

    @FindBy(css = ".title")
    private WebElement pageTitle;

    /**
     * Returns count of items in cart.
     */
    public int getCartItemCount() {
        return cartItems.size();
    }

    /**
     * Returns list of cart item names.
     */
    public List<String> getCartItemNames() {
        return cartItemNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns page title text.
     */
    public String getPageTitle() {
        return getText(pageTitle);
    }

    /**
     * Checks if a specific item is in the cart.
     */
    public boolean isItemInCart(String itemName) {
        return getCartItemNames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(itemName));
    }

    /**
     * Proceeds to checkout.
     */
    public CheckoutPage proceedToCheckout() {
        log.info("Proceeding to checkout");
        click(checkoutButton);
        return new CheckoutPage();
    }

    /**
     * Returns to inventory page.
     */
    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        return new InventoryPage();
    }
}
