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

    public String id;

    public ClassAgent addInterceptor(MethodMatcher matcher, MethodInterceptor listener) {
        if (null != listener)
            pairs.add(new Pair(matcher, listener));
        return this;
    }

    public <T> Class<T> define(ClassDefiner cd, Class<T> klass) {
        if (klass.getName().endsWith(CLASSNAME_SUFFIX))
            return klass;
        String newName = klass.getName() + (id == null ? "" : "$" + id) +  CLASSNAME_SUFFIX;
        return define(cd, klass, newName);
    }
    
    public <T> Class<T> define(ClassDefiner cd, Class<T> klass, String newName) {
        Class<T> newClass = try2Load(newName, klass.getClassLoader());
        if (newClass != null)
            return newClass;
        if (!checkClass(klass))
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
        if (cList.isEmpty())
            throw Lang.makeThrow("No non-private constructor founded,unable to create sub-class!");
        return cList.toArray(new Constructor[cList.size()]);
    }

    protected <T> boolean checkClass(Class<T> klass) {
        if (klass == null)
            return false;
        String klassName = klass.getName();
        if (klassName.endsWith(CLASSNAME_SUFFIX))
            return false;
        if (klass.isInterface()
            || klass.isArray()
            || klass.isEnum()
            || klass.isPrimitive()
            || klass.isMemberClass()
            || klass.isAnnotation()
            || klass.isAnonymousClass())
            throw Lang.makeThrow("%s is NOT a Top-Class!Creation FAIL!", klassName);
        if (Modifier.isFinal(klass.getModifiers()) || Modifier.isAbstract(klass.getModifiers()))
            throw Lang.makeThrow("%s is final or abstract!Creation FAIL!", klassName);
        return true;
    }

    @SuppressWarnings("unchecked")
    protected <T> Class<T> try2Load(String newName, ClassLoader loader) {
        try {
            if (loader == null)
                return (Class<T>) getClass().getClassLoader().loadClass(newName);
            return (Class<T>) loader.loadClass(newName);
        }
        catch (ClassNotFoundException e) {
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
            if (!mls.isEmpty())
                p2.add(new Pair2(m, mls));
        }
        return p2.toArray(new Pair2[p2.size()]);
    }

    protected static class Pair {
        MethodMatcher matcher;
        MethodInterceptor listener;

        Pair(MethodMatcher matcher, MethodInterceptor listener) {
            this.matcher = matcher;
            this.listener = listener;
        }
    }

    protected static class Pair2 {
        private Method method;
        private List<MethodInterceptor> listeners;

        Pair2(Method method, List<MethodInterceptor> listeners) {
            this.method = method;
            this.listeners = listeners;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public List<MethodInterceptor> getListeners() {
            return listeners;
        }

        public void setListeners(List<MethodInterceptor> listeners) {
            this.listeners = listeners;
        }
        
    }
}
