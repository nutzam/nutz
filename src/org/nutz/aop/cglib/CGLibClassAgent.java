package org.nutz.aop.cglib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.MethodListener;
import org.nutz.aop.MethodMatcher;

/**
 * 基于CGLib的ClassAgent实现
 * <p/>依赖于cglib-nodep-2.2.jar
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class CGLibClassAgent implements ClassAgent {

	private List<Pair> list = new ArrayList<Pair>();

	public ClassAgent addListener(MethodMatcher matcher, MethodListener listener) {
		if (matcher == null || listener == null)
			throw new IllegalArgumentException();
		list.add(new Pair(matcher, listener));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> define(Class<T> klass) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(klass);
		enhancer.setCallback(new CgLibAdviceAdapter(list));
		return enhancer.createClass();
	}
}

class Pair {
	MethodMatcher matcher;
	MethodListener listener;

	public Pair(MethodMatcher matcher, MethodListener listener) {
		this.matcher = matcher;
		this.listener = listener;
	}
}

class CgLibAdviceAdapter implements MethodInterceptor {

	private List<Pair> mList;

	public CgLibAdviceAdapter(List<Pair> mList) {
		this.mList = mList;
	}

	public Object intercept(Object obj, Method method, Object[] objects,
			MethodProxy proxy) throws Throwable {
		List<MethodListener> lst = new ArrayList<MethodListener>();
		for (Pair pair : mList)
			if (pair.matcher.match(method))
				lst.add(pair.listener);
		for (MethodListener methodListener : lst)
			if (!methodListener.beforeInvoke(obj, method, objects))
				return null;
		Object result = null;
		try {
			result = proxy.invokeSuper(obj, objects);
		} catch (Exception e) {
			for (MethodListener methodListener : lst)
				methodListener.whenException(e, obj, method, objects);
			throw e;
		} catch (Throwable e) {
			for (MethodListener methodListener : lst)
				methodListener.whenError(e, obj, method, objects);
			throw e;
		}
		for (MethodListener methodListener : lst)
			result = methodListener.afterInvoke(obj, result, method, objects);
		return result;
	}
}