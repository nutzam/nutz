package org.nutz.aop;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * 提供ClassAgent的基础实现,拦截不可能插入Aop代码的Class
 * <p/>
 * 传入的Class对象需要满足的条件
 * <li>不能是final或者abstract的
 * <li>必须有非private的构造函数
 * </p>
 * 被拦截的方法需要满足的条件 <li>不能是final或者abstract的 <li>不是private的
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public abstract class AbstractClassAgent implements ClassAgent {

    private ArrayList<Pair> pairs = new ArrayList<Pair>();

    public ClassAgent addInterceptor(MethodMatcher matcher, MethodInterceptor listener) {
        if (null != listener)
            pairs.add(new Pair(matcher, listener));
        return this;
    }

    public <T> Class<T> define(ClassDefiner cd, Class<T> klass) {
        if (klass.getName().endsWith(CLASSNAME_SUFFIX))
            return klass;
        String newName = klass.getName() + CLASSNAME_SUFFIX;
        Class<T> newClass = try2Load(newName, cd);
        if (newClass != null)
            return newClass;
        if (checkClass(klass) == false)
            return klass;
        Pair2[] pair2s = findMatchedMethod(klass);
        if (pair2s.length == 0)
            return klass;
        Constructor<T>[] constructors = getEffectiveConstructors(klass);
        newClass = generate(cd, pair2s, newName, klass, constructors);
        return newClass;
    }

    protected abstract <T> Class<T> generate(    ClassDefiner cd,
                                                Pair2[] pair2s,
                                                String newName,
                                                Class<T> klass,
                                                Constructor<T>[] constructors);

    @SuppressWarnings("unchecked")
    protected <T> Constructor<T>[] getEffectiveConstructors(Class<T> klass) {
        Constructor<T>[] constructors = (Constructor<T>[]) klass.getDeclaredConstructors();
        List<Constructor<T>> cList = new ArrayList<Constructor<T>>();
        for (int i = 0; i < constructors.length; i++) {
            Constructor<T> constructor = constructors[i];
            if (Modifier.isPrivate(constructor.getModifiers()))
                continue;
            cList.add(constructor);
        }
        if (cList.size() == 0)
            throw Lang.makeThrow("No non-private constructor founded,unable to create sub-class!");
        return cList.toArray(new Constructor[cList.size()]);
    }

    protected <T> boolean checkClass(Class<T> klass) {
        if (klass == null)
            return false;
        String klass_name = klass.getName();
        if (klass_name.endsWith(CLASSNAME_SUFFIX))
            return false;
        if (klass.isInterface()
            || klass.isArray()
            || klass.isEnum()
            || klass.isPrimitive()
            || klass.isMemberClass()
            || klass.isAnnotation()
            || klass.isAnonymousClass())
            throw Lang.makeThrow("%s is NOT a Top-Class!Creation FAIL!", klass_name);
        if (Modifier.isFinal(klass.getModifiers()) || Modifier.isAbstract(klass.getModifiers()))
            throw Lang.makeThrow("%s is final or abstract!Creation FAIL!", klass_name);
        return true;
    }

    @SuppressWarnings("unchecked")
    protected <T> Class<T> try2Load(String newName, ClassDefiner cd) {
        try {
            return (Class<T>) cd.load(newName);
        }
        catch (ClassNotFoundException e) {
            ClassLoader classLoader = getClass().getClassLoader();
            try {
                return (Class<T>) Class.forName(newName, false, classLoader);
            }
            catch (ClassNotFoundException e2) {
                try {
                    return (Class<T>) Lang.loadClass(newName);
                }
                catch (ClassNotFoundException e1) {
                    try {
                        return (Class<T>) classLoader.loadClass(newName);
                    }
                    catch (ClassNotFoundException e3) {}
                }
            }
        }
        return null;
    }

    private <T> Pair2[] findMatchedMethod(Class<T> klass) {
        Method[] all = Mirror.me(klass).getAllDeclaredMethodsWithoutTop();
        List<Pair2> p2 = new ArrayList<Pair2>();
        for (Method m : all) {
            int mod = m.getModifiers();
            if (mod == 0 || Modifier.isStatic(mod) || Modifier.isPrivate(mod) 
                    || Modifier.isFinal(mod)
                    || Modifier.isAbstract(mod))
                continue;
            ArrayList<MethodInterceptor> mls = new ArrayList<MethodInterceptor>();
            for (Pair p : pairs)
                if (p.matcher.match(m))
                    mls.add(p.listener);
            if (mls.size() > 0)
                p2.add(new Pair2(m, mls));
        }
        return p2.toArray(new Pair2[p2.size()]);
    }

    protected static class Pair {
        Pair(MethodMatcher matcher, MethodInterceptor listener) {
            this.matcher = matcher;
            this.listener = listener;
        }

        MethodMatcher matcher;
        MethodInterceptor listener;
    }

    protected static class Pair2 {
        Pair2(Method method, ArrayList<MethodInterceptor> listeners) {
            this.method = method;
            this.listeners = listeners;
        }

        public Method method;
        public ArrayList<MethodInterceptor> listeners;
    }
}
