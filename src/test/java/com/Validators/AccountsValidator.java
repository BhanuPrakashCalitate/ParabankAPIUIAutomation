package com.Validators;

import com.Utils.CommonUtils.AccountType;
import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;

public class AccountsValidator {

    public void validateNewAccountDetails(Response response, SoftAssert softAssert, int expectedCustomerId,
                                          int expectedAccountTypeCode){
        softAssert.assertTrue(response.getBody().jsonPath().getInt("id") != 0, "Id should be non zero number");
        softAssert.assertEquals(response.getBody().jsonPath().getInt("customerId"), expectedCustomerId, "Invalid customerId");

        String expectedAccountTypeName = AccountType.fromCode(expectedAccountTypeCode).name();
        softAssert.assertEquals(response.getBody().jsonPath().getString("type"), expectedAccountTypeName, "Invalid accountType");

    }
}
