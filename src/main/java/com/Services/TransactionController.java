package com.Services;

import com.Base.BaseSetup;
import com.Utils.APIUtils.HTTPMethodUtils;
import com.Utils.APIUtils.RequestSpecificationBuilder;
import io.restassured.response.Response;

import java.math.BigDecimal;

public class TransactionController extends HTTPMethodUtils {

    RequestSpecificationBuilder requestSpecificationBuilder = new RequestSpecificationBuilder();
    String baseUrl;

    public void setBaseUrl(){
        baseUrl = BaseSetup.setApiBaseUrl(System.getenv("ENV").toLowerCase().trim());
    }

    public Response transferFunds(String endpoint, int fromAccountId, int toAccountId, BigDecimal amount){
        requestSpecificationBuilder.setHeaders("Accept", "application/json");
        requestSpecificationBuilder.setQueryParams("fromAccountId", fromAccountId);
        requestSpecificationBuilder.setQueryParams("toAccountId", toAccountId);
        requestSpecificationBuilder.setQueryParams("amount", amount);

        return postRequestWithoutPayload(baseUrl+endpoint, requestSpecificationBuilder);
    }

    public Response transactionsDetails(String endpoint, int accountId){
        requestSpecificationBuilder.setHeaders("Accept", "application/json");
        requestSpecificationBuilder.setPathParams("accountId", accountId);

        return getRequestWithBuilder(baseUrl+endpoint, requestSpecificationBuilder);
    }
}
