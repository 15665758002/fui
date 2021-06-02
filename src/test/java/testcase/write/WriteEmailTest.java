package testcase.write;

import common.config.dataProvider.CsvDataProvider;
import common.page.PageFactory;
import common.testLifecycle.BaseUITest;
import org.testng.annotations.Test;
import page.HomePage;
import page.WriteEmailPage;

import static com.google.common.truth.Truth.assertThat;

/**
 * @Description:
 */
public class WriteEmailTest extends BaseUITest {

    @Test(dataProvider = "getData", dataProviderClass = CsvDataProvider.class)
    public void writeEmailTest(String receiver,String title,String emailText) {
        WriteEmailPage writeEmailPage = PageFactory.getPage(HomePage.class).openWriteEmailPage();
        writeEmailPage.inputReceiver(receiver)
                .inputTitle(title)
                .inputEmail(emailText)
                .clickSend();

        assertThat(writeEmailPage.isSuccess()).isTrue();
    }
}
