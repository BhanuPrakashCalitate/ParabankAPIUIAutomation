package com.Base;

import com.Pages.LoginPage;
import com.Utils.DriverUtils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

public class BaseUITest extends BaseTest{

    @BeforeMethod(alwaysRun = true)
    public void initializeBrowser(){
        setupDriver();
        softAssert = new SoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanBrowser(){

        try {
            softAssert.assertAll();
        }finally {
            DriverManager.quitDriver();
        }
    }
}
