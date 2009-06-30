package org.nutz.mvc.annotation;

import java.lang.reflect.Field;

public class Parameter {

	public Parameter(String name, Field field) {
		this.name = name;
		this.field = field;
	}

	private String name;

	private Field field;

	public String getName() {
		return name;
	}

	public Field getField() {
		return field;
	}

}
