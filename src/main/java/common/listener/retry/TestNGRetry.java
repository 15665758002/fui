package common.listener.retry;

import common.config.Property;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Created by baoan on 2021/4/26.
 * 重试监听器：当用例执行失败后，会执行 retry 方法
 */
public class TestNGRetry implements IRetryAnalyzer {

    int counter = 0;//设置当前的重跑次数
    int retryLimit = Property.MAX_RETRY_COUNT.getIntWithDefault(1);;//设置最大重跑次数。


    @Override
    public boolean retry(ITestResult result) {

        if (counter < retryLimit) {
            counter++;
            return true;
        }
        return false;
    }

    public void reSetCount(){
        counter=0;
    }

}
