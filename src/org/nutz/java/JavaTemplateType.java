package org.nutz.java;

public class JavaTemplateType extends JavaType {

	@Override
	public JavaTemplateType setFullName(String name) {
		setName(name);
		return this;
	}

	@Override
	protected String renderSource() {
		return getName();
	}

}
