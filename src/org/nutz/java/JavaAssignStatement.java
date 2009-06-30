package org.nutz.java;

public class JavaAssignStatement extends JavaStatement {

	private String left;
	private String right;

	public String getLeft() {
		return left;
	}

	public JavaAssignStatement setLeft(String left) {
		this.left = left;
		return this;
	}

	public String getRight() {
		return right;
	}

	public JavaAssignStatement setRight(String right) {
		this.right = right;
		return this;
	}

	@Override
	protected String renderSource() {
		return String.format("%s=%s;", left, right);
	}

}
