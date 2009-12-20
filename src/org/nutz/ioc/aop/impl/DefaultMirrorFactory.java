package org.nutz.ioc.aop.impl;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.MethodMatcher;
import org.nutz.aop.SimpleMethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.lang.Mirror;
import org.nutz.log.Logs;
import org.nutz.plugin.NoPluginCanWorkException;
import org.nutz.plugin.SimplePluginManager;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class DefaultMirrorFactory implements MirrorFactory {

	private Ioc ioc;
	
	private ClassAgent agent;

	public DefaultMirrorFactory(Ioc ioc) {
		this.ioc = ioc;
		try{
			agent = new SimplePluginManager<ClassAgent>("org.nutz.aop.asm.AsmClassAgent").get();
		}catch (NoPluginCanWorkException e) {
			Logs.getLog(DefaultMirrorFactory.class).warn("No ClassAgent can work." +
					"Aop will be disable!");
		}
	}

	private <T> List<Method> getAopMethod(Mirror<T> mirror) {
		List<Method> aops = new LinkedList<Method>();
		for (Method m : mirror.getMethods())
			if (null != m.getAnnotation(Aop.class))
				aops.add(m);
		return aops;
	}

	public <T> Mirror<T> getMirror(Class<T> type, String name) {
		Mirror<T> mirror = Mirror.me(type);
		List<Method> aops = this.getAopMethod(mirror);
		if(agent == null || aops.size() < 1)
		return mirror;
		for (Method m : aops) {
			MethodMatcher mm = new SimpleMethodMatcher(m);
			for (String nm : m.getAnnotation(Aop.class).value())
					agent.addListener(mm, ioc.get(MethodInterceptor.class, nm));
			}
		return Mirror.me(agent.define(type));
	}
}
