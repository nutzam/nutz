package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import org.nutz.mvc.ActionContext;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MethodInvokeProcessor extends AbstractProcessor{

	public void process(ActionContext ac) throws Throwable {
		Object module = ac.getModule();
		Method method = ac.getMethod();
		Object[] args = ac.getMethodArgs();
		Object re = method.invoke(module, args);
		ac.setMethodReturn(re);
		doNext(ac);
	}

}
