package common.testLifecycle;

import com.paulhammant.ngwebdriver.NgWebDriver;
import common.element.ExtraExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

import java.util.concurrent.TimeUnit;

import static common.testLifecycle.TestLifecycle.DEFAULT_TIMEOUT;

/**
 * implementation of waiting for JS events on page-load.
 */
public class JavascriptWait {

    private final Wait<WebDriver> wait;
    private final JavascriptExecutor javascriptExecutor;

    public JavascriptWait(
            JavascriptExecutor javascriptExecutor, Wait<WebDriver> wait) {
        this.wait = wait;
        this.javascriptExecutor = javascriptExecutor;
    }

    /**
     * Default entry to {@link JavascriptWait}.
     * The following actions are waited for:
     * <ol>
     * <li>Document state to be ready</li>
     * <li>If page is using Angular, it will detect and wait</li>
     * </ol>
     */
    public void waitForJavascriptEventsOnLoad() {
        waitForDocumentReady();
        waitForAngular();
    }

    /**
     * If a page is using a supported JS framework, it will wait until it's ready.
     */
    public void waitForJavascriptFramework() {
        waitForAngular();
    }

    private void waitForDocumentReady() {
        wait.until(ExtraExpectedConditions.documentBodyReady());
    }

    private void waitForAngular() {
        new NgWebDriver(javascriptExecutor).waitForAngularRequestsToFinish();
    }


    public WebElement forElementVisible(WebElement element, int timeoutSec, int polling) {
        changeImplicitWait(250, TimeUnit.MILLISECONDS);
        try {
            return wait.until(ExpectedConditions.visibilityOf(
                    element));
        } finally {
            restoreDeaultImplicitWait();
        }
    }

    public WebElement forElementVisible(WebElement element, int timeoutSec) {
        return forElementVisible(element, timeoutSec, 500);
    }


    /**
     * Wait for element to be either invisible or not present on the DOM.
     */
    public boolean forElementNotVisible(WebElement element) {
        changeImplicitWait(250, TimeUnit.MILLISECONDS);
        try {
            return wait.until(ExtraExpectedConditions.invisibilityOfElementLocated(element));
        } finally {
            restoreDeaultImplicitWait();
        }
    }


    /**
     * Wait for element to be in viewport Either position top or left is bigger then -1
     */
    public boolean forElementInViewPort(WebElement element) {
        changeImplicitWait(0, TimeUnit.MILLISECONDS);
        try {
            return wait.until(ExtraExpectedConditions.elementInViewPort(element));
        } finally {
            restoreDeaultImplicitWait();
        }
    }

    public boolean forValueToBePresentInElementsAttribute(
            WebElement element, String attribute, String value) {
        changeImplicitWait(0, TimeUnit.SECONDS);
        try {
            return wait.until(ExtraExpectedConditions.valueToBePresentInElementsAttribute(element,
                    attribute,
                    value
            ));
        } finally {
            restoreDeaultImplicitWait();
        }
    }

    public boolean forAttributeToContain(WebElement element, String attribute, String expectedValue) {
        changeImplicitWait(0, TimeUnit.SECONDS);
        try {
            return wait.until(ExtraExpectedConditions.valueToBePresentInElementsAttribute(element,
                    attribute,
                    expectedValue
            ));
        } finally {
            restoreDeaultImplicitWait();
        }
    }

    public boolean forAttributeToBePresent(WebElement element, String attribute) {
        changeImplicitWait(0, TimeUnit.SECONDS);
        try {
            return wait.until(ExtraExpectedConditions.attributeToBePresentInElement(element, attribute));
        } finally {
            restoreDeaultImplicitWait();
        }
    }


    protected void restoreDeaultImplicitWait() {
        changeImplicitWait((int) DEFAULT_TIMEOUT.getSeconds(), TimeUnit.SECONDS);
    }

    protected void changeImplicitWait(int value, TimeUnit timeUnit) {
        TestLifecycle.get().getWebDriver().manage().timeouts().implicitlyWait(value, timeUnit);
    }

}
