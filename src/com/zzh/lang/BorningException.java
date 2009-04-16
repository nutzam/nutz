package com.zzh.lang;

import static java.lang.String.*;

@SuppressWarnings("serial")
public class BorningException extends RuntimeException {

	public BorningException(Exception e, Class<?> klass, Object[] args) {
		super(makeMessage(e, klass, args));
	}

	public BorningException(String format, Object... args) {
		super(format(format, args));
	}

	private static String makeMessage(Exception e, Class<?> klass, Object[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("Fail to born class '").append(klass.getName()).append('\'');
		if (null != args && args.length > 0) {
			sb.append(" by args: ");
			for (Object arg : args) {
				sb.append("\n").append(arg.toString());
			}
		}
		if (null != e) {
			sb.append("\nfor the reason: ").append(e.toString());
		}
		return sb.toString();
	}

}
