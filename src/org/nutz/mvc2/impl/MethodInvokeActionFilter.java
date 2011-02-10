package org.nutz.mvc2.impl;

import java.lang.reflect.Method;

import org.nutz.mvc2.ActionFilterChain;

/**
 * 负责执行特定的方法,以获取方法的返回值
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MethodInvokeActionFilter extends AbstractActionFilter{

	@Override
	public void filter(ActionFilterChain chain) throws Throwable {
		Object obj = chain.get(ActionFilters.methodObj);
		Method method = (Method) chain.get(ActionFilters.method);
		Object[] args = (Object[]) chain.get(ActionFilters.methodArgs);
		Object retutnValue = method.invoke(obj, args);
		chain.put(ActionFilters.returnValue, retutnValue);
		chain.doChain();
	}
	
}
