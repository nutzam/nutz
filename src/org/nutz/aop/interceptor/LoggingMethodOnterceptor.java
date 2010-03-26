package org.nutz.aop.interceptor;

import java.lang.reflect.Method;

import org.nutz.aop.AbstractMethodInterceptor;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class LoggingMethodOnterceptor extends AbstractMethodInterceptor {

	protected boolean logBeforeInvoke = true;
	protected boolean logAfterInvoke = true;
	protected boolean logWhenException = true;
	protected boolean logWhenError = true;
	
	public void setLogEvent(boolean logBeforeInvoke,boolean logAfterInvoke,boolean logWhenException,boolean logWhenError){
		this.logBeforeInvoke = logBeforeInvoke;
		this.logAfterInvoke = logAfterInvoke;
		this.logWhenException = logWhenException;
		this.logWhenError = logWhenError;
	}
	
	@Override
	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		Log log = getLog(obj, method);
		if (logBeforeInvoke && log.isDebugEnabled())
			log.debugf("[beforeInvoke]Obj = %s , Method = %s , args = %s",obj,method,str(args));
		return super.beforeInvoke(obj, method, args);
	}
	
	@Override
	public Object afterInvoke(Object obj, Object returnObj, Method method,
			Object... args) {
		Log log = getLog(obj, method);
		if (logAfterInvoke && log.isDebugEnabled())
			log.debugf("[afterInvoke]Obj = %s , Return = %s , Method = %s , args = %s",obj,returnObj,method,str(args));
		return super.afterInvoke(obj, returnObj, method, args);
	}
	
	@Override
	public boolean whenException(Exception e, Object obj, Method method,Object... args) {
		Log log = getLog(obj, method);
		if (logWhenException && log.isDebugEnabled())
			log.debugf("[whenException]Obj = %s , Throwable = %s , Method = %s , args = %s",obj,e,method,str(args));
		return super.whenException(e, obj, method, args);
	}

	@Override
	public boolean whenError(Throwable e, Object obj, Method method,Object... args) {
		Log log = getLog(obj, method);
		if (logWhenError && log.isDebugEnabled())
			log.debugf("[whenError]Obj = %s , Throwable = %s , Method = %s , args = %s",obj,e,method,str(args));
		return super.whenError(e, obj, method, args);
	}


	protected Log getLog(Object obj,Method method) {
		if (obj == null){
			if (method != null){
				Class<?> klass = method.getClass();
				if (klass != null){
					Class<?> classZ = method.getDeclaringClass();
					if (classZ != null)
						return Logs.getLog(classZ);
				}
			}
			return Logs.getLog(LoggingMethodOnterceptor.class);
		}	
		else
			return Logs.getLog(obj.getClass());
	}
	
	protected final String str(Object... args) {
		if (args == null || args.length == 0)
			return "[]";
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object object : args)
			sb.append(String.valueOf(object)).append(",");
		sb.replace(sb.length()-1, sb.length(), "]");
		return sb.toString();
	}
}
