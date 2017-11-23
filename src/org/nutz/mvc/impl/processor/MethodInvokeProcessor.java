package org.nutz.mvc.impl.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nutz.lang.Lang;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MethodInvokeProcessor extends AbstractProcessor{
    
    protected FastMethod fm;
	
	public void process(ActionContext ac) throws Throwable {
        Object module = ac.getModule();
        Method method = ac.getMethod();
        Object[] args = ac.getMethodArgs();
        try {
        	if (Mvcs.disableFastClassInvoker)
        		ac.setMethodReturn(method.invoke(module, args));
        	else {
        	    _check(method);
        		ac.setMethodReturn(fm.invoke(module, args));
        	}
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
	
	protected void _check(Method method) {
	    if (fm != null)
	        return;
	    synchronized (this) {
            if (fm != null)
                return;
            fm = FastClassFactory.get(method);
        }
	}
}
