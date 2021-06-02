package common.testLifecycle;

import common.listener.ReporterListener;
import common.listener.ScreenshotListener;
import common.listener.TestListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Optional;

@Listeners({
        ScreenshotListener.class,ReporterListener.class,
        TestListener.class})
@Test(groups = "base-ui")
public abstract class BaseUITest {

    /** Logger for subclasses (logs with correct class i.e. not BaseUITest). */
    protected final Logger logger = LogManager.getLogger(this);

    /**
     * Runs before the test suite to initialise a pool of drivers, if requested.
     */
    @BeforeSuite(alwaysRun = true)
    protected static void initialiseDriverPool() {
        TestLifecycle.get().beforeSuite();
    }

    /**
     * Runs before each test method, it initialises the following:
     * </ul>
     */
    @BeforeMethod(alwaysRun = true)
    protected void configureBrowserBeforeTest(Method testMethod) {
        TestLifecycle.get().beforeTestMethod(testMethod);
    }

    /** Tears down the browser after the test method. */
    @AfterMethod(alwaysRun = true)
    protected static void tearDownDriver() {
        TestLifecycle.get().afterTestMethod();
    }

    /**
     * 1、关闭所有浏览器
     * 2、创建allure报告中的环境配置
     */
    @AfterSuite(alwaysRun = true)
    protected static void afterTestSuiteCleanUp() {
        TestLifecycle.get().afterTestSuite();
    }

    /**
     * 返回当前线程使用的 WebDriver
     */
    @Deprecated
    public static WebDriver getWebDriver() {
        return TestLifecycle.get().getWebDriver();
    }

    /**
     * @deprecated use {@code UITestLifecycle.get().getWait()}.
     */
    @Deprecated
    public static Wait<WebDriver> getWait() {
        return TestLifecycle.get().getWait();
    }

    /**
     * @return the user agent of the browser in the first UI test to run.
     * @deprecated use {@code UITestLifecycle.get().getUserAgent()}.
     */
    @Deprecated
    public static Optional<String> getUserAgent() {
        return TestLifecycle.get().getUserAgent();
    }

    public String getSessionId() {
        return TestLifecycle.get().getRemoteSessionId();
    }

}
