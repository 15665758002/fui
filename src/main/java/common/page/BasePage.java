package common.page;

import common.element.Visibility;
import common.reporting.allure.AllureLogger;
import common.testLifecycle.JavascriptWait;
import common.testLifecycle.TestLifecycle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import java.time.Duration;

public abstract class BasePage<T extends BasePage<T>> {

    protected final Logger logger = LogManager.getLogger(this);

    protected final WebDriver driver;
    protected Wait<WebDriver> wait;
    private Visibility visibility;
    private JavascriptWait javascriptWait;

    public BasePage() {
        this(TestLifecycle.get().getWebDriver(), TestLifecycle.get().getWait());
    }

    public BasePage(WebDriver driver, Wait<WebDriver> wait) {
        this.driver = driver;
        this.wait = wait;
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        this.visibility = new Visibility(wait, javascriptExecutor);
        this.javascriptWait = new JavascriptWait(javascriptExecutor, wait);
    }

    /**
     * Get the current page object. Useful for e.g.
     * <code>myPage.get().then().doSomething();</code>
     */
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    /**
     * 打开传入的 url，返回初始化完成的页面对象
     */
    public T get(String url) {
        driver.get(url);
        return get();
    }

    public T get(String url, Duration timeout) {
        updatePageTimeout(timeout);
        return get(url);
    }

    public T get(Duration timeout) {
        updatePageTimeout(timeout);
        return get();
    }

    private void updatePageTimeout(Duration timeout) {
        wait = TestLifecycle.get().newWaitWithTimeout(timeout);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        visibility = new Visibility(wait, jsExecutor);
        javascriptWait = new JavascriptWait(jsExecutor, wait);
    }

    /**
     * 1、初始化页面对象。2、等待加可见注解的元素。3、写日志到allure。
     */
    @SuppressWarnings("unchecked")
    public T get() {
        initPageObjectFields();

        // Wait for Elements & JS
        visibility.waitForAnnotatedElementVisibility(this);
//        javascriptWait.waitForJavascriptEventsOnLoad();

        // Log
        logPageLoadToAllure();

        return (T) this;
    }

    /**
     * 用 htmlelement 工具提供的初始化页面对象方法，可初始化页面块。
     */
    protected void initPageObjectFields() {
        HtmlElementLoader.populatePageObject(this, driver);
    }

    private void logPageLoadToAllure() {
        try {
            AllureLogger.logToAllure("Page '" + getClass().getName() + "' successfully loaded");
        } catch (Exception e) {
            logger.warn("Error logging page load, but loaded successfully", e);
        }
    }

    private String getSimplePageObjectName() {
        String packageName = getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1)
                + "."
                + getClass().getSimpleName();
    }

    /** Get title of the web page. */
    public String getTitle() {
        return driver.getTitle();
    }

    /** Get page source code of the current page. */
    public String getSource() {
        return driver.getPageSource();
    }

    public String getCurrentCur() {
        return driver.getCurrentUrl();
    }

    public void freshPage(){
        driver.navigate().refresh();
    }


    /**
     * Waits for all JS framework requests to finish on page.
     */
    protected void waitForJavascriptFrameworkToFinish() {
        javascriptWait.waitForJavascriptFramework();
    }

    /**
     * @param javascript the Javascript to execute on the current page
     * @return One of Boolean, Long, String, List or WebElement. Or null.
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    protected Object executeJS(String javascript, Object... objects) {
        Object returnObj = null;
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        try {
            returnObj = jsExecutor.executeScript(javascript, objects);
        } catch (Exception e) {
            logger.error("Javascript execution failed!");
            logger.debug("Failed Javascript:" + javascript, e);
        }
        return returnObj;
    }

    /**
     * Execute an asynchronous piece of JavaScript in the context of the
     * currently selected frame or window. Unlike executing synchronous
     * JavaScript, scripts executed with this method must explicitly signal they
     * are finished by invoking the provided callback. This callback is always
     * injected into the executed function as the last argument.
     */
    protected Object executeAsyncJS(String javascript, Object... objects) {
        Object returnObj = null;
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            returnObj = jsExecutor.executeAsyncScript(javascript, objects);
        } catch (Exception e) {
            logger.error("Async Javascript execution failed!");
            logger.debug("Failed Javascript:\n" + javascript, e);
        }
        return returnObj;
    }

    //消息弹框
    public void getRunTimeInfoMessage(String messageType, String message) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Check for jQuery on the page, add it if need be
        js.executeScript("if (!window.jQuery) {"
                + "var jquery = document.createElement('script'); jquery.type = 'text/javascript';"
                + "jquery.src = 'https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js';"
                + "document.getElementsByTagName('head')[0].appendChild(jquery);" + "}");
        Thread.sleep(5000);

        // Use jQuery to add jquery-growl to the page
        js.executeScript("$.getScript('https://the-internet.herokuapp.com/js/vendor/jquery.growl.js')");

        // js.executeScript("$.getScript('/Users/NaveenKhunteta/Documents/workspace/Test/src/testcases/jquery.growl.js')");

        // Use jQuery to add jquery-growl styles to the page
        js.executeScript("$('head').append('<link rel=\"stylesheet\" "
                + "href=\"https://the-internet.herokuapp.com/css/jquery.growl.css\" " + "type=\"text/css\" />');");
        Thread.sleep(5000);

        // jquery-growl w/ no frills
        js.executeScript("$.growl({ title: 'GET', message: '/' });");

        if(messageType.equals("error")){
            js.executeScript("$.growl.error({ title: 'ERROR', message: '"+message+"' });");
        }else if(messageType.equals("info")){
            js.executeScript("$.growl.notice({ title: 'Notice', message: '"+message+"' });");
        }else if(messageType.equals("warning")){
            js.executeScript("$.growl.warning({ title: 'Warning!', message: '"+message+"' });");
        }

        // jquery-growl w/ colorized output
//		js.executeScript("$.growl.error({ title: 'ERROR', message: 'Some exception is coming' });");
//		js.executeScript("$.growl.notice({ title: 'Notice', message: 'your notice message goes here' });");
//		js.executeScript("$.growl.warning({ title: 'Warning!', message: 'your warning message goes here' });");
    }


}
