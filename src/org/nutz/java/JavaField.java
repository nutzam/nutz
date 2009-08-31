package org.nutz.java;

import java.lang.reflect.Modifier;

import org.nutz.java.JavaLanguageObject;
import org.nutz.lang.Strings;

import static java.lang.String.*;

public class JavaField extends JavaElement {

	public JavaField(JavaType type, String name) {
		super();
		this.name = name;
		this.type = type;
		this.setModifier(Modifier.PRIVATE);
	}

	private String name;

	private JavaType type;

	private boolean hasGetter;

	private boolean hasSetter;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JavaType getType() {
		return type;
	}

	public void setType(JavaClass type) {
		this.type = type;
	}

	public boolean isHasGetter() {
		return hasGetter;
	}

	public JavaField setHasGetter(boolean hasGetter) {
		this.hasGetter = hasGetter;
		return this;
	}

	public boolean isHasSetter() {
		return hasSetter;
	}

	public JavaField setHasSetter(boolean hasSetter) {
		this.hasSetter = hasSetter;
		return this;
	}

	public String toString() {
		return renderSource();
	}

	protected String renderSource() {
		StringBuilder sb = new StringBuilder();
		for (JavaAnnotation ann : this.getAnnotations()) {
			sb.append(ann.renderSource()).append("\n");
		}
		sb.append(getModifierString()).append(getType().getFormalName());
		return sb.append(' ').append(name).append(';').toString();
	}

	public String getGetterString() {
		return format("\tpublic %s get%s(){\n\t\treturn this.%s;\n\t}", getType().getName(), Strings
				.capitalize(getName()), getName());
	}

	public String getSetterString() {
		return format("\tpublic void set%s(%s %s){\n\t\tthis.%s=%s;\n\t}", Strings.capitalize(getName()), getType()
				.getName(), getName(), getName(), getName());
	}

	public int compareTo(JavaLanguageObject o) {
		if (o instanceof JavaField)
			return name.compareTo(((JavaField) o).name);
		throw new RuntimeException("Can not compare with " + o.toString());
	}

}
