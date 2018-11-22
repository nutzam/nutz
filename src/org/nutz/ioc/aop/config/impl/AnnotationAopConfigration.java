package org.nutz.ioc.aop.config.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.aop.SimpleAopMaker;

/**
 * 通过扫描@Aop标注过的Method判断需要拦截哪些方法
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class AnnotationAopConfigration extends SimpleAopMaker<Aop> {

	public List<? extends MethodInterceptor> makeIt(Aop t, Method method, Ioc ioc) {
		List<MethodInterceptor> list = new ArrayList<MethodInterceptor>();
		for (String name : t.value()) {
			list.add(ioc.get(MethodInterceptor.class, name));
		}
		return list;
	}
}
