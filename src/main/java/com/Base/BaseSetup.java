package com.Base;

import com.Utils.CommonUtils.ConfigManager;
import com.Utils.DriverUtils.BrowserFactory;
import com.Utils.DriverUtils.DriverManager;
import com.Utils.ReportUtils.ExtentReportListener;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.Optional;

public class BaseSetup extends ExtentReportListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseSetup.class);

    private static final String LOCAL_ENV = "local";
    private static final String PROD_ENV = "prod";
    public Response response = null;

    public static String setApiBaseUrl(String env){

        String apiBaseUrl = "";
        if (LOCAL_ENV.equals(env)){
            apiBaseUrl =  ConfigManager.getProperty("api.url.local");
        } else if (PROD_ENV.equals(env)) {
            apiBaseUrl = ConfigManager.getProperty("api.url.prod");
        }
        return apiBaseUrl;
    }

    public static String setUIUrl(String env){

        String uiUrl = "";
        if (LOCAL_ENV.equals(env)){
            uiUrl =  ConfigManager.getProperty("ui.url.local");
        } else if (PROD_ENV.equals(env)) {
            uiUrl = ConfigManager.getProperty("ui.url.prod");
        }
        return uiUrl;
    }

    public void setupDriver(){
        WebDriver driver = BrowserFactory.createDriver(System.getenv("BROWSER"),
                Boolean.parseBoolean(System.getenv("HEADLESS")));
        DriverManager.setDriver(driver);
    }

    public WebDriver getDriver(){
        return DriverManager.getDriver();
    }

    /**
     * Logs a message to multiple logging systems
     * @param status Log status for extent reports
     * @param message Message to log
     */

    public void log(LogStatus status, String message){
        if (message == null || message.trim().isEmpty()){
            LOGGER.warn("Attempted to log null or empty message");
            return;
        }

        Reporter.log(message, false);
        try {
            ExtentTest currentTest = ExtentReportListener.test.get();
            if (currentTest != null && ExtentReportListener.reports != null){
                currentTest.log(status, message);
            }
        }catch (Exception e){
            LOGGER.warn("ExtentReports log failed (non-fatal: {}", e.getMessage());
        }
        LOGGER.info(message);
    }
}
