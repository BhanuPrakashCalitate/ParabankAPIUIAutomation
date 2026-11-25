package com.TestClasses.UI;

import com.Base.BaseUITest;
import com.Pages.AccountsOverviewPage;
import com.Pages.LoginPage;
import com.relevantcodes.extentreports.LogStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class AcountsOverviewPageTest extends BaseUITest {

    private LoginPage loginPage;
    private AccountsOverviewPage accountsOverviewPage;

    @BeforeMethod
    public void initialiseClasses(){
        loginPage = new LoginPage(getDriver());
        accountsOverviewPage = new AccountsOverviewPage(getDriver());
    }

    @Test(priority = 1, description = "Verify successful loading of accounts overview page")
    @Parameters({"username", "password"})
    public void verifyAccountsPageLoad(String username, String password){
        accountsOverviewPage = loginPage.redirectToAccountsSectionPage(setUIUrl(System.getenv("ENV")), username, password);
        softAssert.assertTrue(accountsOverviewPage.isPageLoaded());
        softAssert.assertEquals(accountsOverviewPage.getPageTitle(), "Accounts Overview");
        log(LogStatus.INFO, "Page title: " + accountsOverviewPage.getPageTitle());
    }
}
