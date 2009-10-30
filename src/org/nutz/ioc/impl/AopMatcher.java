package org.nutz.ioc.impl;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.MethodListener;

/**
 * @author zozohtnt
 * @author Wendal(wendal1985@gmail.com)
 *
 */
class AopMatcher {

	private ClassAgent ca;
	private String regex;
	
	AopMatcher(String key, AopMethod[] aopMethods) {
		regex = key;
		if (null != aopMethods && aopMethods.length > 0)
			for (AopMethod am : aopMethods) {
				ca = Utils.newDefaultClassAgent();
				if (null != am.getListeners()) {
					for (MethodListener ml : am.getListeners())
						ca.addListener(am.getMethods(), ml);
				}
			}
	}

	ClassAgent matchType(Class<?> type) {
		if (null == ca)
			return null;
		if (type.getName().matches(regex))
			return ca;
		return null;
	}

	ClassAgent matchName(String name) {
		if (null == ca)
			return null;
		if (name.matches(regex))
			return ca;
		return null;
	}

}
