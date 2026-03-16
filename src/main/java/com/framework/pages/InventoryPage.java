package com.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * InventoryPage - Page Object for SauceDemo product inventory page.
 *
 * <p>SOLID: Single Responsibility - only handles inventory/products page interactions.</p>
 */
public class InventoryPage extends BasePage {

    public static final String INVENTORY_URL_FRAGMENT = "inventory.html";

    // ─────────────────────────────────────────────────────────────────────────
    // Page Elements
    // ─────────────────────────────────────────────────────────────────────────

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;

    @FindBy(css = "[data-test='add-to-cart-sauce-labs-backpack']")
    private WebElement addBackpackToCartBtn;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement hamburgerMenu;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    @FindBy(css = "[data-test='product_sort_container']")
    private WebElement sortDropdown;

    // ─────────────────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the page header title text.
     */
    public String getPageTitle() {
        return getText(pageTitle);
    }

    /**
     * Returns true if currently on the inventory page.
     */
    public boolean isOnInventoryPage() {
        return getCurrentUrl().contains(INVENTORY_URL_FRAGMENT);
    }

    /**
     * Returns the count of displayed inventory items.
     */
    public int getInventoryItemCount() {
        return inventoryItems.size();
    }

    /**
     * Returns list of all product names.
     */
    public List<String> getProductNames() {
        return productNames.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Adds the Backpack to cart.
     */
    public InventoryPage addBackpackToCart() {
        log.info("Adding Backpack to cart");
        click(addBackpackToCartBtn);
        return this;
    }

    /**
     * Returns the cart badge count as integer.
     */
    public int getCartCount() {
        try {
            return Integer.parseInt(getText(cartBadge));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Navigates to the shopping cart page.
     */
    public CartPage goToCart() {
        log.info("Navigating to cart");
        click(cartIcon);
        return new CartPage();
    }

    /**
     * Logs out of the application.
     */
    public LoginPage logout() {
        log.info("Logging out");
        click(hamburgerMenu);
        click(logoutLink);
        return new LoginPage();
    }
}
