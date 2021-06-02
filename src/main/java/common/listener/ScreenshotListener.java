package common.listener;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import common.listener.retry.TestNGRetry;
import common.testLifecycle.TestLifecycle;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.assertthat.selenium_shutterbug.core.Capture.FULL;

public class ScreenshotListener extends TestListenerAdapter {

    private static final Logger logger = LogManager.getLogger();
//    private final boolean captureEnabled = ScreenshotCapture.isRequired();

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        tr.getMethod().setRetryAnalyzerClass(TestNGRetry.class);
        TestNGRetry retryAnalyzer = (TestNGRetry) tr.getMethod().getRetryAnalyzer();
        retryAnalyzer.reSetCount();
    }

    @Override
    public void onTestFailure(ITestResult failingTest) {
        //失败，重置失败次数
        super.onTestSuccess(failingTest);
        failingTest.getMethod().setRetryAnalyzerClass(TestNGRetry.class);
        TestNGRetry retryAnalyzer = (TestNGRetry) (failingTest.getMethod().getRetryAnalyzer());
        retryAnalyzer.reSetCount();

        takeScreenshotAndSaveLocally(failingTest.getName());
    }

    @Override
    public void onTestSkipped(ITestResult skippedTest) {
        takeScreenshotAndSaveLocally(skippedTest.getName());
    }

    public void takeScreenshotAndSaveLocally(String testName) {
        takeScreenshotAndSaveLocally(
                testName, (TakesScreenshot) TestLifecycle.get().getWebDriver());
//        takeEntireScreenshotAndSaveLocally(
//                testName, TestLifecycle.get().getWebDriver());
    }

    private void takeEntireScreenshotAndSaveLocally(String testName, WebDriver webDriver) {
        String screenshotDirectory = System.getProperty("screenshotDirectory");
        if (screenshotDirectory == null) {
            screenshotDirectory = "screenshots";
        }
        String fileName = String.format(
                "%s_%s.png",
                System.currentTimeMillis(),
                testName);
        Path screenshotPath = Paths.get(screenshotDirectory);
        Path absolutePath = screenshotPath.resolve(fileName);
        if (createScreenshotDirectory(screenshotPath)) {
            writeEntireScreenshotToFile1(webDriver, absolutePath);
            logger.info("Written screenshot to " + absolutePath);
        } else {
            logger.error("Unable to create " + screenshotPath);
        }
    }

    private void takeScreenshotAndSaveLocally(String testName, TakesScreenshot driver) {
        String screenshotDirectory = System.getProperty("screenshotDirectory");
        if (screenshotDirectory == null) {
            screenshotDirectory = "screenshots";
        }
        String fileName = String.format(
                "%s_%s.png",
                System.currentTimeMillis(),
                testName);
        Path screenshotPath = Paths.get(screenshotDirectory);
        Path absolutePath = screenshotPath.resolve(fileName);
        if (createScreenshotDirectory(screenshotPath)) {
            writeScreenshotToFile(driver, absolutePath);
            logger.info("Written screenshot to " + absolutePath);
        } else {
            logger.error("Unable to create " + screenshotPath);
        }
    }

    private boolean createScreenshotDirectory(Path screenshotDirectory) {
        try {
            Files.createDirectories(screenshotDirectory);
        } catch (IOException e) {
            logger.error("Error creating screenshot directory", e);
        }
        return Files.isDirectory(screenshotDirectory);
    }

    @Attachment(value = "Screenshot on failure", type = "image/png")
    private byte[] writeScreenshotToFile(TakesScreenshot driver, Path screenshot) {
        try (OutputStream screenshotStream = Files.newOutputStream(screenshot)) {
            byte[] bytes = driver.getScreenshotAs(OutputType.BYTES);
            screenshotStream.write(bytes);
            screenshotStream.close();
            return bytes;
        } catch (IOException e) {
            logger.error("Unable to write " + screenshot, e);
        } catch (WebDriverException e) {
            logger.error("Unable to take screenshot.", e);
        }
        return null;
    }

    //滚动截图
    @Attachment(value = "EntireScreenshot", type = "image/png")
    private byte[] writeEntireScreenshotToFile(WebDriver driver, Path screenshot) {
        try (OutputStream screenshotStream = Files.newOutputStream(screenshot)) {

            BufferedImage image = new AShot()
                    .shootingStrategy(ShootingStrategies.simple())
                    .takeScreenshot(driver).getImage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", out);
            } catch (IOException e) {
                //log.error(e.getMessage());
            }

            byte[] bytes = out.toByteArray();
            screenshotStream.write(bytes);
            screenshotStream.close();
            return bytes;
        } catch (IOException e) {
            logger.error("Unable to write " + screenshot, e);
        } catch (WebDriverException e) {
            logger.error("Unable to take screenshot.", e);
        }
        return null;
    }

    //提供多种截图策略
    @Attachment(value = "EntireScreenshot1", type = "image/png")
    private byte[] writeEntireScreenshotToFile1(WebDriver driver, Path screenshot) {
        try (OutputStream screenshotStream = Files.newOutputStream(screenshot)) {

            BufferedImage image = Shutterbug.shootPage(driver, FULL).getImage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", out);
            } catch (IOException e) {
                //log.error(e.getMessage());
            }

            byte[] bytes = out.toByteArray();
            screenshotStream.write(bytes);
            screenshotStream.close();
            return bytes;
        } catch (IOException e) {
            logger.error("Unable to write " + screenshot, e);
        } catch (WebDriverException e) {
            logger.error("Unable to take screenshot.", e);
        }
        return null;
    }

}
