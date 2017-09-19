package org.nutz.ioc.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.matcher.SimpleMethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.AbstractLifeCycle;
import org.nutz.log.Logs;
import org.nutz.log.Log;

/**
 * 简化Aop扩展:
 * <p/>
 * <p>
 * 1. 声明一个自定义注解
 * <p/>
 * <p>
 * 2. 声明一个AopLoader类,继承本类,实现makeIt方法
 * </p>
 * <p>
 * 3. 在@IocBy中引用即可该AopLoader类即可
 * </p>
 * 
 * @author wendal
 *
 * @param <T>
 *            注解类,记得声明@Retention(RetentionPolicy.RUNTIME)哦
 */
public abstract class SimpleAopMaker<T extends Annotation> extends AbstractLifeCycle
        implements IocLoader, AopConfigration {

    private static final Log log = Logs.get();

    protected Class<T> annoClass;

    protected String iocName;

    public String _name() {
        return Strings.lowerFirst(_anno().getSimpleName());
    }

    public Class<T> _anno() {
        return annoClass;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SimpleAopMaker() {
        annoClass = (Class<T>) (Class) Mirror.getTypeParam(getClass(), 0);
        IocBean iocBean = getClass().getAnnotation(IocBean.class);
        if (iocBean != null) {
            if (Strings.isBlank(iocBean.name()))
                iocName = Strings.lowerFirst(getClass().getSimpleName());
            else
                iocName = iocBean.name();
            if (!iocName.startsWith("$aop_"))
                // 如果声明了@IocBean,那么应该用@IocBean(name="$aop_xxx") 不然会有问题
                throw new IocException(iocName,
                                       getClass().getName()
                                                + " using @IocBean but not start with @IocBean(name=\"$aop_xxx\")");
        }
        if (log.isDebugEnabled())
            log.debugf("Load AopConfigure for anno=%s by type=%s",
                       annoClass.getName(),
                       getClass().getName());
    }

    public abstract List<? extends MethodInterceptor> makeIt(T t, Method method, Ioc ioc);

    public boolean checkMethod(Method method) {
        int mod = method.getModifiers();
        if (mod == 0
            || Modifier.isStatic(mod)
            || Modifier.isPrivate(mod)
            || Modifier.isFinal(mod)
            || Modifier.isAbstract(mod))
            return false;
        return true;
    }

    public boolean checkClass(Class<?> klass) {
        return !(klass.isInterface()
                 || klass.isArray()
                 || klass.isEnum()
                 || klass.isPrimitive()
                 || klass.isMemberClass()
                 || klass.isAnnotation()
                 || klass.isAnonymousClass());
    }

    @Override
    public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> klass) {
        if (!checkClass(klass))
            return null;
        List<InterceptorPair> list = new ArrayList<InterceptorPair>();
        for (Method method : getMethods(ioc, klass)) {
            if (!checkMethod(method))
                continue;
            T t = method.getAnnotation(_anno());
            if (t != null) {
                List<? extends MethodInterceptor> _list = makeIt(t, method, ioc);
                if (_list != null) {
                    for (MethodInterceptor mi : _list) {
                        list.add(new InterceptorPair(mi, new SimpleMethodMatcher(method)));
                    }
                }
            }
        }
        if (list.isEmpty())
            return null;
        return list;
    }

    public String[] getName() {
        if (iocName != null)
            return new String[]{iocName};
        return new String[]{"$aop_" + _name()};
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        IocObject iobj = Iocs.wrap(this);
        iobj.setType(getClass());
        IocEventSet events = new IocEventSet();
        events.setDepose("depose");
        events.setCreate("init");
        events.setFetch("fetch");
        iobj.setEvents(events);
        return iobj;
    }

    public boolean has(String name) {
        if (iocName != null)
            return iocName.equals(name);
        return ("$aop_" + _name()).equals(name);
    }
    
    protected Method[] getMethods(Ioc ioc, Class<?> klass) {
        return klass.getDeclaredMethods();
    }
}
