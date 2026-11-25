package com.Services;

import com.Base.BaseSetup;
import com.Utils.APIUtils.HTTPMethodUtils;
import com.Utils.APIUtils.RequestSpecificationBuilder;
import io.restassured.response.Response;

public class AccountsController extends HTTPMethodUtils {
    RequestSpecificationBuilder requestSpecificationBuilder = new RequestSpecificationBuilder();
    String baseUrl;

    public void setBaseUrl(){
        baseUrl = BaseSetup.setApiBaseUrl(System.getenv("ENV").toLowerCase().trim());
    }

    public Response createAccount(String endpoint, int customerId, int accountType, int sourceAccountId){
        requestSpecificationBuilder.setHeaders("Accept", "application/json");
        requestSpecificationBuilder.setQueryParams("customerId", customerId);
        requestSpecificationBuilder.setQueryParams("newAccountType", accountType);
        requestSpecificationBuilder.setQueryParams("fromAccountId", sourceAccountId);

        response = postRequestWithoutPayload(baseUrl+endpoint, requestSpecificationBuilder);

        return response;
    }
}
