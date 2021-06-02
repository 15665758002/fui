package common.listener;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * @Description:
 */
public class TestSuiteListener implements ISuiteListener {

    public void onStart(ISuite suite) {
        System.out.println("线程数："+suite.getXmlSuite().getThreadCount());
        int threadCount = suite.getXmlSuite().getThreadCount();
        System.setProperty("THREADS", String.valueOf(threadCount));
    }
}
