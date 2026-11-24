package com.Validators;

import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;

public class CommonValidator {

    private final SoftAssert softAssert;

    public CommonValidator(SoftAssert softAssert){
        this.softAssert = softAssert;
    }

    public void validateStatusCode(Response response, int statusCode){
        softAssert.assertEquals(response.statusCode(), statusCode, "Expected status code: " + statusCode + " but got: " + response.statusCode());
    }
}
