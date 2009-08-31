package org.nutz.aop.javassist.lstn;

import java.lang.reflect.Method;

import org.nutz.aop.MethodListener;
import org.nutz.aop.javassist.meta.Vegetarian;
import org.nutz.castor.Castors;

public class RhinocerosListener implements MethodListener {

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		return null;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		if (Vegetarian.BEH.fight == Castors.me().castTo(args[0], Vegetarian.BEH.class))
			return false;
		return true;
	}

	public void whenError(Throwable e, Object obj, Method method, Object... args) {
	}

	public void whenException(Exception e, Object obj, Method method, Object... args) {
	}

}
