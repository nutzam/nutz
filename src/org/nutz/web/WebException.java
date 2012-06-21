package org.nutz.web;

public class WebException extends RuntimeException {

	private static final long serialVersionUID = 3343036182101828118L;

	private String key;

	private String reason;

	public WebException() {
		super();
	}

	public WebException(Throwable cause) {
		super(cause);
	}

	public String getKey() {
		return key;
	}

	public WebException key(String key) {
		this.key = key;
		return this;
	}

	public String getReason() {
		return this.reason;
	}

	public WebException reasonf(String fmt, Object... args) {
		this.reason = String.format(fmt, args);
		return this;
	}

	public WebException reason(Object msg) {
		this.reason = null == msg ? null : msg.toString();
		return this;
	}

}
