package com.Services;

import com.Base.BaseSetup;
import com.Utils.APIUtils.HTTPMethodUtils;
import com.Utils.APIUtils.RequestSpecificationBuilder;
import io.restassured.response.Response;

public class CustomersController extends HTTPMethodUtils {

    RequestSpecificationBuilder requestSpecificationBuilder = new RequestSpecificationBuilder();
    String baseUrl;

    public void setBaseUrl(){
        baseUrl = BaseSetup.setApiBaseUrl(System.getenv("ENV").toLowerCase().trim());
    }

    public Response getCustomerDetails(String endpoint, int customerId){

        requestSpecificationBuilder.setHeaders("Accept", "application/json, text/javascript, */*; q=0.01");
        requestSpecificationBuilder.setPathParams("customerId", customerId);

        return getRequestWithBuilder(baseUrl+endpoint, requestSpecificationBuilder);
    }
}
