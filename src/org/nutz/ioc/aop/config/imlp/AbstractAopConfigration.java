package org.nutz.ioc.aop.config.imlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.MethodMatcherFactory;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.lang.Lang;

/**
 * 
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public abstract class AbstractAopConfigration implements AopConfigration {

	private List<AopConfigrationItem> aopItemList;

	public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz) {
		List<InterceptorPair> ipList = new ArrayList<InterceptorPair>();
		for (AopConfigrationItem aopItem : aopItemList) {
			if (aopItem.matchClassName(clazz.getName()))
				ipList.add(new InterceptorPair(	getMethodInterceptor(	ioc,
																		aopItem.getInterceptor(),
																		aopItem.isSingleton()),
												MethodMatcherFactory.matcher(aopItem.getMethodName())));
		}
		return ipList;
	}

	public void setAopItemList(List<AopConfigrationItem> aopItemList) {
		this.aopItemList = aopItemList;
	}

	protected MethodInterceptor getMethodInterceptor(	Ioc ioc,
														String interceptorName,
														boolean singleton) {
		System.out.println(interceptorName);
		if (interceptorName.startsWith("ioc:"))
			return ioc.get(MethodInterceptor.class, interceptorName.substring(4));
		try {
			if (singleton == false)
				return (MethodInterceptor) Class.forName(interceptorName).newInstance();
			MethodInterceptor methodInterceptor = cachedMethodInterceptor.get(interceptorName);
			if (methodInterceptor == null) {
				methodInterceptor = (MethodInterceptor) Class	.forName(interceptorName)
																.newInstance();
				cachedMethodInterceptor.put(interceptorName, methodInterceptor);
			}
			return methodInterceptor;
		}
		catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

	private HashMap<String, MethodInterceptor> cachedMethodInterceptor = new HashMap<String, MethodInterceptor>();

}
