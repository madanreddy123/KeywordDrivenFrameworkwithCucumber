package com.base;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.manager.SeleniumManager;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

public class DriverManager {
    private WebDriver driver;
    DesiredCapabilities cap = null;
    public WebDriver getDriver() throws IOException, InterruptedException {
        String browser = null;
        if (Reporter.getCurrentTestResult() != null) {
            browser = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browser");
        }
        if (browser == null) {
            browser = "chrome";
        }
        String path=System.getProperty("user.dir");
        switch (browser.toLowerCase()) {
            case "edge":
                System.setProperty("webdriver.edge.driver", "src/main/resources/drivers/msedgedriver.exe");
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--disable-extensions");
                edgeOptions.addArguments("disable-infobars");
                edgeOptions.addArguments("start-maximized");
                edgeOptions.addArguments("--disable-gpu");
                driver = new EdgeDriver(edgeOptions);
                driver.manage().deleteAllCookies();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                break;
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
                //WebDriverManager.chromedriver().setup(); // Line 2
                String downloadFilepath = System.getProperty("user.dir")+File.separator+"downloads";
                HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
                chromePrefs.put("profile.default_content_settings.popups", 0);
                chromePrefs.put("download.default_directory", downloadFilepath);
//                ChromeOptions options = new ChromeOptions();
                ChromeOptions options1 = new ChromeOptions();
                options1.addArguments("--disable-extensions");
                options1.addArguments("disable-infobars");
                options1.addArguments("start-maximized");
                options1.addArguments("--disable-gpu");
                options1.setExperimentalOption("prefs", chromePrefs);
                driver = new ChromeDriver(options1);
                driver.manage().deleteAllCookies();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                break;

            case "firefox":

                System.setProperty("webdriver.gecko.driver", "src/main/resources/drivers/geckodriver.exe");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--disable-extensions");
                firefoxOptions.addArguments("disable-infobars");
                firefoxOptions.addArguments("start-maximized");
                firefoxOptions.addArguments("--disable-gpu");
                driver = new FirefoxDriver(firefoxOptions);
                driver.manage().deleteAllCookies();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                break;

            case "gridfirefox":
                //System.setProperty("webdriver.gecko.driver", "src/main/resources/drivers/geckodriver.exe");
                //executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.0.jar\" hub");
//                Thread.sleep(5000);
//                executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.0.jar\" node");
//                Thread.sleep(5000);
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("--ignore-certificate-errors",
                        "--allow-insecure-localhost","--disable-gpu");
                options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
                cap = new DesiredCapabilities();
                cap.setBrowserName("firefox");
                cap.setVersion("96.0.4664.45");
                cap.setPlatform(Platform.ANY);
                cap.setCapability("platform", Platform.WIN10);
                cap.setCapability(ChromeOptions.CAPABILITY,options);
                System.out.println("before remote");
                driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),cap);
                System.out.println("after remote");
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                break;

            case "gridchrome":

//                System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
////                File file=new File(""+path+"/src/main/resources/drivers/selenium-server-4.1.1.jar");
////                file.setExecutable(true);
//                executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.1.jar\" hub");
//                executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.1.jar\" node");
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--ignore-certificate-errors",
                        "--allow-insecure-localhost","--disable-gpu");
                chromeOptions.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
                cap = new DesiredCapabilities();
                cap.setBrowserName("chrome");
                cap.setCapability(ChromeOptions.CAPABILITY,chromeOptions);
                System.out.println("before remote");
                driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),cap);
                System.out.println("after remote");
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                break;

            case "dockerchrome":

//                System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
////                File file=new File(""+path+"/src/main/resources/drivers/selenium-server-4.1.1.jar");
////                file.setExecutable(true);
//                executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.1.jar\" hub");
//                executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.1.jar\" node");
                ChromeOptions chromeOptions1 = new ChromeOptions();
                chromeOptions1.addArguments("--ignore-certificate-errors",
                        "--allow-insecure-localhost","--disable-gpu", "--incognito");
//                chromeOptions1.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
                cap = new DesiredCapabilities();
                cap.setBrowserName("chrome");
                cap.setPlatform(Platform.ANY);
                cap.setCapability(ChromeOptions.CAPABILITY,chromeOptions1);
                System.out.println("before remote");
                driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),cap);
                System.out.println("after remote");
                driver.manage().window().maximize();
                driver.manage().deleteAllCookies();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                break;


            case "gridAppium":

                cap.setCapability("platformName", "Android");
                cap.setCapability("deviceName", "docker-android:emulator_11.0");
                cap.setCapability("automationName", "UiAutomator2");
                cap.setCapability("appPackage", "com.android.calculator2");
                cap.setCapability("appActivity", "com.android.calculator2.Calculator");


                // Initialize Appium driver
                driver=new RemoteWebDriver(new URL("http://localhost:4723/wd/hub"),cap);

                break;

            case "gridedge":
                //System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/msedgedriver.exe");
//                file=new File(""+path+"/src/main/resources/drivers/selenium-server-4.1.0.jar");
//                file.setExecutable(true);
//                executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.0.jar\" hub");
//                Thread.sleep(5000);
//                executeCMDCommands("java -Xmx1024m -jar \""+path+"/src/main/resources/drivers/selenium-server-4.1.0.jar\" node");
//                Thread.sleep(5000);
                EdgeOptions edgeOption = new EdgeOptions();
                edgeOption.addArguments("--ignore-certificate-errors",
                        "--allow-insecure-localhost","--disable-gpu");
                edgeOption.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
                cap = new DesiredCapabilities();
                cap.setBrowserName("MicrosoftEdge");
                cap.setPlatform(Platform.ANY);
                cap.setCapability("platform", Platform.ANY);
                cap.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);
                System.out.println("before remote");
                //edgeOption.merge(cap);
                driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),cap);
                System.out.println("after remote");
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + browser.toLowerCase());
        }

        return driver;
    }

    public WebDriver getDriverInstance() {

        return driver;
    }

    public void closeBrowser() {
        driver.quit();
    }


}
