package common.driver;

import common.config.Property;
import common.driver.driverImpl.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;

import java.lang.reflect.InvocationTargetException;

public class DriverSetup {

    public static final Browser DEFAULT_BROWSER = Browser.CHROME;

    /** Supported drivers. */
    public enum Browser {
        FIREFOX, CHROME, EDGE, IE, SAFARI, CUSTOM
    }

    /** Supported remote grids. */
    private enum RemoteGrid {
        GRID
    }

    /** Supported platforms for remote grids. */
    public enum Platform {
        WINDOWS, OSX, IOS, ANDROID, NONE
    }

    protected static final Logger logger = LogManager.getLogger();

    /**
     * 返回一个具体的 driver实现类，包含 webDriver，比如chromeImpl。内部打开 浏览器。
     */
    public static Driver instantiateDriver() {
        Driver driver = createDriverImpl(getBrowserTypeFromProperty());
        if (useRemoteDriver()) {
            driver = instantiateDesiredRemote(driver);
        }
        driver.initialise();
        return driver;
    }

    /**
     * 返回远程 driver实现类
     */
    private static Driver instantiateDesiredRemote(Driver driver) {

        Capabilities capabilities = driver.getCapabilities();
        switch (getRemoteType()) {
            case GRID:
                return new GridImpl(capabilities);
            default:
                return driver;
        }
    }

    private static Driver createDriverImpl(Browser browser) {
        switch (browser) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxImpl();
            case CHROME:
                WebDriverManager.chromedriver().setup();
                return new ChromeImpl();
            case EDGE:
                WebDriverManager.edgedriver().setup();
                return new EdgeImpl();
            case IE:
                WebDriverManager.iedriver().setup();
                return new InternetExplorerImpl();
            case SAFARI:
                return new SafariImpl();
            case CUSTOM:
                String customBrowserImpl = Property.CUSTOM_BROWSER_IMPL.getValue();
                try {
                    return getCustomBrowserImpl(customBrowserImpl)
                            .getDeclaredConstructor()
                            .newInstance();
                } catch (InstantiationException | IllegalAccessException
                        | NoSuchMethodException | InvocationTargetException e) {
                    throw new IllegalArgumentException(
                            "Unable to use custom browser implementation - " + customBrowserImpl, e);
                }
            default:
                throw new IllegalArgumentException("Invalid Browser specified");
        }
    }

    public static boolean useRemoteDriver() {
        return Property.GRID_URL.isSpecified();
    }

    //返回指定的浏览器,默认是 chrome
    private static Browser getBrowserTypeFromProperty() {
        if (Property.CUSTOM_BROWSER_IMPL.isSpecified()) {
            return Browser.CUSTOM;
        } else if (Property.BROWSER.isSpecified()) {
            return Browser.valueOf(Property.BROWSER.getValue().toUpperCase());
        } else {
            return DEFAULT_BROWSER;
        }
    }

    private static RemoteGrid getRemoteType() {
        return RemoteGrid.GRID;
    }

    /**
     * 返回自定义驱动的实现类。暂时不用
     */
    private static Class<? extends Driver> getCustomBrowserImpl(String implClassName) {
        try {
            return Class.forName(implClassName).asSubclass(Driver.class);
        } catch (ClassNotFoundException ex) {
            String message = "Failed to find custom browser implementation class: " + implClassName;
            logger.fatal(message, ex);
            throw new IllegalArgumentException(message
                    + "\nFully qualified class name is required. "
                    + "e.g. com.frameworkium.ui.MyCustomImpl");
        } catch (ClassCastException ex) {
            String message = String.format(
                    "Custom browser implementation class '%s' does not implement the Driver interface.",
                    implClassName);
            logger.fatal(message, ex);
            throw new IllegalArgumentException(message, ex);
        }
    }
}
