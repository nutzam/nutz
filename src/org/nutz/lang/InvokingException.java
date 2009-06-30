package org.nutz.lang;

import static java.lang.String.format;

@SuppressWarnings("serial")
public class InvokingException extends RuntimeException {

	public InvokingException(Exception e, Class<?> klass, String methodName, Object[] args) {
		super(makeMessage(e, klass, methodName, args));
	}
	
	public InvokingException(String format, Object... args) {
		super(format(format, args));
	}

	private static String makeMessage(Exception e, Class<?> klass, String methodName, Object[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("Fail to invoke class '").append(klass.getName()).append('\'');
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
