package org.nutz.aop.interceptor;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class LoggingMethodInterceptor implements MethodInterceptor {
	
	private static final Log LOG = Logs.getLog(LoggingMethodInterceptor.class);
	
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

	public void filter(InterceptorChain chain) {
		try {
			if (logBeforeInvoke && LOG.isDebugEnabled())
				log("[beforeInvoke]Obj = %s , Method = %s , args = %s",chain.getCallingObj(),chain.getCallingMethod(),str(chain.getArgs()));
			chain.doChain();
		}catch (Exception e) {
			if (logWhenException && LOG.isDebugEnabled())
				log("[whenException]Obj = %s , Throwable = %s , Method = %s , args = %s",chain.getCallingObj(),e,chain.getCallingMethod(),str(chain.getArgs()));
			throw Lang.wrapThrow(e);
		} catch (Throwable e) {
			if (logWhenError && LOG.isDebugEnabled())
				log("[whenError]Obj = %s , Throwable = %s , Method = %s , args = %s",chain.getCallingObj(),e,chain.getCallingMethod(),str(chain.getArgs()));
			throw Lang.wrapThrow(e);
		} finally {
			if (logAfterInvoke && LOG.isDebugEnabled())
				log("[afterInvoke]Obj = %s , Return = %s , Method = %s , args = %s",chain.getCallingObj(),chain.getReturn(),chain.getCallingMethod(),str(chain.getArgs()));
		}

	}
	
	protected void log(String str,Object...args){
		LOG.debugf(str,args);
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
