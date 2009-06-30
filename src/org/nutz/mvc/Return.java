package org.nutz.mvc;

public class Return {

	private Return() {}

	public static Return OK() {
		Return re = new Return();
		re.success = true;
		return re;
	}

	public static Return fail(String ptn, Object... args) {
		Return re = new Return();
		re.success = false;
		re.message = String.format(ptn, args);
		return re;
	}

	private String url;

	private boolean success;

	private String message;

	public Return setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public String getUrl() {
		return url;
	}

	public Return setUrl(String url) {
		this.url = url;
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
