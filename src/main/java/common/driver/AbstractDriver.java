package common.driver;

import common.config.Property;
import common.listener.LoggingListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public abstract class AbstractDriver implements Driver {

    protected static final Logger logger = LogManager.getLogger();

    private EventFiringWebDriver webDriverWrapper;

    @Override
    public EventFiringWebDriver getWebDriver() {
        return this.webDriverWrapper;
    }

    /**
     * 创建包装的 driver ，注册 日志监听器
     * 最大化窗口
     */
    public void initialise() {
        this.webDriverWrapper = setupEventFiringWebDriver(getCapabilities());
        maximiseBrowserIfRequired();
    }

    private EventFiringWebDriver setupEventFiringWebDriver(Capabilities capabilities) {
        logger.debug("Browser Capabilities: " + capabilities);
        WebDriver webDriver = getWebDriver(capabilities);
        EventFiringWebDriver eventFiringWD = new EventFiringWebDriver(webDriver);
        eventFiringWD.register(new LoggingListener());
        return eventFiringWD;
    }


    private void maximiseBrowserIfRequired() {
        if (Property.MAXIMISE.getBoolean()) {
            this.webDriverWrapper.manage().window().maximize();
        }
    }

}
