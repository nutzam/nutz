package org.nutz.mvc.impl.processor;

import java.lang.reflect.InvocationTargetException;
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
		try {
			Object re = method.invoke(module, args);
			ac.setMethodReturn(re);
			doNext(ac);
		} 
		catch (IllegalAccessException e) {
			throw e.getCause();
		}
		catch (IllegalArgumentException e) {
			throw e.getCause();
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}
