package org.test.applitool;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class classic_runner {

    private final int viewPortWidth = 800;
    private final int viewPortHeight = 600;
    String myEyesServer = "https://eyes.applitools.com/"; //set to your server/cloud URL
    String appName = "EKB Example : classic app";
    String batchName = "EKB Example : classic";
    //private String apiKey = System.getenv("APPLITOOLS_API_KEY");
    private EyesRunner runner = null;
    private com.applitools.eyes.config.Configuration suiteConfig;
    private Eyes eyes;
    private WebDriver webDriver;

    @BeforeSuite
    public void beforeTestSuite() {
        runner = new ClassicRunner();
        // Create a configuration object, we will use this when setting up each test
        suiteConfig = new Configuration()
                // Checkpoint configurations
                .setForceFullPageScreenshot(true)
                .setStitchMode(StitchMode.CSS)
                .setHideScrollbars(true)
                .setHideCaret(true)
                .setViewportSize( new RectangleSize(viewPortWidth, viewPortHeight))
                // Test suite configurations
                .setApiKey("pkHt2edhFKGrYkBTmO9101RdjWGcFPRdPyBoGIGNdBMSY110")
                .setServerUrl(myEyesServer)
                .setAppName(appName)
                .setBatch(new BatchInfo(batchName)
                        /* ... more configurations */ );
    }


    @BeforeMethod
    public void beforeEachTest(ITestResult result) {

        // Create the Eyes instance for the test and associate it with the runner
        eyes = new Eyes(runner);
        // Set the configuration values we set up in beforeTestSuite
        eyes.setConfiguration(suiteConfig);
        // Create a WebDriver for the test
        webDriver = new ChromeDriver();
    }


    @Test
    public void testHelloWorld() {
        // Update the Eyes configuration with test specific values
        Configuration testConfig = eyes.getConfiguration();
        testConfig.setTestName("Hello World test");
        eyes.setConfiguration(testConfig);

        // Open Eyes, the application,test name
        WebDriver driver = eyes.open(webDriver);

        // Now run the test

        // Visual checkpoint #1.
        driver.get("https://applitools.com/helloworld");   // navigate to website
        eyes.checkWindow("Before mouse click");

        // Visual checkpoint #2
        driver.findElement(By.tagName("button")).click();  // Click the button.
        eyes.checkWindow("After mouse click");
    }

    @AfterMethod
    public void afterEachTest(ITestResult result) {
        // check if an exception was thrown
        boolean testPassed = result.getStatus() != ITestResult.FAILURE;
        if (testPassed) {
            // Close the Eyes instance, no need to wait for results, we'll get those at the end in afterTestSuite
            eyes.closeAsync();
        } else {
            // There was an exception so the test may be incomplete - abort the test
            eyes.abortAsync();
        }
        webDriver.quit();
    }

    @AfterSuite
    public void afterTestSuite(ITestContext testContext) {
        //Wait until the test results are available and retrieve them
        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        for (TestResultContainer result : allTestResults) {
            handleTestResults(result);
        }
    }
    void handleTestResults(TestResultContainer summary) {
        Throwable ex = summary.getException();
        if (ex != null ) {
            System.out.printf("System error occured while checking target.\n");
        }
        TestResults result = summary.getTestResults();
        if (result == null) {
            System.out.printf("No test results information available\n");
        } else {
            System.out.printf("URL = %s, AppName = %s, testname = %s, Browser = %s,OS = %s, viewport = %dx%d, matched = %d,mismatched = %d, missing = %d,aborted = %s\n",
                    result.getUrl(),
                    result.getAppName(),
                    result.getName(),
                    result.getHostApp(),
                    result.getHostOS(),
                    result.getHostDisplaySize().getWidth(),
                    result.getHostDisplaySize().getHeight(),
                    result.getMatches(),
                    result.getMismatches(),
                    result.getMissing(),
                    (result.isAborted() ? "aborted" : "no"));
        }
    }
}