package common.testLifecycle;

import common.driver.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static common.config.ConfigurationManager.configuration;
import static common.testLifecycle.PreLogin.preLogin;

/** @see DriverLifecycle */
public class DriverLifecycle {

    private static final Logger logger = LogManager.getLogger();

    private int poolSize;
    private BlockingDeque<Driver> driverPool;

    //当前线程中的 driver
    private static final ThreadLocal<Driver> threadLocalDriver = new ThreadLocal<>();

    public DriverLifecycle(int poolSize) {
        this.poolSize = poolSize;
    }

    /**
     * 根据并发线程数，创建多个 driver ，放到 driverPool 里
     */
    public void initDriverPool(Supplier<Driver> driverSupplier) {
        if (driverPool != null) {
            throw new IllegalStateException(
                    "initDriverPool called when already initialised");
        }
        driverPool = new LinkedBlockingDeque<>(poolSize);
        IntStream.range(0, poolSize)
                .parallel()
                .mapToObj(i -> driverSupplier.get())
                .forEach(driverPool::addLast);
    }

    /**
     * 1、用于方法执行之前，从driverPool里获取driver。
     * 2、如果driver没有登录，就去登录。
     */
    public void initBrowserBeforeTest() {
        threadLocalDriver.set(driverPool.removeFirst());
        WebDriver currentWebDriver = getWebDriver();
        if (!currentWebDriver.getCurrentUrl().contains(configuration().isLoginurl())) {
            preLogin(currentWebDriver);
        }
    }

    //获取 当前线程 driver中的 webDriver
    public WebDriver getWebDriver() {
        return threadLocalDriver.get().getWebDriver();
    }

    /**
     * 销毁当前线程中使用的 driver
     */
    public void tearDownDriver() {
        try {
            Driver driver = threadLocalDriver.get();
            driver.getWebDriver().manage().deleteAllCookies();
            driverPool.addLast(driver);
        } catch (Exception e) {
            logger.error("Failed to tear down browser after test method.");
            logger.debug("Failed to tear down browser after test method.", e);
            throw e;
        } finally {
            threadLocalDriver.remove();
        }
    }

    /**
     * 销毁 driverPool 中的 所有 driver。
     */
    public void tearDownDriverPool() {
        if (driverPool == null) {
            return;
        }

        driverPool.parallelStream()
                .forEach(driver -> {
                    try {
                        driver.getWebDriver().quit();
                    } catch (Exception e) {
                        logger.error("Failed to quit a browser in the pool.");
                        logger.debug("Failed to quit a browser in the pool.", e);
                    }
                });

        driverPool = null; // allows re-initialisation
    }
}

