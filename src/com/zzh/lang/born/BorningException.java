package com.zzh.lang.born;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("serial")
public class BorningException extends RuntimeException {

	public BorningException(Throwable e, Class<?> klass, Object[] args) {
		super(makeMessage(e, klass, args));
	}

	private static String makeMessage(Throwable e, Class<?> klass, Object[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("Fail to born class '").append(klass.getName()).append('\'');
		if (null != args && args.length > 0) {
			sb.append("\n by args: [");
			for (Object arg : args)
				sb.append("\n  @(").append(arg.toString()).append(')');
			sb.append("]");
		}
		if (null != e) {
			sb.append(" becasue:\n").append(getExceptionMessage(e));
		}
		return sb.toString();
	}

	private static String getExceptionMessage(Throwable e) {
		if (e instanceof InvocationTargetException) {
			return getExceptionMessage(((InvocationTargetException) e).getTargetException());
		}
		return e.getMessage();
	}

}
