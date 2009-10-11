package org.nutz.aop;

import static java.lang.reflect.Modifier.TRANSIENT;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.lang.Maths;

public class MethodMatcher {

	private String active;
	private String ignore;
	private int mods;

	public MethodMatcher(String active) {
		this(active, null);
	}

	public MethodMatcher(String active, String ignore) {
		this(active, ignore, Modifier.PUBLIC | Modifier.PROTECTED);
	}

	public MethodMatcher(String active, String ignore, int mods) {
		this.active = active;
		this.ignore = ignore;
		this.mods = mods;
	}

	public boolean match(Method method) {
		int mod = method.getModifiers();
		if (null != ignore)
			if (method.getName().matches(ignore))
				return false;
		if (null != active)
			if (!method.getName().matches(active))
				return false;
		if (mods <= 0)
			return true;

		if (mod == 0)
			mod |= TRANSIENT;

		return Maths.isMask(mod, mods);
	}

}
