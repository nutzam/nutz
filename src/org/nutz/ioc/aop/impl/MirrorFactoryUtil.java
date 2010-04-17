package org.nutz.ioc.aop.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class MirrorFactoryUtil {
	
	public static boolean canInterceptor(Method method) {
		int modify = method.getModifiers();
		if (!Modifier.isAbstract(modify))
			if (!Modifier.isFinal(modify))
				if (!Modifier.isPrivate(modify))
					if (!Modifier.isStatic(modify))
						return true;
		return false;
	}

}
