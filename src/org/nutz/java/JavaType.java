package org.nutz.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.castor.castor.String2Class;
import org.nutz.java.Java;
import org.nutz.java.JavaLanguageObject;
import org.nutz.lang.Lang;

public abstract class JavaType extends JavaElement {

	private JavaType parent;
	private String name;
	private JavaPackage javaPackage;
	private String fullName;
	private List<JavaMethod> methods;
	private List<JavaType> templateParams;

	protected JavaType() {
		super();
		methods = new ArrayList<JavaMethod>();
		templateParams = new ArrayList<JavaType>();
		javaPackage = new JavaPackage("");
	}

	protected void setName(String name) {
		this.name = name;
	}

	public JavaType setFullName(String className) {
		this.fullName = className;
		int pos = className.lastIndexOf('.');
		if (pos < 0) {
			name = className;
			javaPackage = new JavaPackage("");
		} else {
			name = className.substring(pos + 1);
			javaPackage = new JavaPackage(className.substring(0, pos));
		}
		return this;
	}

	public JavaType addTemplateParam(JavaType tp) {
		templateParams.add(tp);
		return this;
	}

	public List<JavaType> getTemplateParams() {
		return templateParams;
	}

	public String getFormalName() {
		StringBuilder sb = new StringBuilder(name);
		if (templateParams.size() > 0) {
			sb.append('<');
			for (int i = 0; i < templateParams.size() - 2; i++)
				sb.append(templateParams.get(i).getFormalName()).append(',');
			sb.append(templateParams.get(templateParams.size() - 1).getFormalName()).append('>');
		}
		return sb.toString();
	}

	public JavaType getParent() {
		return parent;
	}

	public JavaType setParent(JavaType parent) {
		this.parent = parent;
		return this;
	}

	public JavaType setParent(Class<?> klass) {
		JavaType pt = Java.type(klass);
		if (pt instanceof JavaClass)
			setParent(pt);
		throw Lang.makeThrow("Can not extends from '%s' ", klass.getName());
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return this.javaPackage.renderSource();
	}

	public JavaPackage getPackage() {
		return javaPackage;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isTemplate() {
		if (fullName.length() == 1)
			return Character.isUpperCase(getFullName().charAt(0));
		return false;
	}

	public boolean isBoolean() {
		if (null == getFullName())
			return false;
		if (Boolean.class.getName().equals(getFullName()))
			return true;
		if (boolean.class.getName().equals(getFullName()))
			return true;
		return false;
	}

	public boolean isInLang() {
		if (String2Class.map.containsKey(getFullName()))
			return true;
		return getFullName().startsWith("java.lang");
	}

	public List<JavaMethod> getMethods() {
		return methods;
	}

	public JavaType addMethod(JavaMethod method) {
		methods.add(method);
		return this;
	}

	public Set<JavaType> getNamingDependents() {
		Set<JavaType> set = new HashSet<JavaType>();
		set.add(this);
		for (JavaType t : this.getTemplateParams())
			set.addAll(t.getNamingDependents());
		return set;
	}

	public Set<JavaType> getDependents() {
		Set<JavaType> set = new HashSet<JavaType>();
		if (null != this.getParent())
			set.addAll(getParent().getNamingDependents());
		for (JavaMethod m : methods)
			set.addAll(m.getDependents());
		for (JavaAnnotation ann : this.getAnnotations())
			set.add(ann);
		for (JavaType t : this.getTemplateParams())
			set.add(t);
		return set;
	}

	public boolean equals(Object obj) {
		if (obj instanceof JavaType)
			return fullName.equals(((JavaType) obj).fullName);
		return false;
	}

	public int hashCode() {
		return fullName.hashCode();
	}

	public int compareTo(JavaLanguageObject o) {
		if (o instanceof JavaType)
			return fullName.compareTo(((JavaType) o).fullName);
		throw new RuntimeException("Can not compare with " + o.toString());
	}

	public String toString() {
		return getFullName();
	}

}
