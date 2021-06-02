package component;

import io.qameta.allure.Step;
import okhttp3.Headers;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * @Description:
 */
@FindBy(xpath = "//ul[@id='_mail_tab_0_117']")
public class Header extends HtmlElement {

    @Name("首页按钮")
    @FindBy(xpath = "//div[text()='首页']")
    private WebElement homeButton;


    @Step("网页导航栏是否显示")
    public boolean headerDisplay() {
        return homeButton.isDisplayed();
    }

    @Step("返回首页")
    public Header goHome() {
        homeButton.click();
        return this;
    }
}
