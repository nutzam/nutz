package org.nutz.aop.asm;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.nutz.aop.AbstractClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Mirror;

/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class AsmClassAgent extends AbstractClassAgent {
    
    static final String MethodArray_FieldName = "_$$Nut_methodArray";
    static final String MethodInterceptorList_FieldName = "_$$Nut_methodInterceptorList";

    @SuppressWarnings("unchecked")
    protected <T> Class<T> generate(ClassDefiner cd,
                                    Pair2[] pair2s,
                                    String newName,
                                    Class<T> klass,
                                    Constructor<T>[] constructors) {
        Method[] methodArray = new Method[pair2s.length];
        List<MethodInterceptor>[] methodInterceptorList = new List[pair2s.length];
        for (int i = 0; i < pair2s.length; i++) {
            Pair2 pair2 = pair2s[i];
            methodArray[i] = pair2.getMethod();
            methodInterceptorList[i] = pair2.getListeners();
        }
        byte[] bytes = ClassY.enhandClass(klass, newName, methodArray, constructors);
        Class<T> newClass = (Class<T>) cd.define(newName, bytes, klass.getClassLoader());
        try {
            Mirror<T> mirror = Mirror.me(newClass);
            mirror.setValue(null, MethodArray_FieldName, methodArray);
            mirror.setValue(null, MethodInterceptorList_FieldName, methodInterceptorList);
        }
        catch (Throwable e) {}
        return newClass;
    }

}
