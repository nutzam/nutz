package org.nutz.ioc.impl;

import org.nutz.aop.MethodListener;
import org.nutz.aop.javassist.JavassistClassAgent;

class AopMatcher {

	private JavassistClassAgent ca;
	private String regex;

	AopMatcher(String key, AopMethod[] aopMethods) {
		regex = key;
		if (null != aopMethods && aopMethods.length > 0)
			for (AopMethod am : aopMethods) {
				ca = new JavassistClassAgent();
				if (null != am.getListeners()) {
					for (MethodListener ml : am.getListeners())
						ca.addListener(am.getMethods(), ml);
				}
			}
	}

	JavassistClassAgent matchType(Class<?> type) {
		if (null == ca)
			return null;
		if (type.getName().matches(regex))
			return ca;
		return null;
	}

	JavassistClassAgent matchName(String name) {
		if (null == ca)
			return null;
		if (name.matches(regex))
			return ca;
		return null;
	}

}
