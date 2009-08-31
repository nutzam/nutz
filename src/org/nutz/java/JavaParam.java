package org.nutz.java;

import java.util.Set;

import org.nutz.java.JavaLanguageObject;

public class JavaParam extends JavaElement {

	public JavaParam(JavaType type, String name) {
		super();
		this.type = type;
		this.name = name;
	}

	private JavaType type;

	private String name;

	public JavaType getType() {
		return type;
	}

	public void setType(JavaType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected String renderSource() {
		return type.getName() + " " + name;
	}

	public Set<JavaType> getDependents() {
		return type.getDependents();
	}

	public int compareTo(JavaLanguageObject o) {
		if (o instanceof JavaParam)
			return name.compareTo(((JavaParam) o).name);
		throw new RuntimeException("Can not compare with " + o.toString());
	}

}
