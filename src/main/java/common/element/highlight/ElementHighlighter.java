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
