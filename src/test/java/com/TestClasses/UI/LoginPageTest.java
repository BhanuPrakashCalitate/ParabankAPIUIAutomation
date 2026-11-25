package com.TestClasses.UI;

import com.Base.BaseUITest;
import com.Pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseUITest {

    private LoginPage loginPage;

    @BeforeMethod
    public void initialiseClasses(){
        loginPage = new LoginPage(getDriver());
    }

    @Test(priority = 1, description = "Login with valid credentials")
    @Parameters({"username", "password"})
    public void login(String username, String password) {
        loginPage.open(setUIUrl(System.getenv("ENV")));
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }
}
