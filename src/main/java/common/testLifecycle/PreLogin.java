package common.testLifecycle;

import common.basic.Bot;
import common.page.PageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import page.HomePage;
import page.LoginPage;

import static common.config.ConfigurationManager.configuration;

/**
 * @Description:用于浏览器执行用例之前的登录
 */
public class PreLogin {
    private static final Logger logger = LogManager.getLogger();

    private static HomePage homePage;

    public static void preLogin(WebDriver driver) {

        homePage = getHomePage(driver);
        if (!homePage.isLoginSuccess()){
            logger.info("登录失败");
            throw new RuntimeException("登录失败");
        }else {
            logger.info("登录成功");
        }
    }

    private static HomePage getHomePage(WebDriver driver) {
        driver.get(configuration().loginUrl());
        LoginPage loginPage = PageFactory.newInstance(LoginPage.class);

        try {
            loginPage.inputEmail(configuration().getUsername());
        } catch (Exception exception) {
            exception.printStackTrace();
            driver.get(configuration().loginUrl());
            loginPage.inputEmail(configuration().getUsername());
        }

        HomePage homePage = loginPage.inputPssword(configuration().getPassword())
                .clickLogin();
        Bot.waitSomeSecond(1);
        return PageFactory.getPage(HomePage.class);
    }


}
