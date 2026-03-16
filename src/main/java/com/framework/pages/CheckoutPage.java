package com.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CheckoutPage - Page Object for SauceDemo checkout flow.
 */
public class CheckoutPage extends BasePage {

    @FindBy(id = "first-name")
    private WebElement firstNameInput;

    @FindBy(id = "last-name")
    private WebElement lastNameInput;

    @FindBy(id = "postal-code")
    private WebElement postalCodeInput;

    @FindBy(css = "[data-test='continue']")
    private WebElement continueButton;

    @FindBy(css = "[data-test='finish']")
    private WebElement finishButton;

    @FindBy(css = ".complete-header")
    private WebElement orderCompleteHeader;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    /**
     * Fills the checkout information form.
     */
    public CheckoutPage fillCheckoutInfo(String firstName, String lastName, String postalCode) {
        log.info("Filling checkout info: {} {} {}", firstName, lastName, postalCode);
        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(postalCodeInput, postalCode);
        return this;
    }

    /**
     * Clicks the Continue button.
     */
    public CheckoutPage clickContinue() {
        click(continueButton);
        return this;
    }

    /**
     * Clicks the Finish button to complete the order.
     */
    public CheckoutPage clickFinish() {
        click(finishButton);
        return this;
    }

    /**
     * Returns the order complete header text.
     */
    public String getOrderCompleteHeader() {
        return getText(orderCompleteHeader);
    }

    /**
     * Returns true if the order complete message is displayed.
     */
    public boolean isOrderComplete() {
        return isDisplayed(orderCompleteHeader);
    }

    /**
     * Returns the error message text.
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
