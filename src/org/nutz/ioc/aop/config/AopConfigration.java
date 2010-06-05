package org.nutz.ioc.aop.config;

import java.util.List;

import org.nutz.ioc.Ioc;

public interface AopConfigration {
	
	String IOCNAME = "$aop";
	
	List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz);
	
}
