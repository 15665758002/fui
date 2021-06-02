package common.reporting.allure;

import common.config.Property;
import common.testLifecycle.TestLifecycle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Creates the Allure environment.properties file based on properties used.
 * Each method will overwrite the existing file.
 */
public class AllureProperties {

    private static final Logger logger = LogManager.getLogger();

    private AllureProperties() {
        // hide default constructor for this util class
    }

    public static void createUI() {
        Properties props = new Properties();
        props.putAll(getAllFrameworkiumProperties());
        props.putAll(getCommonProps());
        props.putAll(getUIProperties());
        save(props);
    }

    public static void createAPI() {
        Properties props = new Properties();
        props.putAll(getAllFrameworkiumProperties());
        props.putAll(getCommonProps());
        save(props);
    }

    private static Properties getAllFrameworkiumProperties() {
        Map<String, String> allProperties =
                Arrays.stream(Property.values())
                        .filter(Property::isSpecified)
                        .collect(Collectors.toMap(
                                Property::toString,
                                AllureProperties::obfuscatePasswordValue));

        Properties properties = new Properties();
        properties.putAll(allProperties);
        return properties;
    }

    private static String obfuscatePasswordValue(Property p) {
        String key = p.toString();
        String value = p.getValue();
        if (key.toLowerCase().contains("password")) {
            return value.replaceAll(".", "*");
        }
        return value;
    }

    private static Properties getCommonProps() {
        Properties props = new Properties();

        if (nonNull(System.getenv("BUILD_URL"))) {
            props.setProperty("CI build URL", System.getenv("BUILD_URL"));
        }
        if (nonNull(System.getProperty("common/config"))) {
            props.setProperty("Config file", System.getProperty("common/config"));
        }

        return props;
    }

    private static Properties getUIProperties() {
        Properties props = new Properties();
        TestLifecycle.get().getUserAgent()
                .ifPresent(ua -> props.setProperty("UserAgent", ua));
        return props;
    }

    private static void save(Properties props) {
        try (FileOutputStream fos = new FileOutputStream(
                "target/allure-results/environment.properties")) {
            props.store(fos,
                    "See https://github.com/allure-framework/allure-core/wiki/Environment");
        } catch (IOException e) {
            logger.error("IO problem when writing allure properties file", e);
        }
    }
}
