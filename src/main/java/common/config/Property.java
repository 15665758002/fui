package common.config;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static common.config.ConfigurationManager.configuration;

public enum Property {

    MAX_RETRY_COUNT("maxRetryCount"),
    // UI specific
    BROWSER("browser"),
    BROWSER_VERSION("browserVersion"),
    PLATFORM("platform"),
    PLATFORM_VERSION("platformVersion"),
    DEVICE("device"),
    GRID_URL("gridURL"),
    MAXIMISE("maximise"),
    CUSTOM_BROWSER_IMPL("customBrowserImpl"),
    THREADS("threads"),
    HEADLESS("headless");

    private static Properties properties = null;
    private String value;
    private String systemPropertyKey;

    Property(String key) {
        this.systemPropertyKey = key;
        this.value = retrieveValue(key);
    }

    private String retrieveValue(String key) {
        if (System.getProperty(key) != null) {
            return System.getProperty(key);
        } else {
            return getValueFromConfigFile(key);
        }
    }

    private static String getValueFromConfigFile(String key) {
        if (properties == null) {
            properties = loadConfigFile();
        }

        Object objFromFile = properties.get(key);
        if (objFromFile != null) {
            return Objects.toString(objFromFile);
        } else {
            return null;
        }
    }

    private static Properties loadConfigFile() {
        String configFileName = System.getProperty("common/config");
        if (configFileName == null) {
            configFileName = configuration().getConfigDriver();
        }

        if (StringUtils.isBlank(configFileName)) {
            return new Properties();
        }

        try (InputStream configFileStream =
                     ClassLoader.getSystemClassLoader()
                             .getResourceAsStream(configFileName)) {
            Properties properties = new Properties();
            properties.load(configFileStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Properties file '" + configFileName + "' not found.", e);
        }
    }

    /**
     * Check if a property is specified.
     *
     * @return true if the property is not empty ("") and not null
     */
    public boolean isSpecified() {
        return StringUtils.isNotEmpty(value);
    }

    public String getValue() {
        return retrieveValue(this.systemPropertyKey);
    }

    /**
     * @return true if the property is set and is equal, ignoring case, to "true".
     */
    public boolean getBoolean() {
        return isSpecified() && Boolean.parseBoolean(value);
    }

    public int getIntWithDefault(int defaultValue) {
        return isSpecified()
                ? Integer.parseInt(value)
                : defaultValue;
    }
}
