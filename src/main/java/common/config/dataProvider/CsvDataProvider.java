package common.config.dataProvider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvDataProvider {


    private static final Logger LOGGER = LoggerFactory.getLogger(CsvDataProvider.class);

    @org.testng.annotations.DataProvider
    public static Object[][] getData(Method method) {
        Annotation[] annotations = method.getAnnotations();
        Boolean isHaveCsvPath = false;
        for (Annotation annotation : annotations) {
            Class c = annotation.annotationType();
            if (c.getSimpleName().equals("Csv")) {
                isHaveCsvPath = true;
                String path = ((Csv) annotation).value();
                if (StringUtils.isEmpty(path)) {
                    throw new RuntimeException("数据驱动路径不对");
                }
                String encoding = ((Csv) annotation).encoding();

                try {
                    return getObjects(path,encoding, method);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    System.out.println("读取数据驱动异常");
                }
            }
        }
        if (!isHaveCsvPath) {
            String path = "/"+method.getDeclaringClass().getName().replace(".","/")+"/"+method.getName()+".csv";
            path = path.replace("testcase", "testdata");
            try {
                return getObjects(path,"utf-8", method);
            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println("读取数据驱动异常");
            }
        }
        throw new RuntimeException("no data");
    }


    private static Object[][] getObjects(String filePath,String encoding, Method method) throws Exception {
        LOGGER.debug("path:{}", new File(".").getAbsolutePath());
        Scanner scanner;

        if (StringUtils.isEmpty(encoding)) {
            scanner = new Scanner(CsvDataProvider.class.getResourceAsStream(filePath));  // 默认自动识别编码的
        } else {
//            scanner = new Scanner(new FileInputStream(filePath), encoding);
            scanner = new Scanner(CsvDataProvider.class.getResourceAsStream(filePath), encoding);  // 默认自动识别编码的

        }
        // 第一行
        String head = scanner.nextLine();
        Class[] paraTypes = method.getParameterTypes();
        int paramCount = paraTypes.length;
        List<Object[]> list = new ArrayList<Object[]>();
        while (scanner.hasNext()) {
            String data = scanner.nextLine();
            String[] array = data.split(",");
            Object[] objects = new Object[paramCount];
            for (int i = 0; i < paramCount; i++) {
                // 类型转换
                objects[i] = convert(paraTypes[i], array[i]);
            }
            list.add(objects);
        }
        return list.toArray(new Object[0][0]);
    }

    private static Object convert(Class paraType, String s) {
        Object object;
        if (paraType.equals(Byte.TYPE) || paraType.equals(Byte.class)) {
            object = Byte.valueOf(s);
        } else if (paraType.equals(Integer.TYPE) || paraType.equals(Integer.class)) {
            object = Integer.valueOf(s);
        } else if (paraType.equals(Short.TYPE) || paraType.equals(Short.class)) {
            object = Short.valueOf(s);
        } else if (paraType.equals(Long.TYPE) || paraType.equals(Long.class)) {
            object = Long.valueOf(s);
        } else if (paraType.equals(Float.TYPE) || paraType.equals(Float.class)) {
            object = Float.valueOf(s);
        } else if (paraType.equals(Double.TYPE) || paraType.equals(Double.class)) {
            object = Double.valueOf(s);
        } else if (paraType.equals(Boolean.TYPE) || paraType.equals(Boolean.class)) {
            object = Boolean.valueOf(s);
        } else if (paraType.equals(Character.TYPE) || paraType.equals(Character.class)) {
            object = s.charAt(0);
        } else if (paraType.equals(String.class)) {
            object = s;
        } else {
            object = s;    //String
        }
        return object;
    }
}
