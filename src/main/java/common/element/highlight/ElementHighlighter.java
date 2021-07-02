package common.element.highlight;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementHighlighter {

    private JavascriptExecutor js;
    private WebElement previousElem;

    public ElementHighlighter(WebDriver driver) {
        js = (JavascriptExecutor) driver;
    }

    /**
     * Highlight a WebElement.
     *
     * @param webElement to highlight
     */
    public void highlightElement(WebElement webElement) {

        previousElem = webElement; // remember the new element
        try {
            // TODO: save the previous border
            js.executeScript("element = arguments[0];"  +
                    "original_style = element.getAttribute('style');" +
                    "element.setAttribute('style', original_style + \";"  +
                    " border: 2px solid red;\");"
                    , webElement);
//            "setTimeout(function(){element.setAttribute('style', original_style);}, 1000);", webElement);

        } catch (StaleElementReferenceException ignored) {
            // something went wrong, but no need to crash for highlighting
        }
    }
    public void highlightElementNew(WebElement webElement) {

        previousElem = webElement; // remember the new element
        try {
            // TODO: save the previous border
//            Object original_style = js.executeScript("arguments[0].getAttribute('style');"
//                    , webElement);

            js.executeScript("element = arguments[0];"  +
                            "element.style.boxShadow =\n" +
                            "'0px 0px 6px 6px rgba(128, 128, 128, 0.5)';"
                    , webElement);
            Thread.sleep(20);
            js.executeScript("element = arguments[0];"  +
                            "element.style.boxShadow =\n" +
                            "'0px 0px 6px 6px rgba(255, 0, 0, 1)';"
                    , webElement);
            Thread.sleep(20);
            js.executeScript("element = arguments[0];"  +
                            "element.style.boxShadow =\n" +
                            "'0px 0px 6px 6px rgba(128, 0, 128, 1)';"
                    , webElement);
            Thread.sleep(20);
            js.executeScript("element = arguments[0];"  +
                            "element.style.boxShadow =\n" +
                            "'0px 0px 6px 6px rgba(0, 0, 255, 1)';"
                    , webElement);
            Thread.sleep(20);
            js.executeScript("element = arguments[0];"  +
                            "element.style.boxShadow =\n" +
                            "'0px 0px 6px 6px rgba(0, 255, 0, 1)';"
                    , webElement);
            Thread.sleep(20);
            js.executeScript("element = arguments[0];"  +
                            "element.style.boxShadow =\n" +
                            "'0px 0px 6px 6px rgba(128, 128, 0, 1)';"
                    , webElement);
            Thread.sleep(20);
            js.executeScript("element = arguments[0];" +
                    "element.style.boxShadow ='0px 0px 6px 6px rgba(128, 0, 128, 1)';", webElement);
            Thread.sleep(20);

//            js.executeScript("arguments[0].setAttribute('style',arguments[1]);"
//                    , webElement,original_style);
        } catch (InterruptedException e) {
        }
    }
    /**
     * Unhighlight the previously highlighted WebElement.
     */
    public void unhighlightPrevious() {

        try {
            // unhighlight the previously highlighted element
            js.executeScript("arguments[0].style.border='none'", previousElem);
        } catch (StaleElementReferenceException ignored) {
            // the page was reloaded/changed, the same element isn't there
        }
    }
}
