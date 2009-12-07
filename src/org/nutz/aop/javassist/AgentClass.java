package org.nutz.aop.javassist;

import org.nutz.aop.ClassAgent;

public class AgentClass {

	private String newName;
	private String oldName;

	public AgentClass(Class<?> klass) {
		this.oldName = klass.getName();
		this.newName = klass.getName() + ClassAgent.CLASSNAME_SUFFIX;
	}

	public String getOldName() {
		return oldName;
	}

	public String getNewName() {
		return newName;
	}

}
