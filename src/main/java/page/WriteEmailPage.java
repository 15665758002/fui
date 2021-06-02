package page;

import common.basic.Bot;
import common.page.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;

/**
 * @Description:
 */
public class WriteEmailPage extends BasePage<WriteEmailPage> {

    @Name("发送按钮")
    @FindBy(xpath = "//div[@role='toolbar']//span[text()='发送']")
    private WebElement sendButton;

    @Name("收件人输入框")
    @FindBy(xpath = "//input[contains(@aria-label,'收件人地址输入框')]")
    private WebElement receiver;

    @Name("主题")
    @FindBy(xpath = "//div[contains(@aria-label,'邮件主题输入框')]/input")
    private WebElement emailTitle;

    @Name("邮件正文frame")
    @FindBy(className = "APP-editor-iframe")
    private WebElement emailTextIframe;

    @Name("邮件正文文本")
    @FindBy(css = "body>p")
    private WebElement emailText;

    @Name("发送成功文案")
    @FindBy(xpath = "//*[contains(text(),'发送成功')]")
    private WebElement sendSuccess;

    @Step("输入收件人")
    public WriteEmailPage inputReceiver(String receiverStr) {
        receiver.sendKeys(receiverStr);
        return this;
    }

    @Step("输入主题")
    public WriteEmailPage inputTitle(String titleStr) {
        emailTitle.sendKeys(titleStr);
        return this;
    }

    @Step("输入邮件正文")
    public WriteEmailPage inputEmail(String emailTextStr) {
        Bot.switchToIframe(emailTextIframe);
        emailText.sendKeys(emailTextStr);
        Bot.quitIframe();
        return this;
    }

    @Step("点击发送按钮")
    public WriteEmailPage clickSend() {
        sendButton.click();
        return this;
    }

    @Step("是否发送成功")
    public boolean isSuccess() {
        return sendSuccess.isDisplayed();
    }
}
