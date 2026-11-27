package com.Pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class AccountsOverviewPage {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By pageTitleAccountsOverview = By.xpath("//h1[@class='title'][contains(text(),'Accounts')]");
    private final By accountsTable = By.xpath("//table[@id='accountTable']");

    //Dynamic locator for locating specific account row in accounts table
    private By accountRow(String accountId){
        System.out.println();
        return By.xpath("//table[@id='accountTable']//tr[td/a[normalize-space()='"+accountId+"']]");
    }

    //Balance column
    private By accountBalance(String accountId){
        return By.xpath("//table[@id='accountTable']//tr[td/a[normalize-space()='"+accountId+"']]/td[2]");
    }

    //Available amount column
    private By availableAmount(String accountId){
        return By.xpath("//table[@id='accountTable']//tr[td/a[normalize-space()='"+accountId+"']]/td[3]");
    }

    private final By logout = By.xpath("//a[contains(text(),'Log Out')]");

    public AccountsOverviewPage(WebDriver driver){

        if (driver == null){
            throw new IllegalArgumentException("Webdriver cannot be null");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    public boolean isPageLoaded(){
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitleAccountsOverview));
            wait.until(ExpectedConditions.visibilityOfElementLocated(accountsTable));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String getPageTitle(){
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitleAccountsOverview)).getText().trim();
    }

    public boolean isNewAccountDisplayed(String accountId){
        return wait.until(ExpectedConditions.visibilityOfElementLocated(accountRow(accountId))).isDisplayed();
    }

    public BigDecimal getAccountBalance(String accountId){
        String amount = driver.findElement(accountBalance(accountId)).getText().replace("$", "").trim();
        return new BigDecimal(amount);
    }

    public BigDecimal getAvailableAmount(String accountId){
        String amount = driver.findElement(availableAmount(accountId)).getText().replace("$", "").trim();
        return new BigDecimal(amount);
    }

    public void refresh(){
        driver.navigate().refresh();

        wait.until(ExpectedConditions.visibilityOfElementLocated(accountsTable));
    }

    public void clickLogout(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(logout)).click();
    }

    public LoginPage logout(){
        clickLogout();
        return new LoginPage(driver);
    }
}
