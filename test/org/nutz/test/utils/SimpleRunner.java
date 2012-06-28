package org.nutz.test.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.lang.Mirror;

/**
 * 可以传入多个test case作为参数。例如： java org.nutz.test.utils.SimpleRunner
 * org.nutz.log.JdkAdapterPerformanceTest:testCreation
 * org.nutz.log.JdkAdapterPerformanceTest:*
 * 
 * @author Young(sunonfire@gmail.com)
 * 
 */
public class SimpleRunner {

    public static void main(String[] args) throws IllegalArgumentException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException {

        for (String arg : args) {

            int index = arg.indexOf(':');

            String className = null;
            Set<String> methodNames = new HashSet<String>(5);

            if (index == -1) {
                className = arg;
            } else {
                className = arg.substring(0, index);

                String allNames = arg.substring(index + 1);

                if (!"*".equals(allNames)) {
                    String[] methodNameArray = allNames.split(",");

                    for (String m : methodNameArray) {
                        if (m.trim().length() != 0)
                            methodNames.add(m);
                    }
                }
            }

            runClassTest(className, methodNames);
        }
    }

    protected static void runClassTest(String className, Set<String> methodNames)
            throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {

        Mirror<?> mi = Mirror.me(Class.forName(className));

        Method beforeClass = null;
        Method afterClass = null;
        Method before = null;
        Method after = null;
        List<Method> testMethods = new ArrayList<Method>(10);

        Method[] methods = mi.getMethods();

        final boolean methodNamesIsEmptyAtBeginning = methodNames.isEmpty();

        for (Method m : methods) {

            if (m.getName().indexOf("access$") != -1) {
                continue;
            }

            if (m.isAnnotationPresent(BeforeClass.class)) {
                beforeClass = m;
                continue;
            }

            if (m.isAnnotationPresent(AfterClass.class)) {
                afterClass = m;
                continue;
            }

            if (m.isAnnotationPresent(Before.class)) {
                before = m;
                continue;
            }

            if (m.isAnnotationPresent(After.class)) {
                after = m;
                continue;
            }

            if (methodNamesIsEmptyAtBeginning && m.isAnnotationPresent(Test.class)) {
                testMethods.add(m);
                continue;
            }

            if (methodNames.remove(m.getName())) {
                testMethods.add(m);
            }
        }

        if (!methodNames.isEmpty()) {

            System.out.println("Warning!!!: The following methods cannot find in class "
                                + className);

            for (String name : methodNames) {
                System.out.println(name);
            }
        }

        Object caze = mi.born();

        runClassTestInner(beforeClass, afterClass, before, after, testMethods, caze);
    }

    protected static void runClassTestInner(Method beforeClass,
                                            Method afterClass,
                                            Method before,
                                            Method after,
                                            List<Method> testMethods,
                                            Object caze) throws IllegalAccessException,
            InvocationTargetException {

        // System.out.println("Before run test case " +
        // caze.getClass().getName());

        if (beforeClass != null) {
            beforeClass.invoke(null);
        }

        if (afterClass != null) {
            afterClass.invoke(null);
        }

        for (Method test : testMethods) {

            // System.out.println("\tbefore run test " + test.getName());

            if (before != null) {
                before.invoke(caze);
            }

            test.invoke(caze);

            if (after != null)
                after.invoke(caze);
        }
    }
}
