package org.nutz.ioc.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.matcher.SimpleMethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

/**
 * 简化Aop扩展:<p/>
 * <p>1. 声明一个自定义注解<p/>
 * <p>2. 声明一个AopLoader类,继承本类,实现makeIt方法</p>
 * <p>3. 在@IocBy中引用即可该AopLoader类即可</p>
 * @author wendal
 *
 * @param <T> 注解类,记得声明@Retention(RetentionPolicy.RUNTIME)哦
 */
public abstract class SimpleAopMaker<T extends Annotation> implements IocLoader, AopConfigration {

	protected Class<T> annoClass;

	public String _name() {
		return Strings.lowerFirst(_anno().getSimpleName());
	}

	public Class<T> _anno() {
		return annoClass;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SimpleAopMaker() {
		annoClass = (Class<T>) (Class)Mirror.getTypeParam(getClass(), 0);
	}

	public abstract List<? extends MethodInterceptor> makeIt(T t, Method method, Ioc ioc);
	
	public boolean checkMethod(Method method) {
		int mod = method.getModifiers();
        if (mod == 0 || Modifier.isStatic(mod) || Modifier.isPrivate(mod) 
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
		for (Method method : klass.getDeclaredMethods()) {
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
		return new String[] { "$aop_" + _name() };
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
		return ("$aop_"+_name()).equals(name);
	}
	
	public void init() throws Exception {}
	public void fetch() throws Exception {}
	public void depose() throws Exception{}
}
