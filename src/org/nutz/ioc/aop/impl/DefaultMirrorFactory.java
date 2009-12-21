package org.nutz.ioc.aop.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.MethodMatcher;
import org.nutz.aop.SimpleMethodMatcher;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.lang.Mirror;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class DefaultMirrorFactory implements MirrorFactory {

	private Ioc ioc;

	private ClassDefiner cd;

	public DefaultMirrorFactory(Ioc ioc) {
		this.ioc = ioc;
		this.cd = new DefaultClassDefiner();
	}

	@SuppressWarnings("unchecked")
	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		try {
			return (Mirror<T>) Mirror.me(cd.load(type.getName() + ClassAgent.CLASSNAME_SUFFIX));
		} catch (ClassNotFoundException e) {}
		Mirror<T> mirror = Mirror.me(type);
		List<Method> aops = this.getAopMethod(mirror);
		if (aops.size() < 1)
			return mirror;
		ClassAgent agent = new AsmClassAgent();
		for (Method m : aops) {
			MethodMatcher mm = new SimpleMethodMatcher(m);
			for (String nm : m.getAnnotation(Aop.class).value())
				agent.addListener(mm, ioc.get(MethodInterceptor.class, nm));
		}
		return Mirror.me(agent.define(cd, type));
	}

	private <T> List<Method> getAopMethod(Mirror<T> mirror) {
		List<Method> aops = new LinkedList<Method>();
		for (Method m : mirror.getMethods())
			if (null != m.getAnnotation(Aop.class)) {
				int modify = m.getModifiers();
				if (!Modifier.isAbstract(modify))
					if (!Modifier.isFinal(modify))
						if (!Modifier.isPrivate(modify))
							if (!Modifier.isStatic(modify))
								aops.add(m);
			}
		return aops;
	}
}
