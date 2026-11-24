package com.Utils.DriverUtils;

import org.openqa.selenium.WebDriver;

public class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager(){
        //Prevent instantiation
    }

    public static void setDriver(WebDriver driver){
        if (driver == null){
            throw new IllegalArgumentException("WebDriver instance cannot be null");
        }

        DRIVER_THREAD_LOCAL.set(driver);
    }

    public static WebDriver getDriver(){
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null){
            throw new IllegalStateException("WebDriver has not been initialised for thread: " + Thread.currentThread().getName());
        }
        return driver;
    }

    public static void quitDriver(){
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null){
            try {
                driver.quit();
            }finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
    }
}
