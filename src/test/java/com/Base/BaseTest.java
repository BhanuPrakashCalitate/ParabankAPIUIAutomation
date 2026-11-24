package com.Base;

import com.Utils.APIUtils.RequestSpecificationBuilder;
import com.Utils.ReportUtils.ExtentReportListener;
import com.Utils.ReportUtils.TestNGReport;
import com.Validators.CommonValidator;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

@Listeners({ExtentReportListener.class, TestNGReport.class})
public class BaseTest extends BaseSetup{

    public SoftAssert softAssert;
    public RequestSpecificationBuilder builder;
    protected CommonValidator commonValidator;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup(){
        LOGGER.info("Starting test suite execution");
    }

    @BeforeClass(alwaysRun = true)
    public void classSetup(){
        // Minimal setup for each test class - heavy initialization moved to @BeforeMethod
        LOGGER.info("Initializing test class: {}", this.getClass().getSimpleName());
    }

    @BeforeMethod(alwaysRun = true)
    public void methodSetup(){
        // Setup for each test method
        softAssert = new SoftAssert();
        //builder = new RequestSpecificationBuilder();

        // Initialize common validator with fresh softAssert
        initializeCommonValidator();
    }

    /**
     * Initializes CommonValidator with the current softAssert instance
     * Can be overridden by test classes if they need custom validator initialization
     */
    protected void initializeCommonValidator() {
        // Default implementation - creates CommonValidator for all test classes
        commonValidator = new CommonValidator(softAssert);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        // Cleanup after each test method.
        // NOTE: RestAssured.reset() is intentionally omitted here — it modifies shared static
        // state and is NOT thread-safe when multiple test classes run concurrently via
        // parallel="classes". Each service method already creates its own
        // RequestSpecificationBuilder locally, so no global reset is needed.
        try {
            if (builder != null) {
                builder.reset();
            }
        } catch (Exception e) {
            LOGGER.warn("Error during test method cleanup: {}", e.getMessage());
        }
    }

}
