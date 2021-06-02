package common.listener;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.testng.*;
import org.testng.collections.Maps;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static common.config.ConfigurationManager.configuration;

public class ReporterListener implements IReporter {

    private static Integer msgCount = 10;
    List<String> groups = new ArrayList();
    Log log = LogFactory.get();


    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        List<String> passList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        List<String> detailList = new ArrayList<>();

        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();

            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();

                Set<ITestResult> passResults = context.getPassedTests().getAllResults();
                List<String> pass = passResults.stream().map(x -> x.getTestClass().getRealClass().getName()).collect(Collectors.toList());
                passList.addAll(pass);
                Set<ITestResult> failResults = context.getFailedTests().getAllResults();
                List<String> fail = failResults.stream().map(x -> x.getTestClass().getRealClass().getName()).collect(Collectors.toList());
                failList.addAll(fail);

            }
            for (String fun:result.keySet()){
                detailList.add(fun.toString()+"失败"+result.get(fun).getTestContext().getFailedTests().getAllResults().size()+"条");
            }
            StringBuffer stringBuffer = new StringBuffer().append("UI自动化测试报告:\n").append("成功").append(passList.size())
                    .append("条:").append("\n失败").append(failList.size()).append("条:\n")
                    .append(detailList).append("\n");

            if (failList.size() > msgCount) {
                stringBuffer.append(failList.subList(0, msgCount).toString()).append("\n").append("担心消息太多，此处省略").append(failList.size() - msgCount).append("条。");
            } else if (failList.size()>0){
                stringBuffer.append(failList.toString());
            }

            stringBuffer.append("\nJenkins地址：").append(configuration().getJenkinsUrl());
            sendDingMsg(stringBuffer.toString());
        }


    }

    private void sendDingMsg(String text) {
        try {

            //是否通知所有人
            boolean isAtAll = false;
            //通知具体人的手机号码列表
            List<String> mobileList = new ArrayList();
//            mobileList.add("15665758002");

            //钉钉机器人消息内容
            String content = text;
            //组装请求内容
            String reqStr = buildReqStr(content, isAtAll, mobileList);
            String post = HttpUtil.post(configuration().getdingUrl(), reqStr);

            //推送消息（http请求）
//            String result = HttpUtil.postJson(dingUrl, reqStr);
//            System.out.println("result == " + post);
            if (post.contains("ok")) {
                System.out.println("钉钉报告发送成功");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private String buildReqStr(String content, boolean isAtAll, List<String> mobileList) {
        //消息内容
        Map<String, String> contentMap = Maps.newHashMap();
        contentMap.put("content", content);

        //通知人
        Map<String, Object> atMap = Maps.newHashMap();
        //1.是否通知所有人
        atMap.put("isAtAll", isAtAll);
        //2.通知具体人的手机号码列表
        atMap.put("atMobiles", mobileList);

        Map<String, Object> reqMap = Maps.newHashMap();
        reqMap.put("msgtype", "text");
        reqMap.put("text", contentMap);
        reqMap.put("at", atMap);

        return JSONUtil.toJsonStr(reqMap);
    }


}
