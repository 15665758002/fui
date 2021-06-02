package common.testLifecycle;

import common.config.Property;
import common.driver.DriverSetup;
import common.driver.UserAgent;
import common.reporting.allure.AllureProperties;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Handles all UI test related state and life cycle.
 * Contains a ThreadLocal instance
 */
public class TestLifecycle {

    public static final Duration DEFAULT_TIMEOUT = Duration.of(20, SECONDS);

    private static final ThreadLocal<Wait<WebDriver>> wait = new ThreadLocal<>();
    private static final ThreadLocal<TestLifecycle> TestLifecycle = ThreadLocal.withInitial(TestLifecycle::new);

    private static DriverLifecycle driverLifecycle;
    private static String userAgent;

    /** @return ThreadLocal instance*/
    public static TestLifecycle get() {
        return TestLifecycle.get();
    }

    /**
     * suite执行之前： 创建多个浏览器driver 放到driver池子里。用于并行执行用例
     */
    public void beforeSuite() {
        driverLifecycle =
                new DriverLifecycle(
                        Property.THREADS.getIntWithDefault(1));

        driverLifecycle.initDriverPool(DriverSetup::instantiateDriver);
    }

    /**
     * 1、方法执行之前：获取浏览器driver，如果没有登录，处理登录
     * 2、设置wait  和  userAgent
     */
    public void beforeTestMethod(Method testMethod) {
        driverLifecycle.initBrowserBeforeTest();
        wait.set(newWaitWithTimeout(DEFAULT_TIMEOUT));
        if (userAgent == null) {
            userAgent = UserAgent.getUserAgent((JavascriptExecutor) getWebDriver());
        }
    }


    /** 测试方法执行完成后，将driver 还回 driver池子 */
    public void afterTestMethod() {
        driverLifecycle.tearDownDriver();
    }

    /**
     * 1、suite 执行完之后，清空 driver 池子
     * 2、create properties for Allure.
     */
    public void afterTestSuite() {
        driverLifecycle.tearDownDriverPool();
        AllureProperties.createUI();
    }

    /**
     * @return new Wait with default timeout.
     * @deprecated use {@code UITestLifecycle.get().getWait()} instead.
     */
    @Deprecated
    public Wait<WebDriver> newDefaultWait() {
        return newWaitWithTimeout(DEFAULT_TIMEOUT);
    }

    public Wait<WebDriver> newWaitWithTimeout(Duration timeout) {
        return new FluentWait<>(getWebDriver())
                .withTimeout(timeout)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public WebDriver getWebDriver() {
        return driverLifecycle.getWebDriver();
    }


    public Wait<WebDriver> getWait() {
        if (wait.get() == null) {
            wait.set(newWaitWithTimeout(DEFAULT_TIMEOUT));
        }
        return wait.get();
    }

    /**
     * 返回浏览器userAgent，allure报告里用
     */
    public Optional<String> getUserAgent() {
        return Optional.ofNullable(userAgent);
    }

    /** 返回 WebDriver 的 sessionID */
    public String getRemoteSessionId() {
        return Objects.toString(((RemoteWebDriver) getWebDriver()).getSessionId());
    }

}
