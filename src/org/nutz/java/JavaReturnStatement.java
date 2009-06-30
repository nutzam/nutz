package org.nutz.java;

public class JavaReturnStatement extends JavaStatement {

	private String varName;

	public String getVarName() {
		return varName;
	}

	public JavaReturnStatement setVarName(String varName) {
		this.varName = varName;
		return this;
	}

	@Override
	protected String renderSource() {
		return String.format("return %s;", varName);
	}

}
