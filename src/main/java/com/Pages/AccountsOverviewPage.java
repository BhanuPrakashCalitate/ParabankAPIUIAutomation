package com.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AccountsOverviewPage {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;
    private WebDriverWait wait;

    private final By pageTitleAccountsOverview = By.xpath("//h1[@class='title'][contains(text(),'Accounts')]");
    private final By accountsTable = By.xpath("//table[@id='accountTable']");

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
}
