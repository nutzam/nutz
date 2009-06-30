package org.nutz.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JavaInterface extends JavaType {

	public JavaInterface() {
		super();
		methods = new ArrayList<JavaAbstractMethod>();
	}

	private List<JavaAbstractMethod> methods;

	@Override
	public Set<JavaType> getDependents() {
		Set<JavaType> set = super.getDependents();
		for (JavaMethod m : methods)
			set.addAll(m.getDependents());
		return set;
	}

	@Override
	protected String renderSource() {
		return null;
	}

}
