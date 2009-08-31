package org.nutz.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.java.Java;
import org.nutz.java.JavaLanguageObject;

public abstract class JavaMethod extends JavaElement {

	public JavaMethod() {
		super();
		this.params = new ArrayList<JavaParam>();
		this.returnType = new JavaVoidType();

	}

	private String name;
	private JavaType returnType;
	private List<JavaParam> params;

	public String getName() {
		return name;
	}

	public JavaMethod setName(String name) {
		this.name = name;
		return this;
	}

	public JavaType getReturnType() {
		return returnType;
	}

	public JavaMethod setReturnType(JavaType returnType) {
		this.returnType = returnType;
		return this;
	}

	public JavaMethod setReturnType(Class<?> type) {
		this.returnType = Java.type(type);
		return this;
	}

	public boolean isReturnVoid() {
		return returnType == null || returnType instanceof JavaVoidType;
	}

	public Set<JavaType> getDependents() {
		HashSet<JavaType> set = new HashSet<JavaType>();
		if (!(returnType instanceof JavaVoidType))
			set.add(returnType);
		for (JavaParam p : params)
			set.add(p.getType());
		return set;
	}

	public String toString() {
		return null;
	}

	public List<JavaParam> getParams() {
		return params;
	}

	public JavaMethod addParam(JavaParam param) {
		params.add(param);
		return this;
	}

	protected StringBuilder createMethodHead() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getModifierString()).append(getReturnType().getName()).append(" ");
		sb.append(getName()).append('(');
		if (getParams().size() > 0) {
			for (JavaParam p : getParams())
				sb.append(p.renderSource()).append(", ");
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(")");
		return sb;
	}

	public int compareTo(JavaLanguageObject o) {
		if (o instanceof JavaMethod)
			return name.compareTo(((JavaMethod) o).name);
		throw new RuntimeException("Can not compare with " + o.toString());
	}

}
