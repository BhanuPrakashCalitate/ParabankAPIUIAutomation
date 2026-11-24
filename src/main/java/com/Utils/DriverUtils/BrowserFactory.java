package com.Utils.DriverUtils;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class BrowserFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserFactory.class);
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration SCRIPT_TIMEOUT = Duration.ofSeconds(30);

    private BrowserFactory(){
        //Prevent instantiation
    }

    private static void configureDriver(WebDriver driver, boolean headless){
        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
        driver.manage().timeouts().scriptTimeout(SCRIPT_TIMEOUT);

        // Keeping implicit wait at 0 and relying on explicit WebDriverWait in Page Objects
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        if (!headless){
            driver.manage().window().maximize();
        }
    }

    public static WebDriver createDriver(String browserName, boolean headless){
        BrowserType browserType = BrowserType.browserType(browserName);
        LOGGER.info("Initialising {} browser (headless={})", browserType, headless);

        WebDriver driver = switch (browserType){
            case EDGE -> new EdgeDriver(getEdgeOptions(headless));
            case CHROME -> new ChromeDriver(getChromeOptions(headless));
            case FIREFOX -> new FirefoxDriver(getFirefoxOptions(headless));
        };

        configureDriver(driver, headless);
        return driver;
    }

    private static ChromeOptions getChromeOptions(boolean headless){
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--remote-allow-origins=*");

        if (headless){
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        return options;
    }

    private static FirefoxOptions getFirefoxOptions(boolean headless){
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (headless) {
            options.addArguments("-headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        return options;
    }

    private static EdgeOptions getEdgeOptions(boolean headless){
        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments("--disable-notifications");

        if (headless){
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }
}
