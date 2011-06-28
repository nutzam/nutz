package org.nutz.el;

public class El2Exception extends RuntimeException {

	private static final long serialVersionUID = -1133638103102657570L;

	public El2Exception(String message, Throwable cause) {
		super(message, cause);
	}

	public El2Exception(String format, Object... args) {
		super(String.format(format, args));
	}

	public El2Exception(String message) {
		super(message);
	}

	public El2Exception(Throwable cause) {
		super(cause);
	}

}
