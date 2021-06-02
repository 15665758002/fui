package page;

import common.basic.Bot;
import common.element.annotations.Visible;
import common.page.BasePage;
import common.page.PageFactory;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;

import static common.config.ConfigurationManager.configuration;

/**
 * @Description:
 */
public class LoginPage extends BasePage<LoginPage> {

    @Name("登录页面的iframe")
    @Visible
    @FindBy(css = "div[id='loginDiv']>iframe")
    public WebElement iframe;

    @Name("邮箱账号")
    @FindBy(css = "input[name='email']")
    public WebElement email;

    @Name("密码")
    @FindBy(id = "pwdtext")
    public WebElement password;

    @Name("登录按钮")
    @FindBy(id = "dologin")
    public WebElement loginButton;

    @Step("打开登录页面")
    public LoginPage open() {
        return PageFactory.getPage(
                LoginPage.class, configuration().loginUrl());
    }

    @Step("输入邮箱")
    public LoginPage inputEmail(String emailStr) {
        Bot.switchToIframe(iframe);
        email.clear();
        email.sendKeys(emailStr);
        return this;
    }

    @Step("输入密码")
    public LoginPage inputPssword(String passwordStr) {
        password.clear();
        password.sendKeys(passwordStr);
        return this;
    }

    @Step("点击登录")
    public HomePage clickLogin() {
        loginButton.click();
        return PageFactory.getPage(HomePage.class);
    }
}
