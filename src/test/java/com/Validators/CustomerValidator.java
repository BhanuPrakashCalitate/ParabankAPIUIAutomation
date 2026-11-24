package com.Validators;

import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;

public class CustomerValidator {

    public void validateCustomerDetails(Response response, SoftAssert softAssert, int expectedCustomerId){
        softAssert.assertEquals(response.getBody().jsonPath().getInt("id"), expectedCustomerId);
    }
}
