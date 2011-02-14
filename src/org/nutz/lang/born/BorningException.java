package org.nutz.lang.born;

import org.nutz.lang.Lang;

@SuppressWarnings("serial")
public class BorningException extends RuntimeException {

	public BorningException(Throwable e, Class<?> type, Object[] args) {
		super(makeMessage(e, type, args));
	}

	private static String makeMessage(Throwable e, Class<?> type, Object[] args) {
		StringBuilder sb = new StringBuilder();
		String name = null == type ? "unknown" : type.getName();
		sb.append("Fail to born '").append(name).append('\'');
		if (null != args && args.length > 0) {
			sb.append("\n by args: [");
			for (Object arg : args)
				sb.append("\n  @(").append(arg).append(')');
			sb.append("]");
		}
		if (null != e) {
			sb.append(" becasue:\n").append(getExceptionMessage(e));
		}
		return sb.toString();
	}

	private static String getExceptionMessage(Throwable e) {
		return Lang.unwrapThrow(e).getMessage();
	}

}
