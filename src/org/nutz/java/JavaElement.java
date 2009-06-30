package org.nutz.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.nutz.java.Java;
import org.nutz.java.JavaLanguageObject;

public abstract class JavaElement extends JavaLanguageObject implements
		Comparable<JavaLanguageObject> {

	protected JavaElement() {
		this.annotations = new ArrayList<JavaAnnotation>();
		modifier = Modifier.PUBLIC;
	}

	private List<JavaAnnotation> annotations;

	private int modifier;

	public int getModifier() {
		return modifier;
	}

	public JavaElement addModifier(int mod) {
		this.modifier |= mod;
		return this;
	}

	public JavaElement setModifier(int mod) {
		this.modifier = mod;
		return this;
	}

	public String getAccessModifierName() {
		if (Modifier.isPrivate(modifier))
			return "private";
		if (Modifier.isPublic(modifier))
			return "public";
		if (Modifier.isProtected(modifier))
			return "protected";
		return "";
	}

	public String getModifierString() {
		StringBuilder sb = new StringBuilder();
		if (isFinal())
			sb.append("final ");
		if (isStatic()) {
			sb.append("static ");
		}
		sb.append(getAccessModifierName()).append(' ');
		return sb.toString();
	}

	public boolean isStatic() {
		return Modifier.isStatic(modifier);
	}

	public boolean isFinal() {
		return Modifier.isFinal(modifier);
	}

	public JavaElement addAnnotation(Class<? extends Annotation> annType) {
		annotations.add(Java.ann(annType));
		return this;
	}

	public JavaElement addAnnotation(JavaAnnotation jann) {
		annotations.add(jann);
		return this;
	}

	public List<JavaAnnotation> getAnnotations() {
		return annotations;
	}
}
