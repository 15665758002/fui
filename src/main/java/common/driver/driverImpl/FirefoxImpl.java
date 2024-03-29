package common.driver.driverImpl;


import common.config.Property;
import common.driver.AbstractDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxImpl extends AbstractDriver {

    @Override
    public FirefoxOptions getCapabilities() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(Property.HEADLESS.getBoolean());
        firefoxOptions.setLogLevel(FirefoxDriverLogLevel.INFO);
        return firefoxOptions;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        final FirefoxOptions firefoxOptions;
        if (capabilities instanceof FirefoxOptions) {
            firefoxOptions = (FirefoxOptions) capabilities;
        } else {
            firefoxOptions = new FirefoxOptions().merge(capabilities);
        }
        return new FirefoxDriver(firefoxOptions);
    }
}
