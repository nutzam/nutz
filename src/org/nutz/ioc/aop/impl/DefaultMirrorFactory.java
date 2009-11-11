package org.nutz.ioc.aop.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.MethodListener;
import org.nutz.aop.MethodMatcher;
import org.nutz.aop.SimpleMethodMatcher;
import org.nutz.aop.javassist.JavassistClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class DefaultMirrorFactory implements MirrorFactory {

	private Ioc ioc;

	public DefaultMirrorFactory(Ioc ioc) {
		this.ioc = ioc;
	}

	private <T> List<Method> getAopMethod(Mirror<T> mirror) {
		List<Method> aops = new LinkedList<Method>();
		for (Method m : mirror.getMethods()) {
			if (null != m.getAnnotation(Aop.class)) {
				aops.add(m);
			}
		}
		return aops;
	}

	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		Mirror<T> mirror = Mirror.me(type);
		List<Method> aops = this.getAopMethod(mirror);
		if (aops.size() > 0) {
			ClassAgent ca = new JavassistClassAgent();
			for (Method m : aops) {
				Aop aop = m.getAnnotation(Aop.class);
				if (null != aop) {
					int mod = m.getModifiers();
					if (Modifier.isPublic(mod) || Modifier.isProtected(mod)) {
						MethodMatcher mm = new SimpleMethodMatcher(m);
						for (String nm : aop.value()) {
							MethodListener ml = ioc.get(MethodListener.class, nm);
							ca.addListener(mm, ml);
						}
					} else {
						throw Lang
								.makeThrow(
										"Method '%s' of class '%s' must be protected or public, if you want to declear @Aop",
										m.getName(), m.getDeclaringClass().getName());
					}
				}
			}
			return Mirror.me(ca.define(type));
		}
		return mirror;
	}
}
