package com.Utils.ReportUtils;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TestNG listener that writes a hierarchical ExtentReports HTML report.
 *
 * Report structure:
 *   [Test node name  e.g. "Customer flows"]
 *       └── fetchCustomerDetails
 *       └── createAccount
 *       └── ...
 *   [Test node name  e.g. "Funds transfer flows"]
 *       └── initiateFund
 *       └── ...
 *
 * ISuiteListener.onStart  → creates the report file ONCE for the whole suite.
 * ITestListener.onStart(ITestContext) → creates one parent ExtentTest per <test> node.
 * ITestListener.onTestStart → creates a child ExtentTest under its node's parent.
 * ISuiteListener.onFinish → final flush + close, also once for the whole suite.
 */

public class ExtentReportListener implements ITestListener, IReporter, ISuiteListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtentReportListener.class);

    public static volatile ExtentReports reports;

    /** Holds the currently-running method's ExtentTest, one slot per thread. */
    public static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    /**
     * One parent ExtentTest per {@code <test>} node (keyed by node name).
     * Populated in onStart(ITestContext) before any method in that node runs.
     */
    private static final Map<String, ExtentTest> nodeTestMap = new ConcurrentHashMap<>();

    private static final String REPORT_LOCATION = "./test-output/reports/test/";

    // -------------------------------------------------------------------------
    // ISuiteListener — fires ONCE for the entire suite
    // -------------------------------------------------------------------------

    @Override
    public void onStart(ISuite suite){
        if (reports == null){
            synchronized (ExtentReportListener.class){
                if (reports == null){
                    new File(REPORT_LOCATION).mkdirs();
                    reports = new ExtentReports(REPORT_LOCATION + "ExtentReports.html", true);
                }
            }
        }
    }

    @Override
    public void onFinish(ISuite suite){
        synchronized (ExtentReportListener.class){
            if (reports != null){
                reports.flush();
                reports.close();
                reports = null;
                nodeTestMap.clear();
            }
        }
    }

    // -------------------------------------------------------------------------
    // ITestListener node-level callbacks — one call per <test> node
    // -------------------------------------------------------------------------

    /**
     * Creates a parent ExtentTest for the {@code <test>} node so that all
     * method-level tests can be appended as children under it.
     */
    @Override
    public void onStart(ITestContext context){
        if (reports == null){
            return;
        }

        ExtentTest nodeTest = reports.startTest(context.getName(), "Test node: " + context.getName());
        nodeTestMap.put(context.getName(), nodeTest);
    }

    /** Ends the parent ExtentTest for this node and flushes to disk. */
    @Override
    public void onFinish(ITestContext context){
        ExtentTest nodeTest = nodeTestMap.get(context.getName());
        if (nodeTest != null && reports != null){
            reports.endTest(nodeTest);
            reports.flush();
        }
    }

    // -------------------------------------------------------------------------
    // ITestListener method-level callbacks
    // -------------------------------------------------------------------------

    @Override
    public void onTestStart(ITestResult result) {
        if (reports == null) return;
        ExtentTest methodTest = reports.startTest(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        methodTest.log(LogStatus.INFO, "Started: " + result.getMethod().getMethodName());

        // Nest this method under its <test> node parent
        String nodeName = result.getTestContext().getName();
        ExtentTest parent = nodeTestMap.get(nodeName);
        if (parent != null) {
            parent.appendChild(methodTest);
        }

        test.set(methodTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest t = test.get();
        if (t == null || reports == null) return;
        String desc = result.getMethod().getDescription();
        t.log(LogStatus.PASS, buildLogJson(desc, "PASSED", "NA", "NA"));
        reports.endTest(t);
        reports.flush();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = test.get();
        if (t == null || reports == null) return;
        String desc = result.getMethod().getDescription();
        String exType = result.getThrowable() != null
                ? result.getThrowable().getClass().getSimpleName() : "Unknown";
        String exMsg = result.getThrowable() != null
                ? result.getThrowable().getMessage() : "No message";
        t.log(LogStatus.FAIL, buildLogJson(desc, "FAILED", exType, exMsg));
        reports.endTest(t);
        reports.flush();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest t = test.get();
        if (t == null || reports == null) return;
        String desc = result.getMethod().getDescription();
        String exType = result.getThrowable() != null
                ? result.getThrowable().getClass().getSimpleName() : "Unknown";
        String exMsg = result.getThrowable() != null
                ? result.getThrowable().getMessage() : "No message";
        t.log(LogStatus.SKIP, buildLogJson(desc, "SKIPPED", exType, exMsg));
        reports.endTest(t);
        reports.flush();
    }

    private static String buildLogJson(String testDesc, String status,
                                       String exceptionType, String detailedError) {
        return "{\n"
                + "   \"status\" : \"" + status + "\",\n"
                + "   \"testDescription\" : \"" + testDesc + "\",\n"
                + "   \"exceptionType\" : \"" + exceptionType + "\",\n"
                + "   \"detailedError\" : \"" + detailedError + "\"\n"
                + "}";
    }

}
