package org.nutz.java;

public class JavaVoidType extends JavaPrimitiveType {

	public JavaVoidType() {
		super();
		super.setFullName("void");
	}

	@Override
	public String toString() {
		return renderSource();
	}

	@Override
	public JavaVoidType setFullName(String name) {
		return this;
	}

	@Override
	protected String renderSource() {
		return getFullName();
	}

}
