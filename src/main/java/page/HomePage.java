package page;

import common.basic.Bot;
import common.page.BasePage;
import common.page.PageFactory;
import component.Header;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;

/**
 * @Description:
 */
public class HomePage extends BasePage<HomePage> {

    @Name("收信按钮")
    @FindBy(xpath = "//span[text()='收 信']")
    private WebElement receiveEmail;

    @Name("写信按钮")
    @FindBy(xpath = "//span[text()='写 信']")
    private WebElement writeEmail;

    private Header header;


    @Step("检查是否登录成功")
    public boolean isLoginSuccess() {
//        Bot.waitSomeSecond(2);
        return header.headerDisplay();
    }

    @Step("进入写信页面")
    public WriteEmailPage openWriteEmailPage() {
        Bot.waitForElementToDisplay(writeEmail);
        writeEmail.click();
        return PageFactory.getPage(WriteEmailPage.class);
    }
}
