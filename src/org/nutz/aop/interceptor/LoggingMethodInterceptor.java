package org.nutz.aop.interceptor;

import org.nutz.aop.AopCallback;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 为方法添加Log
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class LoggingMethodInterceptor implements MethodInterceptor {

	private static final Log LOG = Logs.getLog(LoggingMethodInterceptor.class);

	protected boolean logBeforeInvoke = true;
	protected boolean logAfterInvoke = true;
	protected boolean logWhenException = true;
	protected boolean logWhenError = true;

	public void setLogEvent(boolean logBeforeInvoke,
							boolean logAfterInvoke,
							boolean logWhenException,
							boolean logWhenError) {
		this.logBeforeInvoke = logBeforeInvoke;
		this.logAfterInvoke = logAfterInvoke;
		this.logWhenException = logWhenException;
		this.logWhenError = logWhenError;
	}

	public void filter(InterceptorChain chain) {
		try {
			if (LOG.isTraceEnabled())
				LOG.trace("Start ...");
		}
		catch (Throwable e) {}
		try {
			if (logBeforeInvoke && LOG.isDebugEnabled())
				LOG.debugf("[beforeInvoke] Obj = %s , Method = %s , args = %s",
					toString(chain.getCallingObj()),
					chain.getCallingMethod(),
					str(chain.getArgs()));
			chain.doChain();
		}
		catch (Exception e) {
			if (logWhenException && LOG.isDebugEnabled())
				LOG.debugf("[whenException] Obj = %s , Throwable = %s , Method = %s , args = %s",
					toString(chain.getCallingObj()),
					e,
					chain.getCallingMethod(),
					str(chain.getArgs()));
			throw Lang.wrapThrow(e);
		}
		catch (Throwable e) {
			if (logWhenError && LOG.isDebugEnabled())
				LOG.debugf("[whenError] Obj = %s , Throwable = %s , Method = %s , args = %s",
					toString(chain.getCallingObj()),
					e,
					chain.getCallingMethod(),
					str(chain.getArgs()));
			throw Lang.wrapThrow(e);
		}
		finally {
			if (logAfterInvoke && LOG.isDebugEnabled())
				LOG.debugf("[afterInvoke] Obj = %s , Return = %s , Method = %s , args = %s",
					toString(chain.getCallingObj()),
					chain.getReturn(),
					chain.getCallingMethod(),
					str(chain.getArgs()));
		}
		try {
			if (LOG.isTraceEnabled())
				LOG.trace("... End");
		}
		catch (Throwable e) {}
	}

	public static final String toString(Object object) {
		if (object != null )
			if (object instanceof AopCallback)
				return "<Aop>[" + object.getClass().getName() + "]";
		return String.valueOf(object);
	}
	
	protected static final String str(Object... args) {
		if (args == null || args.length == 0)
			return "[]";
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (Object object : args)
			sb.append(toString(object)).append(",");
		sb.replace(sb.length() - 1, sb.length(), "]");
		return sb.toString();
	}

}
