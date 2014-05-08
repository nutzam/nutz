package org.nutz.mvc.impl.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nutz.lang.Lang;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.mvc.ActionContext;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MethodInvokeProcessor extends AbstractProcessor{
	
	/** 在入口方法调用时,禁止调用1.b.51新加入的FastClass功能*/
	// PS: 如果这个修改导致异常,请报issue,并将这个变量设置为true
	public static boolean disableFastClassInvoker = Lang.isAndroid;

    public void process(ActionContext ac) throws Throwable {
        Object module = ac.getModule();
        Method method = ac.getMethod();
        Object[] args = ac.getMethodArgs();
        try {
        	if (disableFastClassInvoker)
        		ac.setMethodReturn(method.invoke(module, args));
        	else
        		ac.setMethodReturn(FastClassFactory.get(module.getClass()).invoke(module, method, args));
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
