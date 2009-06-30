package org.nutz.java;

public class JavaThrowExceptionStatment extends JavaStatement {

	private String msg;

	public JavaThrowExceptionStatment(String msg) {
		this.msg = msg;
	}

	@Override
	protected String renderSource() {
		return String.format("throw new RuntimeException(\"%s\");", msg);
	}

}
