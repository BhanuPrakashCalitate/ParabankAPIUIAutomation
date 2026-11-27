package com.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("input[type='submit'][value='Log In']");

    public LoginPage(WebDriver driver){
        if (driver == null){
            throw new IllegalArgumentException("Webdriver cannot be null");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    public boolean isPageLoaded(){
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public LoginPage open(String uiBaseUrl){
        String normalizedUrl = uiBaseUrl.endsWith("/")
                ? uiBaseUrl.substring(0, uiBaseUrl.length()-1)
                : uiBaseUrl;
        driver.get(normalizedUrl + "/index.htm");
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        return this;
    }

    public void enterUsername(String username) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        element.clear();
        if (username != null){
            element.sendKeys(username);
        }
    }

    public void enterPassword(String password){
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.clear();
        if (password != null){
            element.sendKeys(password);
        }
    }

    public void clickLogin(){
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    /*
     * Performs a full login flow and returns this LoginPage instance
     * (or a dedicated AccountsOverviewPage once you create it).
     */
    public AccountsOverviewPage redirectToAccountsSectionPage(String uiBaseUrl, String username, String password) {
        open(uiBaseUrl);
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new AccountsOverviewPage(driver);
    }
}
