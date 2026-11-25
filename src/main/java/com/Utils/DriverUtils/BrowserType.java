package com.Utils.DriverUtils;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType browserType(String browser){
        if (browser == null || browser.isBlank()){
            return CHROME;
        }

        try {
            return BrowserType.valueOf(browser.trim().toUpperCase());
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Unsupported browser: " + browser + "Supported values: CHROME, FIREFOX, EDGE", e);
        }
    }
}
