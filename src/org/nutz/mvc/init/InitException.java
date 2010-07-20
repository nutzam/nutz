package org.nutz.mvc.init;

@SuppressWarnings("serial")
public class InitException extends RuntimeException {

	public InitException() {
		super();
	}

	public InitException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitException(String message) {
		super(message);
	}

	public InitException(Throwable cause) {
		super(cause);
	}

}
