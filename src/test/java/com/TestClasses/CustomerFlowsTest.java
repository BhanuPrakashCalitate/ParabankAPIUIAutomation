package com.TestClasses;

import com.Base.BaseTest;
import com.Endpoints;
import com.Services.CustomersController;
import com.Utils.CommonUtils.ConfigManager;
import com.relevantcodes.extentreports.LogStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CustomerFlowsTest extends BaseTest {

    CustomersController customersController = new CustomersController();

    @BeforeClass
    public void setup(){
        customersController.setBaseUrl();
    }

    @Test(priority = 1, description = "Fetch and validate customer details")
    public void fetchCustomerDetails(){
        log(LogStatus.INFO, "Fetching valid customer details");
        response = customersController.getCustomerDetails(Endpoints.FETCH_CUSTOMER_DETAILS, ConfigManager.getIntProperty("customer.id"));
        log(LogStatus.INFO, "Fetched valid customer details: " + response.asString());
        commonValidator.validateStatusCode(response, 200);
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "Invalid customer details")
    public void fetchInvalidCustomerDetails(){
        log(LogStatus.INFO, "Fetching invalid customer details");
        response = customersController.getCustomerDetails(Endpoints.FETCH_CUSTOMER_DETAILS, 455362);
        log(LogStatus.INFO, "Fetched invalid customer details: " + response.asString());
        commonValidator.validateStatusCode(response, 400);
        softAssert.assertAll();
    }
}
