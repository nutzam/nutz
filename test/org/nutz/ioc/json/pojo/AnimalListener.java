package org.nutz.ioc.json.pojo;

import java.lang.reflect.Method;

import org.nutz.aop.MethodInterceptor;

public class AnimalListener implements MethodInterceptor {

	private StringBuilder sb;

	public AnimalListener(StringBuilder sb) {
		this.sb = sb;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		sb.append("B:").append(method.getName()).append(args.length).append(';');
		return true;
	}

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		sb.append("A:").append(method.getName()).append(args.length).append(';');
		return returnObj;
	}

	public boolean whenError(Throwable e, Object obj, Method method, Object... args) {
		sb.append("E:").append(method.getName()).append(';');
		return false;
	}

	public boolean whenException(Exception e, Object obj, Method method, Object... args) {
		sb.append("E:").append(method.getName()).append(';');
		return false;
	}

}
