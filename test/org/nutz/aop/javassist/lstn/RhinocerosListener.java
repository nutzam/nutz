package org.nutz.aop.javassist.lstn;

import java.lang.reflect.Method;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.javassist.meta.Vegetarian;
import org.nutz.castor.Castors;

public class RhinocerosListener implements MethodInterceptor {

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		return null;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		if (Vegetarian.BEH.fight == Castors.me().castTo(args[0], Vegetarian.BEH.class))
			return false;
		return true;
	}

	public boolean whenError(Throwable e, Object obj, Method method, Object... args) {
		return false;
	}

	public boolean whenException(Exception e, Object obj, Method method, Object... args) {
		return false;
	}

}
