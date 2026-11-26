package com.TestClasses;

import com.Base.BaseTest;
import com.Endpoints;
import com.Pages.AccountsOverviewPage;
import com.Pages.LoginPage;
import com.Services.AccountsController;
import com.Utils.CommonUtils.ConfigManager;
import com.Utils.DriverUtils.DriverManager;
import com.Validators.AccountsValidator;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class FundsTransferE2EFlowTest extends BaseTest {

    private LoginPage loginPage;
    private AccountsOverviewPage accountsOverviewPage;
    private AccountsController accountsController;
    private AccountsValidator accountsValidator;

    int newAccountId;


    @BeforeMethod(alwaysRun = true)
    public void initialiseE2EComponents(){
        setupDriver();
        loginPage = new LoginPage(getDriver());
        accountsOverviewPage = new AccountsOverviewPage(getDriver());
        accountsController = new AccountsController();
        accountsController.setBaseUrl();
        accountsValidator = new AccountsValidator();
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(){
        DriverManager.quitDriver();
    }

    @Test(description = "Verify E2E funds transaction flows")
    @Parameters({"username", "password"})
    public void verifyE2EFundsTransferFlowTest(String username, String password){
        //Login Parasoft internet banking and verify accounts overview section is loaded successfully
        log(LogStatus.INFO, "Logging into Parasoft internet banking");
        accountsOverviewPage = loginPage.redirectToAccountsSectionPage(setUIUrl(System.getenv("ENV").toLowerCase().trim()), username, password);
        softAssert.assertTrue(accountsOverviewPage.isPageLoaded());
        softAssert.assertEquals(accountsOverviewPage.getPageTitle(), "Accounts Overview");
        log(LogStatus.INFO, "Successfully logged in and landing page title is: " + accountsOverviewPage.getPageTitle());

        /*
        Create new savings account
        accountType for CHECKING is 0 and SAVINGS is 1
         */
        log(LogStatus.INFO, "Creating new account of type SAVINGS whose accountType is 1");
        Response response = accountsController.createAccount(Endpoints.CREATE_ACCOUNT, ConfigManager.getIntProperty("customer.id"),
                1, ConfigManager.getIntProperty("source.account.id"));
        commonValidator.validateStatusCode(response, 200);
        accountsValidator.validateNewAccountDetails(response, softAssert, ConfigManager.getIntProperty("customer.id"), 1);
        log(LogStatus.INFO, "Successfully created new account: " + response.asString());
        newAccountId = response.getBody().jsonPath().getInt("id");
        log(LogStatus.INFO, "Extracted accountId from response: " + newAccountId);

        /*
        Validate newly created account is present in the accounts table in overview page
        then check its balance and available amount columns
         */
        accountsOverviewPage.refresh();
        softAssert.assertTrue(accountsOverviewPage.isNewAccountDisplayed(String.valueOf(newAccountId)),
                "Newly created accountId: " +newAccountId+ " is not present in the table");
        log(LogStatus.INFO, "Newly created account with accountId " +newAccountId+ " is present in the table");
        softAssert.assertEquals(accountsOverviewPage.getAccountBalance(String.valueOf(newAccountId)), "$100.00",
                "Mismatch in available balance or there is no balance in account");
        log(LogStatus.INFO, "Account balance in newly created accountId: " +newAccountId+ " is: " +
                accountsOverviewPage.getAccountBalance(String.valueOf(newAccountId)));
        softAssert.assertEquals(accountsOverviewPage.getAvailableAmount(String.valueOf(newAccountId)), "$100.00",
                "Mismatch in available amount or there is no amount in account");
        log(LogStatus.INFO, "Available amount in newly created accountId: " +newAccountId+ " is: " +
                accountsOverviewPage.getAvailableAmount(String.valueOf(newAccountId)));
    }
}
