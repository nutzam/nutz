package org.nutz.aop.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nutz.Nutzs;
import org.nutz.aop.ClassAgent;
import org.nutz.aop.asm.test.Aop1;
import org.nutz.aop.asm.test.MyMethodInterceptor;
import org.nutz.aop.matcher.MethodMatcherFactory;
import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;

public class Main2 {

    public static void main(String[] args) throws Throwable {

        ClassAgent agent = new AsmClassAgent();
        agent.addInterceptor(MethodMatcherFactory.matcher(".*"), new MyMethodInterceptor());
        Class<Aop1> classZ = agent.define(Nutzs.cd(), Aop1.class);
        System.out.println(classZ);
        Field[] fields = classZ.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("找到一个Field: " + field);
        }
        Method methods[] = classZ.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println("找到一个Method: " + method);
        }
        Constructor<?>[] constructors = classZ.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println("找个一个Constructor: " + constructor);
        }
        Aop1 a1 = Mirror.me(classZ).born("Wendal");
        a1.nonArgsVoid();
        a1.argsVoid("Wendal is the best!");
        a1.mixObjectsVoid("Arg1", new Object(), 1, null);
        a1.mixArgsVoid("XX", "WendalXXX", 0, 'c', 1L, 9090L);
        a1.mixArgsVoid2("Aop1",
                        Boolean.TRUE,
                        8888,
                        'p',
                        34L,
                        false,
                        'b',
                        "Gp",
                        null,
                        null,
                        23L,
                        90L,
                        78L);
        String result = (String) a1.mixArgsVoid4("WendalXXX");
        System.out.println("返回值: " + result);
        try {
            a1.x();
        }
        catch (Throwable e) {
            // TODO: handle exception
        }
        a1.returnString();
        a1.returnLong();
        a1.returnBoolean();
        a1.returnByte();
        a1.returnChar();
        a1.returnFloat();
        a1.returnShort();
        a1.toString();
        a1.equals(new Object());
        a1.getLog(new StringBuilder("I am OK"));
        try {
            a1.throwError();
        }
        catch (Throwable e) {
            System.out.println("抓住你：");
            e.printStackTrace(System.out);
        }
        a1.returnObjectArray();
        a1.returnLongArray();
        a1.returnBooleanArray();
        a1.returnByteArray();
        a1.returnCharArray();
        a1.returnFloatArray();
        a1.returnShortArray();
        {
            // 带异常的构造函数
            Constructor<?> constructor = a1    .getClass()
                                            .getConstructor(new Class<?>[]{    Object.class,
                                                                            Object.class});
            System.out.println("构造方法:"
                                + constructor
                                + " \n带有的异常:"
                                + Castors.me().castToString(constructor.getExceptionTypes()));
        }
        a1.getRunnable();
        a1.getEnum();
        System.out.println("-Demo Over-");
    }

}
