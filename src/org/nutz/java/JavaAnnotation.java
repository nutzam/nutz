package org.nutz.java;

import org.nutz.lang.Mirror;

import static java.lang.String.*;

public class JavaAnnotation extends JavaType{

	private Object value;

	public Object getValue() {
		return value;
	}

	public JavaAnnotation setValue(Object value) {
		this.value = value;
		return this;
	}

	@Override
	protected String renderSource() {
		if (null == value)
			return format("@%s", getName());
		if (Mirror.me(value.getClass()).isStringLike()) {
			return format("@%s(\"%s\")", getName(), value);
		}
		return format("@%s(%s)", getName(), value);
	}

}
