package com.TestClasses;

import com.Base.BaseTest;
import com.Endpoints;
import com.Pages.AccountsOverviewPage;
import com.Pages.LoginPage;
import com.Services.AccountsController;
import com.Services.TransactionController;
import com.Utils.CommonUtils.ConfigManager;
import com.Utils.DriverUtils.DriverManager;
import com.Validators.AccountsValidator;
import com.Validators.TransactionsValidator;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class FundsTransferE2EFlowTest extends BaseTest {

    private LoginPage loginPage;
    private AccountsOverviewPage accountsOverviewPage;
    private AccountsController accountsController;
    private AccountsValidator accountsValidator;
    private TransactionController transactionController;
    private TransactionsValidator transactionsValidator;

    int newAccountId;
    BigDecimal existingAccountBalance;
    BigDecimal newAccountBalance;


    @BeforeMethod(alwaysRun = true)
    public void initialiseE2EComponents(){
        setupDriver();
        loginPage = new LoginPage(getDriver());
        accountsOverviewPage = new AccountsOverviewPage(getDriver());
        accountsController = new AccountsController();
        accountsController.setBaseUrl();
        accountsValidator = new AccountsValidator();
        transactionController = new TransactionController();
        transactionController.setBaseUrl();
        transactionsValidator = new TransactionsValidator();
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(){
        DriverManager.quitDriver();
    }

    @Test(description = "Verify E2E funds transaction flows")
    @Parameters({"username", "password"})
    public void verifyE2EFundsTransferFlowTest(String username, String password) {
        //Login Parasoft internet banking and verify accounts overview section is loaded successfully
        log(LogStatus.INFO, "Logging into Parasoft internet banking");
        accountsOverviewPage = loginPage.redirectToAccountsSectionPage(setUIUrl(System.getenv("ENV").toLowerCase().trim()), username, password);
        softAssert.assertTrue(accountsOverviewPage.isPageLoaded());
        softAssert.assertEquals(accountsOverviewPage.getPageTitle(), "Accounts Overview");
        log(LogStatus.INFO, "Successfully logged in and landing page title is: " + accountsOverviewPage.getPageTitle());

        //Read balance amount of existing account
        existingAccountBalance = accountsOverviewPage.getAccountBalance(String.valueOf(ConfigManager.getIntProperty("existing.account.id")));
        log(LogStatus.INFO, "Existing account balance: " + existingAccountBalance);

        /*
        Create new savings account
        accountType for CHECKING is 0 and SAVINGS is 1
        Once new account is created $100.00 is credited from existing source account
         */
        log(LogStatus.INFO, "Creating new account of type SAVINGS whose accountType is 1");
        Response createAccountResponse = accountsController.createAccount(Endpoints.CREATE_ACCOUNT, ConfigManager.getIntProperty("customer.id"),
                1, ConfigManager.getIntProperty("existing.account.id"));
        commonValidator.validateStatusCode(createAccountResponse, 200);
        accountsValidator.validateNewAccountDetails(createAccountResponse, softAssert, ConfigManager.getIntProperty("customer.id"), 1);
        log(LogStatus.INFO, "Successfully created new account: " + createAccountResponse.asString());
        newAccountId = createAccountResponse.getBody().jsonPath().getInt("id");
        log(LogStatus.INFO, "Extracted accountId from response: " + newAccountId);

        /*
        Validate newly created account is present in the accounts table in overview page
        then check its balance and available amount columns
         */
        accountsOverviewPage.refresh();
        softAssert.assertTrue(accountsOverviewPage.isNewAccountDisplayed(String.valueOf(newAccountId)),
                "Newly created accountId: " +newAccountId+ " is not present in the table");
        log(LogStatus.INFO, "Newly created account with accountId " +newAccountId+ " is present in the table");
        softAssert.assertEquals(accountsOverviewPage.getAccountBalance(String.valueOf(newAccountId)), new BigDecimal("100.00"),
                "Mismatch in available balance or there is no balance in account");
        log(LogStatus.INFO, "Account balance in newly created accountId: " +newAccountId+ " is: " +
                accountsOverviewPage.getAccountBalance(String.valueOf(newAccountId)));
        softAssert.assertEquals(accountsOverviewPage.getAvailableAmount(String.valueOf(newAccountId)), new BigDecimal("100.00"),
                "Mismatch in available amount or there is no amount in account");
        log(LogStatus.INFO, "Available amount in newly created accountId: " +newAccountId+ " is: " +
                accountsOverviewPage.getAvailableAmount(String.valueOf(newAccountId)));

        //Validate existing and new accounts balance post new account creation
        newAccountBalance = accountsOverviewPage.getAccountBalance(String.valueOf(newAccountId));
        softAssert.assertEquals(existingAccountBalance.subtract(newAccountBalance),
                accountsOverviewPage.getAccountBalance(String.valueOf(ConfigManager.getIntProperty("existing.account.id"))),
                "Existing account balance is not correct");
        softAssert.assertEquals(newAccountBalance, new BigDecimal("100.00"), "New account balance is not correct");
        log(LogStatus.INFO, "Existing account balance: " + accountsOverviewPage.getAccountBalance(String.valueOf(ConfigManager.getIntProperty("existing.account.id"))) + "\nNew account balance: " + newAccountBalance);

        /*
        Funds transfer
         */
        BigDecimal amountToBeTransferred = new BigDecimal("100.01");
        log(LogStatus.INFO, "Initiating funds transfer");
        Response fundsTransferResponse = transactionController.transferFunds(Endpoints.TRANSFER_FUNDS,
                newAccountId, ConfigManager.getIntProperty("existing.account.id"), amountToBeTransferred);
        commonValidator.validateStatusCode(fundsTransferResponse, 200);
        transactionsValidator.validateFundsTransfer(fundsTransferResponse, softAssert, newAccountId,
                ConfigManager.getIntProperty("existing.account.id"), amountToBeTransferred);
        log(LogStatus.INFO, "Funds transferred successfully with response: " + fundsTransferResponse.asString());

        //Transactions details of newly created account
        log(LogStatus.INFO, "Fetching transactions details of newly created account: " + newAccountId);
        Response transactionDetailsResponse = transactionController.transactionsDetails(Endpoints.TRANSACTIONS_DETAILS,
                newAccountId);
        commonValidator.validateStatusCode(transactionDetailsResponse, 200);
        transactionsValidator.validateTransactionDetails(transactionDetailsResponse, softAssert, newAccountId, amountToBeTransferred);
        log(LogStatus.INFO, "Transaction details of accountId " + newAccountId + ": " + transactionDetailsResponse.asString());

        //Validate existing and new accounts balance post new funds transfer
        accountsOverviewPage.refresh();
        softAssert.assertEquals(accountsOverviewPage.getAccountBalance(String.valueOf(ConfigManager.getIntProperty("existing.account.id"))), existingAccountBalance.add(new BigDecimal("0.01")),
                "Existing account balance is not correct");
        softAssert.assertEquals(accountsOverviewPage.getAccountBalance(String.valueOf(newAccountId)), newAccountBalance.subtract(amountToBeTransferred),
                "New account balance is not correct");
        log(LogStatus.INFO, "Existing account balance: " + accountsOverviewPage.getAccountBalance(String.valueOf(ConfigManager.getIntProperty("existing.account.id"))) +
                "\n New account balance: " + accountsOverviewPage.getAccountBalance(String.valueOf(newAccountId)));

        //Logout
        loginPage = accountsOverviewPage.logout();
        softAssert.assertTrue(loginPage.isPageLoaded());

        softAssert.assertAll();
    }

}
