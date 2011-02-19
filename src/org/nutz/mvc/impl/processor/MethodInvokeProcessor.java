package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.Processor;

public class MethodInvokeProcessor implements Processor {

	public boolean process(ActionContext ac) throws Throwable {
		Object module = ac.getModule();
		Method method = ac.getMethod();
		Object[] args = ac.getMethodArgs();
		Object re = method.invoke(module, args);
		ac.setMethodReturn(re);
		return true;
	}

}
