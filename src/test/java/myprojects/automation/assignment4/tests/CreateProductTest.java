package myprojects.automation.assignment4.tests;

import myprojects.automation.assignment4.BaseTest;
import myprojects.automation.assignment4.GeneralActions;
import myprojects.automation.assignment4.model.ProductData;
import myprojects.automation.assignment4.utils.Properties;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreateProductTest extends BaseTest {

    @DataProvider
    public Object[][] adminCredentialsProvider() {
        return new Object[][] { { "webinar.test@gmail.com","Xcg7299bnSmMuRLp9ITw" } };
    }

    @DataProvider
    public Object[][] productDataProvider() {
        return new Object[][] { {ProductData.generate()} };
    }


    @Test(dataProvider = "adminCredentialsProvider")
    public void adminPanelLogin(String login, String password){
        driver.navigate().to(Properties.getBaseAdminUrl());
        actions.login(login,password);
        Assert.assertNotEquals(driver.getCurrentUrl(),Properties.getBaseAdminUrl());
    }

    @Test(dataProvider = "productDataProvider",dependsOnMethods = "adminPanelLogin")
    public void createNewProduct(ProductData productData)  {
        actions.createProduct(productData);
    }

    @Test(dependsOnMethods = "createNewProduct")
    public void verifyNewProduct()  {
        ProductData expectedProduct = ProductData.getLastGeneratedProduct();

        driver.navigate().to(Properties.getBaseUrl());
        actions.goToAllProducts();

        Assert.assertTrue(actions.getProductElementByProductsNames(expectedProduct.getName()).isDisplayed());

        actions.clickProductByProductsNames(expectedProduct.getName());

        Assert.assertTrue(actions.getActualProductName().equalsIgnoreCase(expectedProduct.getName()));
        Assert.assertEquals(actions.getActualProductQty(),expectedProduct.getQty());
        Assert.assertEquals(actions.getActualProductPrice(),expectedProduct.getPrice());

    }
    // TODO implement logic to check product visibility on website
}
