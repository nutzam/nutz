package org.nutz.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.java.Java;
import org.nutz.lang.Lang;

import static java.lang.String.*;

public class JavaClass extends JavaType {

	public JavaClass() {
		super();
		fields = new ArrayList<JavaField>();
		interfaces = new ArrayList<JavaInterface>();
		constructors = new ArrayList<JavaConstructor>();

	}

	private ArrayList<JavaField> fields;
	private ArrayList<JavaInterface> interfaces;
	private ArrayList<JavaConstructor> constructors;

	public JavaClass addConstructor(JavaParam... params) {
		JavaConstructor c = new JavaConstructor(this);
		for (JavaParam param : params)
			c.addParam(param);
		constructors.add(c);
		return this;
	}

	public JavaClass addConstructor(JavaConstructor c) {
		constructors.add(c);
		return this;
	}

	public ArrayList<JavaConstructor> getConstructors() {
		return constructors;
	}

	public JavaConstructor getDefaultConstructor() {
		for (JavaConstructor c : constructors)
			if (c.getParams().size() == 0)
				return c;
		return null;
	}

	public ArrayList<JavaInterface> getInterfaces() {
		return interfaces;
	}

	public ArrayList<JavaField> getFields() {
		return fields;
	}

	public JavaClass addInterface(JavaInterface inter) {
		interfaces.add(inter);
		return this;
	}

	public JavaClass addInterface(Class<?> type) {
		if (null != type) {
			if (!type.isInterface())
				throw Lang.makeThrow("Type '%s' should be a interface", type.getName());
			interfaces.add(Java.type(JavaInterface.class, type.getName()));
		}
		return this;
	}

	@Override
	public Set<JavaType> getDependents() {
		Set<JavaType> set = super.getDependents();
		if (interfaces.size() > 0)
			for (JavaType jt : interfaces)
				set.addAll(jt.getDependents());
		if (fields.size() > 0)
			for (JavaField jf : fields) {
				if (!jf.getType().isInLang())
					set.add(jf.getType());
				for (JavaAnnotation ann : jf.getAnnotations())
					set.add(ann);
			}
		return set;
	}

	public JavaClass addField(JavaField field) {
		if (null != field)
			fields.add(field);
		return this;
	}

	public JavaClass addField(Class<?> type, String name) {
		return addField(Java.field(type, name));
	}

	public JavaClass addField(JavaType type, String name) {
		return addField(Java.field(type, name));
	}

	@Override
	protected String renderSource() {
		StringBuilder sb = new StringBuilder();
		sb.append("package ").append(getPackageName()).append(';');
		sb.append("\n");
		// imports
		List<JavaType> imports = Java.formatImport(this);
		for (JavaType imp : imports) {
			if (null == imp)
				sb.append("\n");
			else
				sb.append(format("\nimport %s;", imp.getFullName()));
		}
		sb.append("\n");
		// Annotations
		for (JavaAnnotation ann : this.getAnnotations()) {
			sb.append("\n").append(ann.renderSource());
		}
		// body
		sb.append("\n").append(getModifierString()).append("class ").append(getFormalName());
		// extends
		if (null != this.getParent())
			sb.append(" extends ").append(getParent().getFormalName());
		// interfaces
		if (this.getInterfaces().size() > 0) {
			sb.append(" implements ");
			for (JavaInterface ji : this.getInterfaces())
				sb.append(ji.getFormalName()).append(",");
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(" {");
		// constructors
		for (JavaConstructor c : this.getConstructors()) {
			sb.append(Java.tab("\n\n" + c.renderSource()));
		}
		// fields
		for (JavaField jf : this.getFields()) {
			sb.append(Java.tab("\n\n" + jf.renderSource()));
		}
		// methods
		for (JavaMethod m : this.getMethods()) {
			sb.append("\n").append(Java.tab("\n" + Java.renderSource(m)));
		}
		sb.append("\n}");
		return sb.toString();
	}
}
