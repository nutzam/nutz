package org.nutz;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.nutz.lang.Lang;
import org.nutz.resource.Scans;

/**
 * 以多种顺序执行TestCase
 * @author wendal
 *
 */
public class AdvancedTestAll {
    
    public static void main(String[] args) {
        
        //得到所有带@Test的方法
        List<Class<?>> list = Scans.me().scanPackage("org.nutz");
        List<Request> reqs = new ArrayList<Request>();
        Map<Request, Method> reqMap = new HashMap<Request, Method>();
        for (Class<?> clazz : list) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(Test.class) != null) {
                    Request req = Request.method(clazz, method.getName());
                    reqs.add(req);
                    reqMap.put(req , method);
                }
            }
        }
        System.out.printf("Found %d Test Class\n", reqs.size());
        
        //先按普通顺序跑一次
        TestResult simpleTestResult = test(reqs, "normal", reqMap);
        
        //倒序跑一次
        Collections.reverse(reqs);
        TestResult reverseTestResult = test(reqs, "reverse", reqMap);
        
        //乱序跑3次
        Collections.shuffle(reqs);
        TestResult shuffleTestResult_A =  test(reqs, "shuffle_A", reqMap);
        Collections.shuffle(reqs);
        TestResult shuffleTestResult_B =  test(reqs, "shuffle_B", reqMap);
        Collections.shuffle(reqs);
        TestResult shuffleTestResult_C =  test(reqs, "shuffle_C", reqMap);

        System.out.println("正常顺序------------------------------------------------");
        printResult(simpleTestResult);
        System.out.println("反序------------------------------------------------");
        printResult(reverseTestResult);
        System.out.println("乱序A------------------------------------------------");
        printResult(shuffleTestResult_A);
        System.out.println("乱序B------------------------------------------------");
        printResult(shuffleTestResult_B);
        System.out.println("乱序C------------------------------------------------");
        printResult(shuffleTestResult_C);
        
        System.out.println("-------------------------------------------------------Done");
    }
    
    public static TestResult test(List<Request> reqs, String name, Map<Request, Method> reqMap) {
        
        //记录TestCase的顺序,出错时方便查找原因和重现
        // TODO 根据order文件还原测试顺序
        try {
            FileWriter fw = new FileWriter("./test_order_"+name + ".txt");
            for (Request request : reqs) {
                fw.write(reqMap.get(request).toString());
                fw.write("\n");
            }
            fw.flush();
            fw.close();
        }
        catch (IOException e) {}
        
        final TestResult result = new TestResult();
        RunNotifier notifier = new RunNotifier();
        notifier.addListener(new RunListener() {

            public void testFailure(Failure failure) throws Exception {
                result.addError(asTest(failure.getDescription()), failure.getException());
            }

            public void testFinished(Description description)
                    throws Exception {
                result.endTest(asTest(description));
            }

            public void testStarted(Description description)
                    throws Exception {
                result.startTest(asTest(description));
            }
            
            public junit.framework.Test asTest(Description description) {
                return new junit.framework.Test() {
                    
                    public void run(TestResult result) {
                        throw Lang.noImplement();
                    }
                    
                    public int countTestCases() {
                        return 1;
                    }
                };
            }
        });
        for (Request request : reqs) {
            request.getRunner().run(notifier);
        }
        
        return result;
    }
    
    public static void printResult(TestResult result) {
        System.out.printf("Run %d , Fail %d , Error %d \n", result.runCount(), result.failureCount(), result.errorCount());
        
        if (result.failureCount() > 0) {
            Enumeration<TestFailure> enu = result.failures();
            while (enu.hasMoreElements()) {
                TestFailure testFailure = (TestFailure) enu.nextElement();
                System.out.println("--Fail------------------------------------------------");
                System.out.println(testFailure.trace());
                testFailure.thrownException().printStackTrace(System.out);
            }
        }
        
        if (result.errorCount() > 0) {
            Enumeration<TestFailure> enu = result.errors();
            while (enu.hasMoreElements()) {
                TestFailure testFailure = (TestFailure) enu.nextElement();
                System.out.println("--ERROR------------------------------------------------");
                System.out.println(testFailure.trace());
                testFailure.thrownException().printStackTrace(System.out);
            }
        }
    }
}
