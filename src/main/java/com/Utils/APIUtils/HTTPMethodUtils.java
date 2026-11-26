package com.Utils.APIUtils;

import com.Base.BaseSetup;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class HTTPMethodUtils extends BaseSetup{

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPMethodUtils.class);

    /*Build request specification based on type of parameter*/
    public RequestSpecification getRequestSpecification(RequestSpecificationBuilder builder){

        RequestSpecification requestSpecification = given();

        try {
            if (builder.hasHeaders()){
                requestSpecification.headers(builder.getHeaders());
            }

            if (builder.hasQueryParams()){
                requestSpecification.queryParams(builder.getQueryParams());
            }

            if (builder.hasPathParams()){
                requestSpecification.pathParams(builder.getPathParams());
            }
        }catch (Exception e){
            LOGGER.error("Error in building request specification", e);
        }

        return requestSpecification;
    }

    public Response getRequestWithBuilder(String url, RequestSpecificationBuilder builder){

        Response response = given().spec(getRequestSpecification(builder)).get(url);

        if (response != null){
            LOGGER.info("-----------Successfully retrieved the response------------" + response.statusCode());
            log(LogStatus.INFO, "Response code: " + response.statusCode());
        }

        return response;
    }

    public Response postRequestWithoutPayload(String url, RequestSpecificationBuilder builder){

        Response response = given().spec(getRequestSpecification(builder)).post(url);
        if (response != null){
            LOGGER.info("-----------Successfully retrieved the response------------" + response.statusCode());
            log(LogStatus.INFO, "Response code: " + response.statusCode());
        }

        return response;
    }
}
