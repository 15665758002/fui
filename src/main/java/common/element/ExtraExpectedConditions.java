package common.element;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 扩展断言
 */
public class ExtraExpectedConditions {

    protected ExtraExpectedConditions() {
        // hide default constructor for this util class
        // but allow subclassing for more specialised ExpectedConditions
    }

    /**
     * 断言某个元素 是否 不存在、不可见
     */
    public static ExpectedCondition<Boolean> notPresentOrInvisible(WebElement element) {

        return expectedCondition(
                driver -> {
                    try {
                        return !element.isDisplayed();
                    } catch (NoSuchElementException e) {
                        return true;
                    }
                },
                String.format("element '%s' to be not present or be invisible", element));
    }

    /**
     * 多个元素都不存在 或不可见，返回元素，否则返回null
     */
    public static ExpectedCondition<List<? extends WebElement>> notPresentOrInvisible(
            List<? extends WebElement> elements) {

        return expectedCondition(driver ->
                        elements.stream()
                                .noneMatch(WebElement::isDisplayed)
                                ? elements
                                : null,
                String.format(
                        "the following elements to not be present or be invisible: %s",
                        elements.stream()
                                .map(WebElement::toString)
                                .collect(Collectors.joining(", "))));
    }

    /**
     * 如果元素个数大于预期，返回元素列表
     */
    public static ExpectedCondition<List<? extends WebElement>> sizeGreaterThan(
            List<? extends WebElement> list, int expectedSize) {

        return expectedCondition(
                driver -> list.size() > expectedSize ? list : null,
                "list size of " + list.size() + " to be greater than " + expectedSize);
    }


    public static ExpectedCondition<List<? extends WebElement>> sizeLessThan(
            List<? extends WebElement> list, int expectedSize) {

        return expectedCondition(
                driver -> list.size() < expectedSize ? list : null,
                "list size of " + list.size() + " to be less than " + expectedSize);
    }

    /**
     * Wait until all jQuery AJAX calls are done.
     *
     * @return true iff jQuery is available and 0 ajax queries are active.
     */
    public static ExpectedCondition<Boolean> jQueryAjaxDone() {

        return javascriptExpectedCondition(
                "return !!window.jQuery && jQuery.active === 0;",
                "jQuery AJAX queries to not be active");
    }

    /**
     * Wait for the document ready state to equal 'complete'.
     * Useful for javascript loading on page-load.
     */
    public static ExpectedCondition<Boolean> documentBodyReady() {

        return javascriptExpectedCondition(
                "return document.readyState == 'complete';",
                "the document ready state to equal 'complete'");
    }

    private static ExpectedCondition<Boolean> javascriptExpectedCondition(
            String query, String message) {
        return expectedCondition(
                driver -> (Boolean) ((JavascriptExecutor) driver).executeScript(query),
                message);
    }

    private static <T> ExpectedCondition<T> expectedCondition(
            Function<WebDriver, T> function, String string) {

        return new ExpectedCondition<T>() {
            @Override
            public T apply(WebDriver driver) {
                return function.apply(driver);
            }

            @Override
            public String toString() {
                return string;
            }
        };
    }

    /**
     * 判断某个元素是否有 某属性
     */
    public static ExpectedCondition<Boolean> attributeToBePresentInElement(
            final WebElement element, final String attribute) {

        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver from) {
                try {
                    return element.getAttribute(attribute) != null;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return String.format("'%s' attribute to be present in element", attribute);
            }
        };
    }

    /**
     * An expectation for checking if the given text is present in the specified element.
     */
    public static ExpectedCondition<Boolean> valueToBePresentInElementsAttribute(
            final WebElement element, final String attribute, final String value) {

        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver from) {
                try {
                    String elementsAttributeValue = element.getAttribute(attribute);
                    return elementsAttributeValue.contains(value);
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return String.format("value ('%s') to be present in element found by %s",
                        value,
                        element.getTagName()
                );
            }
        };
    }

    public static ExpectedCondition<Boolean> textToBePresentInElementAfterRefresh(
            final WebElement element, final String text) {

        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                driver.navigate().refresh();
                String elementText = element.getText();
                return elementText.contains(text);
            }

            @Override
            public String toString() {
                return String.format("text ('%s') to be present in element %s", text, element.toString());
            }
        };
    }


    public static ExpectedCondition<Boolean> invisibilityOfElementLocated(final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
                    Boolean isDisplayed = element.isDisplayed();
                    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                    return !isDisplayed;
                } catch (StaleElementReferenceException e) {
                    // Returns true because stale element reference implies that element
                    // is no longer visible.
                    return true;
                } catch (NoSuchElementException e) {
                    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                    return true;
                }
            }

            @Override
            public String toString() {
                return "element to no longer be visible: " + element.toString();
            }
        };
    }


    public static ExpectedCondition<Boolean> elementInViewPort(final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                Dimension size = element.getSize();
                Point location = element.getLocation();
                if (((size.height + location.y) > -1) && (size.width + location.x > -1)) {
                    return true;
                }
                return false;
            }

            @Override
            public String toString() {
                return String.format("Element ('%s') not in viewport!", element.getTagName());
            }
        };
    }

    public static ExpectedCondition<Boolean> newWindowPresent() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                Object[] windows = driver.getWindowHandles().toArray();
                return (windows.length > 1);
            }

            @Override
            public String toString() {
                return String.format("New window not found");
            }
        };
    }
}
