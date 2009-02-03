package com.zzh.lang;

public class FailToBornException extends RuntimeException {

	private static final long serialVersionUID = 4449325552489666385L;

	/**
	 * @param klass
	 * @param args
	 */
	public FailToBornException(Exception e, Class<?> klass, Object[] args) {
		super(makeMessage(e, klass, args));
	}

	private static String makeMessage(Exception e, Class<?> klass, Object[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("Fail to build class '").append(klass.getName()).append('\'');
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
