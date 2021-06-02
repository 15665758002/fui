package common.basic;

import com.github.javafaker.Faker;
import common.testLifecycle.TestLifecycle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Bot {

    private static final Logger logger = LogManager.getLogger();

    /* Driver */
    public static WebDriver driver() {
        return TestLifecycle.get().getWebDriver();
    }
    public static Faker faker = new Faker();

    public static void switchToIframe(WebElement iframe) {
        driver().switchTo().frame(iframe);
    }

    public static void quitIframe() {
        driver().switchTo().defaultContent();
    }

    //返回下一个 选项卡
    public static WebDriver getNextWindow() {
        Set<String> winHandels = driver().getWindowHandles(); // 得到当前窗口的set集合
        List<String> it = new ArrayList<String>(winHandels); // 将set集合存入list对象
        driver().switchTo().window(it.get(1)); // 切换到弹出的新窗口
        waitSomeSecond(1);
        String url=driver().getCurrentUrl(); //获取新窗口的url
        System.out.println(url);
        return driver();
    }

    //返回第一个选项卡页面
    public static void goBackWindow() {
        Set<String> winHandels = driver().getWindowHandles(); // 得到当前窗口的set集合
        List<String> it = new ArrayList<String>(winHandels); // 将set集合存入list对象
        driver().switchTo().window(it.get(0)); // 返回至原页面

    }

    //获取随机数字
    public static String getRandomData() {
        return faker.number().digits(3);
    }
    //获取时间戳
    public static String getTimestampString() {
        String timestamp = String.valueOf(new Date().getTime());
        return timestamp.substring(0,timestamp.length()-3);
    }

    /**
     * 根据文本 选择下拉框的值
     */
    public static void selectValue(List<WebElement> list, String name) {
        boolean flag = false;
        for (WebElement value : list) {
            if (value.getText().equals(name)) {
                value.click();
                flag = true;
                break;
            }
        }
        if (flag == false) {
            throw new RuntimeException("要选择的值不存在");
        }
    }

    public static void selectValueByQueryStr(List<WebElement> list, String name) {
        boolean flag = false;
        for (WebElement value : list) {
            if (value.getAttribute("data-querystring").equals(name)) {
                value.click();
                logger.debug("选择了 data-querystring {} 的下拉框选项", name);
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new RuntimeException("要选择的值不存在");
        }
    }

    //根据 元素的title 选择下拉框的值
    public static void selectValueByTitle(List<WebElement> list, String name) {
        boolean flag = false;
        for (WebElement value : list) {
            if (value.getAttribute("title").contains(name)) {
                value.click();
                logger.debug("选择了 title为 {} 的下拉框选项", name);
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new RuntimeException("要选择的值不存在");
        }
    }

    public static void inputDate(List<WebElement> list, String date) {
        boolean flag = false;
        if (!dateIsOk(date)) {
            throw new RuntimeException("输入的日期格式不正确");
        }
        String inputDate = getStrDate(date);
        for (WebElement value : list) {
            if (value.getAttribute("title").equals(inputDate)) {
                value.click();
                logger.debug("输入日期：{}", inputDate);
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new RuntimeException("要选择的日期不存在");
        }
    }
    /**
     * 1、因为用户输入的日期格式为 yyyy-MM-dd，但是页面日期选择框的值格式为 yyyy年M月d日，需要转换下。
     * @param dateStr：yyyy-MM-dd
     * @return ：yyyy年M月d日
     */
    private static String getStrDate(String dateStr) {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat format2 = new SimpleDateFormat("yyyy年M月d日");
        Date date = null;
        String str = null;

        // String转Date
        str = "2021-11-10";
        try {
            date = format1.parse(dateStr);
            str = format2.format(date);
            System.out.println(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }

    //判断输入的字符串是否是日期格式
    public static boolean dateIsOk(String str) {
        Pattern pattern = Pattern.compile("[0-9]{4}\\-[0-9]{2}-[0-9]{2}");
        return pattern.matcher(str).matches();
    }

    //重新初始化页面对象的元素
    public static void reInitPage(Object page) {
//        PageFactory.initElements(driver(), obj);
        HtmlElementLoader.populatePageObject(page, driver());
    }

    public static void modifyPageLoadStrategy() {
        driver().manage().window().fullscreen();
    }

    /* Click */
    public static void click(WebElement webElement) {
        webElement.click();
    }

    //等待几秒
    public static void waitSomeSecond(int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* 双击 */
    public static void doubleClick(WebElement webElement) {
        Actions action = new Actions(driver());
        action.doubleClick(webElement).perform();
    }

    /* Type */
    public static void type(String text, WebElement webElement) {
        if (text == null) {
            return;
        }
        webElement.sendKeys(text);
    }

    /* Clear */
    public static void clear(WebElement webElement) {
        webElement.clear();
    }

    public static void clearAndType(String text, WebElement webElement) {
        clear(webElement);
        type(text, webElement);
    }

    /* 按回车键 */
    public static void pressEnter(WebElement webElement) {
        pressKeys(webElement, Keys.ENTER);
    }

    /**
     * 1、按 向右箭头，XX次数。用于页面有些表格太宽，有多级滚动条时，滚动页面
     * @param num 按动次数
     */
    public static void pressRight(int num) {
        Actions action = new Actions(driver());
        for (int i = 0; i < num; i++) {
            action.sendKeys(Keys.RIGHT).build().perform();
        }
    }
    /**
     * 1、按 向下箭头，XX次数。用于页面有些表格太长，有多级滚动条时，滚动页面
     * @param num 按动次数
     */
    public static void pressDown(int num) {
        Actions action = new Actions(driver());
        for (int i = 0; i < num; i++) {
            action.sendKeys(Keys.DOWN).build().perform();
        }
    }

    public static void pressKeys(WebElement webElement, CharSequence... keys) {
        webElement.sendKeys(keys);
    }

    /* Open */
    public static void open(String url) {
        driver().get(url);
    }
    /* Navigation */
    public static void navigateBack() {
        driver().navigate().back();
    }

    public static void navigateForward() {
        driver().navigate().forward();
    }

    public static void navigateRefresh() {
        driver().navigate().refresh();
    }


    public static void waitForElementToDisplay(WebElement webElement) {
        waitForElementToDisplay(webElement, 30);
    }

    // 等待某个元素出现
    public static void waitForElementToDisplay(WebElement webElement, long secondsToWait) {
        WebDriverWait wait = new WebDriverWait(driver(), secondsToWait);
        wait.until(ExpectedConditions.visibilityOf(webElement));
    }
    // 等待某个 list元素出现
    public static void waitForElementsToDisplay(List<? extends WebElement> webElements) {
        waitForElementsToDisplay((List<WebElement>) webElements, 30);
    }

    public static void waitForElementsToDisplay(List<? extends WebElement> webElements, long secondsToWait) {
        WebDriverWait wait = new WebDriverWait(driver(), secondsToWait);
        wait.until(ExpectedConditions.visibilityOfAllElements((List<WebElement>) webElements));
    }

    /* Wait Until */
    public static void waitUntil(Predicate<WebDriver> predicate) {
        waitUntil(predicate, 30);
    }

    public static void waitUntil(Predicate<WebDriver> predicate, long secondsToWait) {
        new WebDriverWait(driver(), secondsToWait).until(webDriver -> predicate.test(webDriver));
    }

    /* Scrolling */
//    public static Object scrollTo(WebElement webElement) {
//        if (webElement instanceof WebComponent) {
//            return executeJavascript("arguments[0].scrollIntoView(true);", ((WebComponent) webElement).getWrappedWebElement());
//        }
//        return executeJavascript("arguments[0].scrollIntoView(true);", webElement);
//    }
//
//    public static void scrollToCenter(WebElement webElement) {
//        WebElement target = (webElement instanceof WebComponent)? ((WebComponent) webElement).getWrappedWebElement() : webElement;
//        String js = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
//                + "var elementTop = arguments[0].getBoundingClientRect().top;"
//                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";
//        executeJavascript(js, target);
//    }


    public static Set<String> availableWindowHandles() {
        return driver().getWindowHandles();
    }

    public static String currentWindowHandle() {
        return driver().getWindowHandle();
    }

    public static void switchToWindow(String handle) {
        driver().switchTo().window(handle);
    }

    public static void waitForNewTabToOpen(Set<String> oldWindowHandles) {
        waitForNewTabToOpen(oldWindowHandles, 10);
    }

    public static void waitForNewTabToOpen(Set<String> oldWindowHandles, int seconds) {
        new WebDriverWait(driver(), seconds).until((WebDriver) -> {
            return availableWindowHandles().size() > oldWindowHandles.size();
        });
    }
    /* Browser */
    public static String browser() {
        return ((HasCapabilities) driver()).getCapabilities().getBrowserName();
    }


    // Wait element to be visibility
    // Input: element to be waited
    protected void waitElementVisibility(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver(), 30);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    // Wait element to be clickable
    protected void waitElementClickable(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver(), 30);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // Set implicit wait
    // Input: Number of seconds to be waited
    protected void implicitWaitElement(long seconds){
        driver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }

    // Get text of element
    // Return: string of text
    protected String getTextOfElement(WebElement element) {
        waitElementVisibility(element);
        return element.getText();
    }

    // Click on element
    protected void clickOnElement(WebElement element) {
        waitElementClickable(element);
        element.click();
    }

    // Clear text inside element
    protected void clearTextOfElement(WebElement element) {
        waitElementVisibility(element);
        element.clear();
    }

    // Set value for element
    // Input: value - string to be set
    protected void setValueForElement(WebElement element, String value) {
        clearTextOfElement(element);
        element.sendKeys(value);
    }

    // Select dropdown by text
    // Input: text - string of selected text
    protected void selectDropdownByText(WebElement dropdown, String text) {
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }

    // Select dropdown by index
    // Input: index - index of selected text (int)
    protected void selectDropdownByIndex(WebElement dropdown, int index) {
        Select select = new Select(dropdown);
        select.selectByIndex(index);
    }

    // Wait Alert to be presented
    protected void waitAlertPresent() {
        WebDriverWait wait = new WebDriverWait(driver(), 30);
        wait.until(ExpectedConditions.alertIsPresent());
    }

    // Accept Alert
    protected void acceptAlert() {
        waitAlertPresent();
        Alert alert = driver().switchTo().alert();
        alert.accept();
    }

    // Dismiss Alert
    protected void dismissAlert() {
        waitAlertPresent();
        Alert alert = driver().switchTo().alert();
        alert.dismiss();
    }

    // Set value to Alert
    protected void setValueToAlert(String value) {
        waitAlertPresent();
        Alert alert = driver().switchTo().alert();
        alert.sendKeys(value);
        alert.accept();
    }

    // Scroll to see Element on web
    protected void scrollToSeeElement(WebElement element) {
        waitElementVisibility(element);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Drag Element and Drop it
    protected void dragAndDropElement(WebElement begin, WebElement end) {
        Actions builder = new Actions(driver());
        Action dragAndDrop = builder.clickAndHold(begin).moveToElement(end).release(end).build();
        dragAndDrop.perform();
    }

    // Mouse over click on hamburger button
    protected void mouseOverClick(WebElement firstElement, WebElement secondElement) {
        Actions action = new Actions(driver());
        waitElementVisibility(firstElement);
        action.moveToElement(firstElement).build().perform();
        waitElementVisibility(secondElement);
        secondElement.click();
    }

}
