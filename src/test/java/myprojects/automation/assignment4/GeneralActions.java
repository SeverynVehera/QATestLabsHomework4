package myprojects.automation.assignment4;


import myprojects.automation.assignment4.model.ProductData;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private WebDriverWait wait;

    public GeneralActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 10);
    }

    private class ActualProduct{
        private String name;
        private String qty;
        private String price;

        public ActualProduct() {
            this.name = driver.findElement(By.className("h1")).getText();
            this.qty = driver.findElement(By.cssSelector(".product-quantities span")).getText(); //.product-quantities span
            this.price = driver.findElement(By.cssSelector(".current-price span")).getAttribute("content"); //.current-price span

        }

        public String getName() {
            return name;
        }

        public Integer getQty() {
            String result = new String();
            Matcher matcher = Pattern.compile("\\d+").matcher(qty);
            if (matcher.find()) {
                result = qty.substring(matcher.start(), matcher.end());
            }
            return Integer.parseUnsignedInt(result);
        }

        public String getPrice() {
            return price.replace(".",",");
        }
    }
    private ActualProduct actualProduct;

    public String getActualProductName(){
        return actualProduct.getName();
    }

    public Integer getActualProductQty(){
        return actualProduct.getQty();
    }

    public String getActualProductPrice(){
        return actualProduct.getPrice();
    }

    /**
     * Logs in to Admin Panel.
     * @param login
     * @param password
     */
    public void login(String login, String password) {
        initLoginPageWebElements();

        setLoginFieldText(login);

        setPasswordFieldText(password);

        getButtonLogin().click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("employee_avatar_small")));
    }

    public void createProduct(ProductData newProduct)  {

        clickCatalogSubItemProducts();

        waitForContentLoad();

        getAddNewProductButton().click();

        waitForContentLoad();

        setNewProductNameFieldText(newProduct.getName());
        setNewProductQuantityFieldText(newProduct.getQty().toString());

        wait.until(ExpectedConditions.elementToBeClickable(getNewProductPriceField()));
        setNewProductPriceFieldText(newProduct.getPrice());

        clickActivateNewProductToggle();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("growls")));
        getSettingsChangedNotificationClose();

        getSaveNewProductButton().click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("growls")));
        getSettingsChangedNotificationClose();

    }

    public void goToAllProducts()  {
        getAllProductLink().click();
        waitForContentLoad();
    }

    private List<WebElement> getAllProducts()  {
        return new ArrayList<>(driver.findElements(By.className("product-miniature")));
    }

    private String getProductNameByWebElement(WebElement webElement)  {
        return webElement.findElement(By.className("product-title")).getText();
    }

    public WebElement getProductElementByProductsNames(String productName)  {
        WebElement result = null;

            for (WebElement current :getAllProducts()) {
                if (getProductNameByWebElement(current).equals(productName)) {
                    result = current;
                    break;
                }
            }

        if (result==null){
            throw new NoSuchElementException(productName);
        }

        return result;
    }

    public void clickProductByProductsNames(String productName)  {
        getProductElementByProductsNames(productName).findElement(By.className("product-title")).click();
        waitForContentLoad();
        actualProduct = new ActualProduct();
    }

    /**
     * Waits until page loader disappears from the page
     */
    private void waitForContentLoad() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("div")));
        // ...
    }

    private void scrollToWebElement(WebElement webElement) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor)driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);",webElement);
//        javascriptExecutor.executeScript("$(\"#form_step1_price_shortcut\").animate({ scrollTop: \"100px\" })");
    }

    // *** ATOMIC ACTIONS ***

    // ***Login Field***
    private  WebElement getLoginField(){
        return driver.findElement(By.name("email"));
    }
    private void setLoginFieldText(String text){
        getLoginField().click();
        getLoginField().sendKeys(text);
    }

    // ***Password Field***
    private WebElement getPasswordField(){
        return driver.findElement(By.name("passwd"));
    }

    private void setPasswordFieldText(String text){
        getPasswordField().click();
        getPasswordField().sendKeys(text);
    }

    // ***Login Button***
    private WebElement getButtonLogin(){
        return driver.findElement(By.name("submitLogin"));
    }

    // *** Initialize Login Page Web Elements ***
    private void initLoginPageWebElements(){
        getLoginField();
        getPasswordField();
        getButtonLogin();
    }


    // *** Catalog Menu Item  ***
    private WebElement getMenuItemCatalog(){
        return driver.findElement(By.id("subtab-AdminCatalog"));
    }

    // *** Catalog Sub Item Products ***
    private WebElement getCatalogSubItemProducts(){
        return driver.findElement(By.id("subtab-AdminProducts"));
    }

    // *** Click products ***
    private void clickCatalogSubItemProducts(){
        Actions action = new Actions(driver);
        action.
                moveToElement(getMenuItemCatalog()).
                build().perform();

        wait.until(ExpectedConditions.elementToBeClickable(getCatalogSubItemProducts()));
        action.
                moveToElement(getCatalogSubItemProducts()).
                click().
                build().perform();

    }

    // *** Add New Product Button***
    private WebElement getAddNewProductButton(){
        return driver.findElement(By.id("page-header-desc-configuration-add"));
    }

    // *** New Product Name Input Field ***
    private WebElement getNewProductNameField(){
        return driver.findElement(By.id("form_step1_name_1"));
    }

    private void setNewProductNameFieldText(String text){
        getNewProductNameField().click();
        getNewProductNameField().sendKeys(text);
    }

    // *** New Product Quantity Input Field ***
    private WebElement getNewProductQuantityField(){
        return driver.findElement(By.id("form_step1_qty_0_shortcut"));
    }

    private void setNewProductQuantityFieldText(String text){
        getNewProductQuantityField().click();
        getNewProductQuantityField().clear();
        getNewProductQuantityField().sendKeys(text);
    }

    // *** New Product Price Input Field ***
    private WebElement getNewProductPriceField(){
        return driver.findElement(By.id("form_step1_price_shortcut"));
    }

    private void setNewProductPriceFieldText(String text){
        Actions action = new Actions(driver);
        scrollToWebElement(getNewProductPriceField());
        wait.until(ExpectedConditions.elementToBeClickable(getNewProductPriceField()));
        action.moveToElement(getNewProductPriceField()).click().build().perform();
        getNewProductPriceField().clear();
        getNewProductPriceField().sendKeys(text);
    }


    // *** Activate New Product Toggle***
    private WebElement getActivateNewProductToggle(){
        return driver.findElement(By.className("switch-input"));
    }

    private void clickActivateNewProductToggle(){
        Actions actions = new Actions(driver);
        actions.moveToElement(getActivateNewProductToggle()).click().build().perform();
    }

    // *** Settings Changed Notification***
    private WebElement getSettingsChangedNotification(){
        return driver.findElement(By.id("growls"));
    }

    private void getSettingsChangedNotificationClose(){
        getSettingsChangedNotification().findElement(By.className("growl-close")).click();
    }

    // *** Save New Product Button***
    private WebElement getSaveNewProductButton(){
        return driver.findElement(By.cssSelector(".btn.btn-primary.js-btn-save"));
    }

    // *** Sort Categories By Name ASC ***
    private WebElement getAllProductLink(){
        return driver.findElement(By.className("all-product-link"));
    }















}
