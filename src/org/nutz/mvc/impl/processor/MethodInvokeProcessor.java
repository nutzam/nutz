package org.nutz.mvc.impl.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nutz.lang.Lang;
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
//        	if (Mvcs.disableFastClassInvoker)
        		ac.setMethodReturn(method.invoke(module, args));
//        	else
//        		ac.setMethodReturn(FastClassFactory.get(module.getClass()).invoke(module, method, args));
            doNext(ac);
        } 
        catch (IllegalAccessException e) {
            throw Lang.unwrapThrow(e);
        }
        catch (IllegalArgumentException e) {
            throw Lang.unwrapThrow(e);
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
