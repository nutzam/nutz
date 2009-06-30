package org.nutz.aop.javassist.lstn;

import java.lang.reflect.Method;

import org.nutz.aop.MethodListener;
import org.nutz.aop.javassist.meta.Vegetarian;
import org.nutz.castor.Castors;

public class RhinocerosListener implements MethodListener {

	@Override
	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		return null;
	}

	@Override
	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		if (Vegetarian.BEH.fight == Castors.me().castTo(args[0], Vegetarian.BEH.class))
			return false;
		return true;
	}

	@Override
	public void whenError(Throwable e, Object obj, Method method, Object... args) {}

	@Override
	public void whenException(Exception e, Object obj, Method method, Object... args) {}

}
