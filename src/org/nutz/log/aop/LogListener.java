package org.nutz.log.aop;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.nutz.aop.MethodListener;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;

public class LogListener implements MethodListener {

	public LogListener(Log log) {
		this.log = log;
	}

	private Log log;
	private int deep;
	private boolean showReturn;
	private int[] showArgs;

	public void setDeep(int deep) {
		this.deep = deep;
	}

	public void setShowReturn(boolean showReturn) {
		this.showReturn = showReturn;
	}

	public void setShowArgs(int[] showArgs) {
		this.showArgs = showArgs;
	}

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		if (showReturn) {
			String re;
			if (method.getReturnType() == void.class)
				re = "void";
			else if (returnObj == null)
				re = "null";
			else
				re = returnObj.toString();
			int w = log.getFormat().getWidth();
			if (w > 0 && re.length() > w)
				re = "\n\t" + re;
			log.printlnf(getPrefix('~') + "%s.%s:%s", getClassName(obj), method.getName(), re);
		}
		return returnObj;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		int w = log.getFormat().getWidth();
		StringBuilder sb = new StringBuilder();
		if (showArgs == null || showArgs.length > 0) {
			for (int i = 0; i < args.length; i++) {
				String s = "$arg" + i;
				if (null != showArgs && showArgs.length > 0) {
					for (int n : showArgs)
						if (n == i) {
							s = argToString(args[i]);
							break;
						}
				} else {
					s = argToString(args[i]);
				}
				if (w > 0 && s.length() > w)
					sb.append("\n\t").append('(').append(s).append(")");
				else
					sb.append('(').append(s).append(')');
			}
		}
		log.printlnf(getPrefix('>') + "%s.%s%s", getClassName(obj), method.getName(), sb);
		return true;
	}

	private String argToString(Object arg) {
		if (null == arg)
			return "null";
		if (arg.getClass().isArray()) {
			StringBuilder sb = new StringBuilder("[L").append(arg.getClass().getComponentType().getName());
			int len = Array.getLength(arg);
			for (int i = 0; i < len; i++) {
				sb.append("\n\t[").append(i).append("] ");
				sb.append(Array.get(arg, i));
			}
			return sb.toString();
		}
		return arg.toString();
	}

	private String getPrefix(char c) {
		return Strings.dup(c, getDeep());
	}

	private String getClassName(Object obj) {
		return obj.getClass().getSuperclass().getSimpleName();
	}

	private int getDeep() {
		return Thread.currentThread().getStackTrace().length - deep;
	}

	public void whenError(Throwable e, Object obj, Method method, Object... args) {
		log.printlnf("! " + Lang.getStackTrace(e));
	}

	public void whenException(Exception e, Object obj, Method method, Object... args) {
		log.printlnf("@ " + Lang.getStackTrace(e));
	}

}
