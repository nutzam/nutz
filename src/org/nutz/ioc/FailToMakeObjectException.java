package org.nutz.ioc;

@SuppressWarnings("serial")
public class FailToMakeObjectException extends RuntimeException {

	public FailToMakeObjectException(String name, Throwable cause) {
		this(name, cause.getMessage());
	}

	public FailToMakeObjectException(String name, String reason) {
		super(String.format("Fail to make object [%s] because:\n%s", name, reason));
	}

	public FailToMakeObjectException() {
		super();
	}

	public FailToMakeObjectException(String message) {
		super(message);
	}

	public FailToMakeObjectException(Throwable cause) {
		super(cause);
	}

}
